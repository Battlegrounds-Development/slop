package me.remag501.power.ability;

import me.remag501.power.ViltrumiteModeTracker;
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
        boolean cityBreaker = ViltrumiteModeTracker.isActive(player);
        double maxRadius = cityBreaker ? 10.5 : 7.5;
        double damage = cityBreaker ? 18.0 : 12.0;
        double horizontalKnock = cityBreaker ? 3.0 : 2.3;
        double verticalKnock = cityBreaker ? 1.35 : 1.05;

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, cityBreaker ? 1.7f : 1.3f, cityBreaker ? 0.55f : 0.7f);

        for (double radius = 1.5; radius <= maxRadius; radius += 1.0) {
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 20) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().clone().add(x, 0.15, z), 1, 0.0, 0.0, 0.0, 0.0);
                player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation().clone().add(x, 0.15, z), 1, 0.0, 0.0, 0.0, 0.0, Material.STONE.createBlockData());
            }
        }

        for (Entity nearby : player.getNearbyEntities(maxRadius + 0.5, 6.0, maxRadius + 0.5)) {
            if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                continue;
            }

            Vector launch = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(horizontalKnock);
            launch.setY(verticalKnock);
            target.setVelocity(launch);
            target.damage(damage, player);
        }
    }
}
