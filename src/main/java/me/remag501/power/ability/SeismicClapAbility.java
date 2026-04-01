package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SeismicClapAbility implements Ability {

    @Override
    public String getId() {
        return "seismic_clap";
    }

    @Override
    public String getDisplayName() {
        return "Seismic Clap";
    }

    @Override
    public String getDescription() {
        return "Emit a devastating pressure wave that ragdolls everything around you.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.NETHERITE_INGOT;
    }

    @Override
    public int getCooldownSeconds() {
        return 13;
    }

    @Override
    public void activate(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.3f, 0.7f);

        for (double radius = 1.5; radius <= 7.5; radius += 1.0) {
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 20) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().clone().add(x, 0.15, z), 1, 0.0, 0.0, 0.0, 0.0);
                player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation().clone().add(x, 0.15, z), 1, 0.0, 0.0, 0.0, 0.0, Material.STONE.createBlockData());
            }
        }

        for (Entity nearby : player.getNearbyEntities(8.0, 5.0, 8.0)) {
            if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                continue;
            }

            Vector launch = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(2.3);
            launch.setY(1.05);
            target.setVelocity(launch);
            target.damage(12.0, player);
        }
    }
}

