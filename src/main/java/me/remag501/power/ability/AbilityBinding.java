package me.remag501.power.ability;

import java.util.Objects;

/**
 * Represents an ability bound to a player's hotbar slot.
 */
public record AbilityBinding(String abilityId, int hotbarSlot) {

    public AbilityBinding {
        Objects.requireNonNull(abilityId, "abilityId cannot be null");
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            throw new IllegalArgumentException("Hotbar slot must be 0-8");
        }
    }
}

