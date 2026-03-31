package me.remag501.power.ability;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Sky Launch ability for Skybound power.
 */
public class SkyLaunchAbility implements Ability {

    @Override
    public String getId() {
        return "sky_launch";
    }

    @Override
    public String getDisplayName() {
        return "Sky Launch";
    }

    @Override
    public String getDescription() {
        return "Sneak + right-click with an empty hand to launch upward.";
    }

    @Override
    public int getCooldownSeconds() {
        return 10;
    }

    @Override
    public void activate(Player player) {
        Vector launch = player.getVelocity();
        launch.setY(1.05);
        player.setVelocity(launch);
        player.setFallDistance(0.0F);
    }
}

