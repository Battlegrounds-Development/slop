package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Blink ability for Speedster power - short range teleport.
 */
public class BlinkAbility implements Ability {

    @Override
    public String getId() {
        return "blink";
    }

    @Override
    public String getDisplayName() {
        return "Blink";
    }

    @Override
    public String getDescription() {
        return "Teleport forward 10 blocks in the direction you're looking.";
    }

    @Override
    public int getCooldownSeconds() {
        return 10;
    }

    @Override
    public void activate(Player player) {
        Vector direction = player.getLocation().getDirection().normalize().multiply(10);
        player.teleport(player.getLocation().add(direction));
    }

    @Override
    public Material getItemMaterial() {
        return Material.GLOWSTONE_DUST;
    }
}
