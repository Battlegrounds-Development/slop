package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Dash ability for Speedster power.
 */
public class DashAbility implements Ability {

    @Override
    public String getId() {
        return "dash";
    }

    @Override
    public String getDisplayName() {
        return "Dash";
    }

    @Override
    public String getDescription() {
        return "Sneak + right-click with an empty hand to surge forward.";
    }

    @Override
    public int getCooldownSeconds() {
        return 8;
    }

    @Override
    public void activate(Player player) {
        Vector dash = player.getLocation().getDirection().normalize().multiply(1.45);
        dash.setY(Math.max(0.24, dash.getY()));
        player.setVelocity(dash);
    }

    @Override
    public Material getItemMaterial() {
        return Material.SUGAR;
    }
}
