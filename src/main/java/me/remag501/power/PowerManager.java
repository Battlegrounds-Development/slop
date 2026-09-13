package me.remag501.power;

import me.remag501.power.ability.Ability;
import me.remag501.power.ability.AbilityItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Locale;

public class PowerManager {

    private static final String PLAYER_ROOT = "players";
    private static final int INFINITE_DURATION = Integer.MAX_VALUE;
    private static final int HOTBAR_SIZE = 9;
    private static final int HOTBAR_MODEL_BASE = 2000;

    private final JavaPlugin plugin;
    private final Map<UUID, PowerType> powersByPlayer = new HashMap<>();
    private final Map<UUID, Map<Integer, Ability>> abilityBindings = new HashMap<>(); // playerId -> (hotbarSlot -> ability)
    private final AbilityCooldownTracker cooldownTracker = new AbilityCooldownTracker();

    public PowerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        powersByPlayer.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(PLAYER_ROOT);
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                Optional<PowerType> parsed = PowerType.fromInput(section.getString(key));
                parsed.ifPresent(powerType -> powersByPlayer.put(UUID.fromString(key), powerType));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed player UUID keys in config.
            }
        }
    }

    public void save() {
        plugin.getConfig().set(PLAYER_ROOT, null);
        for (Map.Entry<UUID, PowerType> entry : powersByPlayer.entrySet()) {
            plugin.getConfig().set(PLAYER_ROOT + "." + entry.getKey(), entry.getValue().name());
        }
        plugin.saveConfig();
    }

    public Optional<PowerType> getPower(UUID uuid) {
        return Optional.ofNullable(powersByPlayer.get(uuid));
    }

    public void setPower(Player player, PowerType powerType) {
        powersByPlayer.put(player.getUniqueId(), powerType);
        if (powerType != PowerType.VILTRUMITE) {
            ViltrumiteModeTracker.clear(player);
        }
        applyPower(player, powerType);
        syncAbilityLoadout(player, powerType);
        save();
    }

    public void clearPower(Player player) {
        powersByPlayer.remove(player.getUniqueId());
        abilityBindings.remove(player.getUniqueId());
        cooldownTracker.clear(player.getUniqueId());
        ViltrumiteModeTracker.clear(player);
        clearPowerEffects(player);
        clearAbilityItems(player);
        save();
    }

    public void reapplyPower(Player player) {
        Optional<PowerType> powerType = getPower(player.getUniqueId());
        clearPowerEffects(player);
        if (powerType.isPresent()) {
            applyPower(player, powerType.get());
            syncAbilityLoadout(player, powerType.get());
            if (powerType.get() != PowerType.VILTRUMITE) {
                ViltrumiteModeTracker.clear(player);
            }
        } else {
            abilityBindings.remove(player.getUniqueId());
            ViltrumiteModeTracker.clear(player);
            clearAbilityItems(player);
        }
    }

    public String listPowerKeys() {
        StringBuilder builder = new StringBuilder();
        for (PowerType type : PowerType.values()) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(type.getKey());
        }
        return builder.toString();
    }

    /**
     * Binds an ability to a hotbar slot for a player.
     */
    public void bindAbility(Player player, Ability ability, int hotbarSlot) {
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            throw new IllegalArgumentException("Hotbar slot must be 0-8");
        }
        abilityBindings
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(hotbarSlot, ability);
    }

    /**
     * Unbinds an ability from a hotbar slot.
     */
    public void unbindAbility(Player player, int hotbarSlot) {
        Map<Integer, Ability> playerBindings = abilityBindings.get(player.getUniqueId());
        if (playerBindings != null) {
            playerBindings.remove(hotbarSlot);
            if (playerBindings.isEmpty()) {
                abilityBindings.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Gets the ability bound to a hotbar slot, if any.
     */
    public Optional<Ability> getBoundAbility(Player player, int hotbarSlot) {
        Map<Integer, Ability> playerBindings = abilityBindings.get(player.getUniqueId());
        if (playerBindings == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(playerBindings.get(hotbarSlot));
    }

    /**
     * Gets all abilities bound by a player.
     */
    public Map<Integer, Ability> getBoundAbilities(Player player) {
        return Map.copyOf(abilityBindings.getOrDefault(player.getUniqueId(), Map.of()));
    }

    /**
     * Triggers a specific bound ability by hotbar slot.
     */
    public AbilityTriggerResult triggerBoundAbility(Player player, int hotbarSlot) {
        Optional<Ability> bound = getBoundAbility(player, hotbarSlot);
        if (bound.isEmpty()) {
            return AbilityTriggerResult.noPower();
        }

        Ability ability = bound.get();
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        long remaining = cooldownTracker.getRemainingMillis(playerId, ability.getId(), now);

        if (remaining > 0) {
            long remainingSeconds = Math.max(1L, (remaining + 999L) / 1000L);
            return AbilityTriggerResult.cooldown(null, ability, (int) remainingSeconds);
        }

        ability.activate(player);
        cooldownTracker.markUsed(playerId, ability.getId(), now, getEffectiveCooldownSeconds(player, ability));
        return AbilityTriggerResult.success(null, ability);
    }

    /**
     * Triggers an ability for a player. If they have multiple abilities,
     * this will trigger the first available ability that's off cooldown.
     * (Legacy method - prefer triggerBoundAbility for hotbar-based triggering)
     */
    public AbilityTriggerResult triggerAbility(Player player) {
        Optional<PowerType> powerType = getPower(player.getUniqueId());
        if (powerType.isEmpty()) {
            return AbilityTriggerResult.noPower();
        }

        PowerType selected = powerType.get();
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();

        // Try to trigger the first available ability
        for (Ability ability : selected.getAbilities()) {
            long remaining = cooldownTracker.getRemainingMillis(playerId, ability.getId(), now);
            if (remaining <= 0) {
                ability.activate(player);
                cooldownTracker.markUsed(playerId, ability.getId(), now, getEffectiveCooldownSeconds(player, ability));
                return AbilityTriggerResult.success(selected, ability);
            }
        }

        // All abilities are on cooldown, return the one with shortest remaining cooldown
        Ability firstAbility = selected.getAbilities().get(0);
        long remaining = cooldownTracker.getRemainingMillis(playerId, firstAbility.getId(), now);
        long remainingSeconds = Math.max(1L, (remaining + 999L) / 1000L);
        return AbilityTriggerResult.cooldown(selected, firstAbility, (int) remainingSeconds);
    }

    /**
     * Gets remaining cooldown seconds for an ability bound to a specific hotbar slot.
     */
    public int getRemainingCooldownSeconds(Player player, int hotbarSlot) {
        Optional<Ability> bound = getBoundAbility(player, hotbarSlot);
        if (bound.isEmpty()) {
            return 0;
        }
        return getRemainingCooldownSeconds(player, bound.get());
    }

    /**
     * Gets remaining cooldown seconds for an ability.
     */
    public int getRemainingCooldownSeconds(Player player, Ability ability) {
        long remaining = cooldownTracker.getRemainingMillis(player.getUniqueId(), ability.getId(), System.currentTimeMillis());
        if (remaining <= 0) {
            return 0;
        }
        return (int) Math.max(1L, (remaining + 999L) / 1000L);
    }

    /**
     * Returns true when the player has at least one bound ability.
     */
    public boolean hasBoundAbilities(Player player) {
        return !getBoundAbilities(player).isEmpty();
    }

    /**
     * Returns true if this player currently has city-breaker mode active.
     */
    public boolean isCityBreakerActive(Player player) {
        return ViltrumiteModeTracker.isActive(player);
    }

    private void applyPower(Player player, PowerType powerType) {
        clearPowerEffects(player);
        for (PowerType.EffectSpec effect : powerType.getEffects()) {
            PotionEffectType effectType = resolveEffectType(effect.effectType());
            if (effectType != null) {
                player.addPotionEffect(new PotionEffect(effectType, INFINITE_DURATION, effect.amplifier(), false, false, true));
            }
        }
    }

    private void clearPowerEffects(Player player) {
        Set<PotionEffectType> managedEffectTypes = new HashSet<>();
        for (PowerType powerType : PowerType.values()) {
            for (PowerType.EffectSpec effect : powerType.getEffects()) {
                PotionEffectType effectType = resolveEffectType(effect.effectType());
                if (effectType != null) {
                    managedEffectTypes.add(effectType);
                }
            }
        }
        managedEffectTypes.forEach(player::removePotionEffect);
    }

    private void syncAbilityLoadout(Player player, PowerType powerType) {
        Map<Integer, Ability> slotBindings = new HashMap<>();
        int slot = 0;
        for (Ability ability : powerType.getAbilities()) {
            if (slot >= HOTBAR_SIZE) {
                break;
            }
            slotBindings.put(slot, ability);
            player.getInventory().setItem(slot, AbilityItemBuilder.buildAbilityHotbarItem(ability, slot));
            slot++;
        }
        abilityBindings.put(player.getUniqueId(), slotBindings);

        // Remove stale plugin ability items from unused hotbar slots.
        for (int i = slot; i < HOTBAR_SIZE; i++) {
            ItemStack existing = player.getInventory().getItem(i);
            if (isAbilityHotbarItem(existing)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    private void clearAbilityItems(Player player) {
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            ItemStack existing = player.getInventory().getItem(i);
            if (isAbilityHotbarItem(existing)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    private boolean isAbilityHotbarItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null || !item.getItemMeta().hasCustomModelData()) {
            return false;
        }
        int model = item.getItemMeta().getCustomModelData();
        return model >= HOTBAR_MODEL_BASE && model < HOTBAR_MODEL_BASE + HOTBAR_SIZE;
    }

    private PotionEffectType resolveEffectType(String effectKey) {
        if (effectKey == null) {
            return null;
        }

        String canonical = switch (effectKey.toUpperCase(Locale.ROOT)) {
            case "HASTE", "FAST_DIGGING" -> "HASTE";
            case "STRENGTH", "INCREASE_DAMAGE" -> "STRENGTH";
            case "RESISTANCE", "DAMAGE_RESISTANCE" -> "RESISTANCE";
            case "JUMP_BOOST", "JUMP" -> "JUMP_BOOST";
            default -> effectKey.toUpperCase(Locale.ROOT);
        };

        PotionEffectType type = PotionEffectType.getByName(canonical);
        if (type != null) {
            return type;
        }

        return PotionEffectType.getByName(effectKey.toUpperCase(Locale.ROOT));
    }

    private int getEffectiveCooldownSeconds(Player player, Ability ability) {
        int base = Math.max(1, ability.getCooldownSeconds());
        Optional<PowerType> selected = getPower(player.getUniqueId());
        if (selected.isEmpty() || selected.get() != PowerType.VILTRUMITE) {
            return base;
        }
        if (!ViltrumiteModeTracker.isActive(player)) {
            return base;
        }
        if ("city_breaker_mode".equals(ability.getId())) {
            return base;
        }
        return Math.max(1, (int) Math.floor(base * 0.55));
    }

    public record AbilityTriggerResult(Status status, PowerType powerType, Ability ability, int remainingSeconds) {
        public static AbilityTriggerResult noPower() {
            return new AbilityTriggerResult(Status.NO_POWER, null, null, 0);
        }

        public static AbilityTriggerResult cooldown(PowerType powerType, Ability ability, int remainingSeconds) {
            return new AbilityTriggerResult(Status.COOLDOWN, powerType, ability, remainingSeconds);
        }

        public static AbilityTriggerResult success(PowerType powerType, Ability ability) {
            return new AbilityTriggerResult(Status.SUCCESS, powerType, ability, 0);
        }
    }

    public enum Status {
        NO_POWER,
        COOLDOWN,
        SUCCESS
    }
}

