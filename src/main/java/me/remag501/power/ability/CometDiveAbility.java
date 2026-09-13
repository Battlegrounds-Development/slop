package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CometDiveAbility implements Ability {

    @Override
    public String getId() {
        return "comet_dive";
    }

    @Override
    public String getDisplayName() {
        return "Comet Dive";
    }

    @Override
    public String getDescription() {
        return "Rocket upward then crash forward like a meteor, blasting targets away.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.BLAZE_POWDER;
    }

    @Override
    public int getCooldownSeconds() {
        return 20;
    }

    @Override
    public void activate(Player player) {
        World world = player.getWorld();
        Vector direction = player.getLocation().getDirection().normalize();
        Vector launch = direction.multiply(1.9);
        launch.setY(1.25);
        player.setVelocity(launch);
        player.setFallDistance(0.0F);

        world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.9f, 1.8f);
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 18) {
            double x = Math.cos(angle) * 2.6;
            double z = Math.sin(angle) * 2.6;
            world.spawnParticle(Particle.FLAME, player.getLocation().clone().add(x, 0.2, z), 2, 0.02, 0.02, 0.02, 0.0);
        }

        for (Entity nearby : player.getNearbyEntities(5.0, 3.5, 5.0)) {
            if (!(nearby instanceof LivingEntity target) || target.equals(player)) {
                continue;
            }
            Vector blast = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.9);
            blast.setY(0.95);
            target.setVelocity(blast);
            target.damage(7.0, player);
        }
    }
}

