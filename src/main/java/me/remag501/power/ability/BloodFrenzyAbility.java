package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BloodFrenzyAbility implements Ability {

    @Override
    public String getId() {
        return "blood_frenzy";
    }

    @Override
    public String getDisplayName() {
        return "Blood Frenzy";
    }

    @Override
    public String getDescription() {
        return "Enter an elite kill-state with extreme regeneration and attack pressure.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.TOTEM_OF_UNDYING;
    }

    @Override
    public int getCooldownSeconds() {
        return 26;
    }

    @Override
    public void activate(Player player) {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.9f, 1.6f);

        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || ticks > 100) {
                    cancel();
                    return;
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 3, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 4, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 30, 3, false, false, true));

                player.getWorld().spawnParticle(Particle.CRIMSON_SPORE, player.getLocation(), 14, 0.4, 0.6, 0.4, 0.03);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}

