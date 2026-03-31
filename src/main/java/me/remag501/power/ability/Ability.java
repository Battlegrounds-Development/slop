package me.remag501.power.ability;

import org.bukkit.entity.Player;

/**
 * Represents an ability that can be triggered by a player with a power.
 */
public interface Ability {

    /**
     * Gets the unique identifier for this ability.
     */
    String getId();

    /**
     * Gets the display name for this ability.
     */
    String getDisplayName();

    /**
     * Gets the description of what this ability does.
     */
    String getDescription();

    /**
     * Gets the cooldown in seconds for this ability.
     */
    int getCooldownSeconds();

    /**
     * Activates the ability for the given player.
     */
    void activate(Player player);
}

