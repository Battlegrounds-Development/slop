package me.remag501.power;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks temporary Viltrumite city-breaker state for cooldown and power scaling.
 */
public final class ViltrumiteModeTracker {

    private static final Map<UUID, Long> ACTIVE_UNTIL_MILLIS = new ConcurrentHashMap<>();

    private ViltrumiteModeTracker() {
    }

    public static void activate(Player player, int durationTicks) {
        long durationMillis = Math.max(1, durationTicks) * 50L;
        ACTIVE_UNTIL_MILLIS.put(player.getUniqueId(), System.currentTimeMillis() + durationMillis);
    }

    public static boolean isActive(Player player) {
        return isActive(player.getUniqueId());
    }

    public static boolean isActive(UUID playerId) {
        long now = System.currentTimeMillis();
        long until = ACTIVE_UNTIL_MILLIS.getOrDefault(playerId, 0L);
        if (until <= now) {
            ACTIVE_UNTIL_MILLIS.remove(playerId);
            return false;
        }
        return true;
    }

    public static void clear(Player player) {
        ACTIVE_UNTIL_MILLIS.remove(player.getUniqueId());
    }
}

