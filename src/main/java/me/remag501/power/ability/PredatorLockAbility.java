package me.remag501.power.ability;

import me.remag501.power.ViltrumiteModeTracker;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PredatorLockAbility implements Ability {

    @Override
    public String getId() {
        return "predator_lock";
    }

    @Override
    public String getDisplayName() {
        return "Predator Lock";
    }

    @Override
    public String getDescription() {
        return "Acquire the nearest target and auto-intercept with homing pursuit physics.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.TRIDENT;
    }

    @Override
    public int getCooldownSeconds() {
        return 19;
    }

    @Override
    public void activate(Player player) {
        LivingEntity target = player.getWorld().getNearbyLivingEntities(player.getLocation(), 22.0,
                entity -> !entity.equals(player)).stream().findFirst().orElse(null);

        if (target == null) {
            player.sendMessage("No target in range.");
            return;
        }

        boolean cityBreaker = ViltrumiteModeTracker.isActive(player);
        double lockSpeed = cityBreaker ? 2.9 : 2.1;
        double impactDamage = cityBreaker ? 24.0 : 16.0;

        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, cityBreaker ? 1.3f : 1.0f, cityBreaker ? 1.2f : 1.8f);

        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || !target.isValid() || ticks > 40) {
                    cancel();
                    return;
                }

                Vector desired = target.getLocation().toVector().subtract(player.getLocation().toVector());
                if (desired.lengthSquared() < 2.2) {
                    target.damage(impactDamage, player);
                    Vector recoil = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(cityBreaker ? 1.3 : 0.8);
                    recoil.setY(cityBreaker ? 0.4 : 0.22);
                    target.setVelocity(recoil);
                    player.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.3f, 0.8f);
                    player.getWorld().spawnParticle(Particle.SONIC_BOOM, target.getLocation(), 1, 0.0, 0.0, 0.0, 0.0);
                    cancel();
                    return;
                }

                Vector homing = desired.normalize().multiply(lockSpeed);
                homing.setY(Math.max(0.08, homing.getY() * 0.7));
                player.setVelocity(homing);
                player.setFallDistance(0.0F);
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), cityBreaker ? 12 : 6, 0.2, 0.2, 0.2, 0.02);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
