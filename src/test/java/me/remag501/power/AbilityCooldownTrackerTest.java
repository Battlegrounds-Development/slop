package me.remag501.power;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbilityCooldownTrackerTest {

    @Test
    void entersAndExpiresCooldown() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        UUID playerId = UUID.randomUUID();
        String abilityId = "test_ability";
        long now = 1_000L;

        tracker.markUsed(playerId, abilityId, now, 5);

        assertTrue(tracker.isOnCooldown(playerId, abilityId, now));
        assertEquals(5_000L, tracker.getRemainingMillis(playerId, abilityId, now));
        assertFalse(tracker.isOnCooldown(playerId, abilityId, now + 5_000L));
    }

    @Test
    void clearRemovesCooldown() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        UUID playerId = UUID.randomUUID();
        String abilityId = "test_ability";

        tracker.markUsed(playerId, abilityId, 2_000L, 10);
        tracker.clear(playerId);

        assertEquals(0L, tracker.getRemainingMillis(playerId, abilityId, 3_000L));
    }
}

