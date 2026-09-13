package me.remag501.power;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityCooldownTracker {

    // Map of (playerId -> (abilityId -> readyAtMillis))
    private final Map<UUID, Map<String, Long>> readyAtByPlayerAndAbility = new HashMap<>();

    /**
     * Gets the remaining cooldown in milliseconds for a specific ability.
     */
    public long getRemainingMillis(UUID playerId, String abilityId, long nowMillis) {
        Map<String, Long> playerCooldowns = readyAtByPlayerAndAbility.getOrDefault(playerId, new HashMap<>());
        long readyAt = playerCooldowns.getOrDefault(abilityId, 0L);
        return Math.max(0L, readyAt - nowMillis);
    }

    /**
     * Checks if a specific ability is on cooldown.
     */
    public boolean isOnCooldown(UUID playerId, String abilityId, long nowMillis) {
        return getRemainingMillis(playerId, abilityId, nowMillis) > 0;
    }

    /**
     * Marks a specific ability as used, setting its cooldown.
     */
    public void markUsed(UUID playerId, String abilityId, long nowMillis, int cooldownSeconds) {
        long cooldownMillis = Math.max(0, cooldownSeconds) * 1000L;
        readyAtByPlayerAndAbility
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(abilityId, nowMillis + cooldownMillis);
    }

    /**
     * Clears all cooldowns for a player.
     */
    public void clear(UUID playerId) {
        readyAtByPlayerAndAbility.remove(playerId);
    }
}

