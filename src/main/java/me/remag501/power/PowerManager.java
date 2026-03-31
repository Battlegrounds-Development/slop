package me.remag501.power;

import me.remag501.power.ability.Ability;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
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

    private final JavaPlugin plugin;
    private final Map<UUID, PowerType> powersByPlayer = new HashMap<>();
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
        applyPower(player, powerType);
        save();
    }

    public void clearPower(Player player) {
        powersByPlayer.remove(player.getUniqueId());
        cooldownTracker.clear(player.getUniqueId());
        clearPowerEffects(player);
        save();
    }

    public void reapplyPower(Player player) {
        Optional<PowerType> powerType = getPower(player.getUniqueId());
        clearPowerEffects(player);
        powerType.ifPresent(type -> applyPower(player, type));
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
     * Triggers an ability for a player. If they have multiple abilities,
     * this will trigger the first available ability that's off cooldown.
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
                // Ability is off cooldown, activate it
                ability.activate(player);
                cooldownTracker.markUsed(playerId, ability.getId(), now, ability.getCooldownSeconds());
                return AbilityTriggerResult.success(selected, ability);
            }
        }

        // All abilities are on cooldown, return the one with shortest remaining cooldown
        Ability firstAbility = selected.getAbilities().get(0);
        long remaining = cooldownTracker.getRemainingMillis(playerId, firstAbility.getId(), now);
        long remainingSeconds = Math.max(1L, (remaining + 999L) / 1000L);
        return AbilityTriggerResult.cooldown(selected, firstAbility, (int) remainingSeconds);
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

