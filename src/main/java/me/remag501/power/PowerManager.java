package me.remag501.power;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;

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

        return switch (effectKey.toUpperCase(Locale.ROOT)) {
            case "SPEED" -> PotionEffectType.SPEED;
            case "HASTE" -> PotionEffectType.HASTE;
            case "STRENGTH" -> PotionEffectType.STRENGTH;
            case "RESISTANCE" -> PotionEffectType.RESISTANCE;
            case "JUMP_BOOST" -> PotionEffectType.JUMP_BOOST;
            case "SLOW_FALLING" -> PotionEffectType.SLOW_FALLING;
            default -> null;
        };
    }
}

