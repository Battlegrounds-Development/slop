package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class ViltrumiteRushAbility implements Ability {

    @Override
    public String getId() {
        return "viltrumite_rush";
    }

    @Override
    public String getDisplayName() {
        return "Viltrumite Rush";
    }

    @Override
    public String getDescription() {
        return "Lock your trajectory and shred through targets for a short, violent burst.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.DRAGON_BREATH;
    }

    @Override
    public int getCooldownSeconds() {
        return 14;
    }

    @Override
    public void activate(Player player) {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());

        Vector lockedDirection = player.getLocation().getDirection().normalize();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.6f);

        new BukkitRunnable() {
            private int ticks = 0;
            private final Set<Integer> hitEntities = new HashSet<>();

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || ticks > 24) {
                    cancel();
                    return;
                }

                Vector v = lockedDirection.clone().multiply(2.6);
                v.setY(Math.max(0.15, v.getY()));
                player.setVelocity(v);
                player.setFallDistance(0.0F);

                player.getWorld().spawnParticle(Particle.SONIC_BOOM, player.getLocation(), 1, 0.0, 0.0, 0.0, 0.0);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 16, 0.3, 0.3, 0.3, 0.15);

                for (Entity nearby : player.getNearbyEntities(1.6, 1.6, 1.6)) {
                    if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                        continue;
                    }
                    if (!hitEntities.add(nearby.getEntityId())) {
                        continue;
                    }
                    target.damage(14.0, player);
                    Vector knock = lockedDirection.clone().multiply(2.0);
                    knock.setY(0.35);
                    target.setVelocity(knock);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
