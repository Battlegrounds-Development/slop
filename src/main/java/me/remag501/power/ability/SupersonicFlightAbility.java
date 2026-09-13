package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SupersonicFlightAbility implements Ability {

    @Override
    public String getId() {
        return "supersonic_flight";
    }

    @Override
    public String getDisplayName() {
        return "Supersonic Flight";
    }

    @Override
    public String getDescription() {
        return "Sustain controlled high-speed flight with real-time direction and lift calculations.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.HEART_OF_THE_SEA;
    }

    @Override
    public int getCooldownSeconds() {
        return 22;
    }

    @Override
    public void activate(Player player) {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_3, 1.0f, 1.2f);

        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || ticks > 120) {
                    cancel();
                    return;
                }

                // Blend look direction with vertical stabilization each tick.
                Vector look = player.getLocation().getDirection().normalize();
                double lift = look.getY() > -0.2 ? 0.18 : 0.07;
                Vector flight = look.multiply(1.75);
                flight.setY(look.getY() * 0.75 + lift);
                player.setVelocity(flight);
                player.setFallDistance(0.0F);

                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 7, 0.25, 0.25, 0.25, 0.01);
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 3, 0.2, 0.2, 0.2, 0.0);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}

