package me.remag501.power.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Ground Slam ability for Titan power - AOE damage and knockup.
 */
public class GroundSlamAbility implements Ability {

    @Override
    public String getId() {
        return "ground_slam";
    }

    @Override
    public String getDisplayName() {
        return "Ground Slam";
    }

    @Override
    public String getDescription() {
        return "Slam the ground, damaging and launching enemies in all directions.";
    }

    @Override
    public int getCooldownSeconds() {
        return 16;
    }

    @Override
    public void activate(Player player) {
        for (Entity nearby : player.getNearbyEntities(5.0, 3.0, 5.0)) {
            if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                continue;
            }

            var knockVelocity = target.getLocation().toVector()
                    .subtract(player.getLocation().toVector())
                    .normalize()
                    .multiply(1.5);
            knockVelocity.setY(0.8);
            target.setVelocity(knockVelocity);
            target.damage(6.0, player);
        }
    }
}

