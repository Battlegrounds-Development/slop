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
        long now = 1_000L;

        tracker.markUsed(playerId, now, 5);

        assertTrue(tracker.isOnCooldown(playerId, now));
        assertEquals(5_000L, tracker.getRemainingMillis(playerId, now));
        assertFalse(tracker.isOnCooldown(playerId, now + 5_000L));
    }

    @Test
    void clearRemovesCooldown() {
        AbilityCooldownTracker tracker = new AbilityCooldownTracker();
        UUID playerId = UUID.randomUUID();

        tracker.markUsed(playerId, 2_000L, 10);
        tracker.clear(playerId);

        assertEquals(0L, tracker.getRemainingMillis(playerId, 3_000L));
    }
}

