package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NovaBurstAbility implements Ability {

    @Override
    public String getId() {
        return "nova_burst";
    }

    @Override
    public String getDisplayName() {
        return "Nova Burst";
    }

    @Override
    public String getDescription() {
        return "Collapse then detonate space around you, launching enemies in every direction.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.NETHER_STAR;
    }

    @Override
    public int getCooldownSeconds() {
        return 18;
    }

    @Override
    public void activate(Player player) {
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.2f, 1.3f);

        // Three-layer particle sphere for a flashy blast core.
        for (double y = -1.5; y <= 1.5; y += 0.35) {
            double radius = Math.sqrt(Math.max(0.0, 2.4 - (y * y)));
            for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 14) {
                double x = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;
                world.spawnParticle(Particle.END_ROD, player.getLocation().clone().add(x, y, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().clone().add(x * 0.6, y * 0.6, z * 0.6), 1, 0, 0, 0, 0);
            }
        }

        for (Entity nearby : player.getNearbyEntities(8.0, 4.0, 8.0)) {
            if (!(nearby instanceof LivingEntity target) || target.equals(player)) {
                continue;
            }

            Vector knock = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(2.2);
            knock.setY(0.9);
            target.setVelocity(knock);
            target.damage(8.0, player);
        }
    }
}

