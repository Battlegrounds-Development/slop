package me.remag501.power;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityCooldownTracker {

    private final Map<UUID, Long> readyAtByPlayer = new HashMap<>();

    public long getRemainingMillis(UUID playerId, long nowMillis) {
        long readyAt = readyAtByPlayer.getOrDefault(playerId, 0L);
        return Math.max(0L, readyAt - nowMillis);
    }

    public boolean isOnCooldown(UUID playerId, long nowMillis) {
        return getRemainingMillis(playerId, nowMillis) > 0;
    }

    public void markUsed(UUID playerId, long nowMillis, int cooldownSeconds) {
        long cooldownMillis = Math.max(0, cooldownSeconds) * 1000L;
        readyAtByPlayer.put(playerId, nowMillis + cooldownMillis);
    }

    public void clear(UUID playerId) {
        readyAtByPlayer.remove(playerId);
    }
}

