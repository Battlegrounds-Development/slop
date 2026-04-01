package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Shockwave ability for Titan power.
 */
public class ShockwaveAbility implements Ability {

    @Override
    public String getId() {
        return "shockwave";
    }

    @Override
    public String getDisplayName() {
        return "Shockwave";
    }

    @Override
    public String getDescription() {
        return "Sneak + right-click with an empty hand to knock nearby enemies away.";
    }

    @Override
    public int getCooldownSeconds() {
        return 14;
    }

    @Override
    public Material getItemMaterial() {
        return Material.IRON_NUGGET;
    }

    @Override
    public void activate(Player player) {
        for (Entity nearby : player.getNearbyEntities(4.0, 2.0, 4.0)) {
            if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                continue;
            }

            Vector knock = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.2);
            knock.setY(0.45);
            target.setVelocity(knock);
            target.damage(4.0, player);
        }
    }
}
