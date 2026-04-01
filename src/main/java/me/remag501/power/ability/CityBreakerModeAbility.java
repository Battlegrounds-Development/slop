package me.remag501.power.ability;

import me.remag501.power.ViltrumiteModeTracker;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CityBreakerModeAbility implements Ability {

    private static final int DURATION_TICKS = 20 * 15;

    @Override
    public String getId() {
        return "city_breaker_mode";
    }

    @Override
    public String getDisplayName() {
        return "City-Breaker Mode";
    }

    @Override
    public String getDescription() {
        return "Enter boss-phase: enhanced stats, collateral shockwaves, and reduced cooldowns for a short duration.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.ENCHANTED_GOLDEN_APPLE;
    }

    @Override
    public int getCooldownSeconds() {
        return 48;
    }

    @Override
    public void activate(Player player) {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        ViltrumiteModeTracker.activate(player, DURATION_TICKS);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.2f, 0.65f);
        player.sendMessage("City-Breaker Mode engaged.");

        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || !ViltrumiteModeTracker.isActive(player)) {
                    cancel();
                    return;
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 5, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 30, 5, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 3, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 3, false, false, true));

                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation(), 18, 0.45, 0.6, 0.45, 0.04);
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 6, 0.35, 0.35, 0.35, 0.0);

                // Collateral pulse every 10 ticks while mode is active.
                if (ticks % 10 == 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.9f);
                    for (Entity nearby : player.getNearbyEntities(5.5, 3.5, 5.5)) {
                        if (!(nearby instanceof LivingEntity target) || nearby.equals(player)) {
                            continue;
                        }
                        target.damage(6.0, player);
                        Vector blast = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.7);
                        blast.setY(0.55);
                        target.setVelocity(blast);
                    }
                }

                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}

