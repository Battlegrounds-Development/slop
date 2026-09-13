package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class EventHorizonAbility implements Ability {

    @Override
    public String getId() {
        return "event_horizon";
    }

    @Override
    public String getDisplayName() {
        return "Event Horizon";
    }

    @Override
    public String getDescription() {
        return "Create a gravitational vortex that drags enemies toward your center.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.ECHO_SHARD;
    }

    @Override
    public int getCooldownSeconds() {
        return 16;
    }

    @Override
    public void activate(Player player) {
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.7f);

        for (double r = 3.6; r >= 0.6; r -= 0.6) {
            for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 10) {
                double x = Math.cos(theta) * r;
                double z = Math.sin(theta) * r;
                world.spawnParticle(Particle.DRAGON_BREATH, player.getLocation().clone().add(x, 0.25, z), 1, 0, 0, 0, 0);
            }
        }

        for (Entity nearby : player.getNearbyEntities(9.0, 5.0, 9.0)) {
            if (!(nearby instanceof LivingEntity target) || target.equals(player)) {
                continue;
            }

            Vector pull = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.6);
            pull.setY(0.35);
            target.setVelocity(pull);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3, false, true));
        }
    }
}

