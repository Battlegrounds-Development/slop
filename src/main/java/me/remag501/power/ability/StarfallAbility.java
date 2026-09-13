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

public class StarfallAbility implements Ability {

    @Override
    public String getId() {
        return "starfall";
    }

    @Override
    public String getDisplayName() {
        return "Starfall";
    }

    @Override
    public String getDescription() {
        return "Drop a radiant starfield, burning and crippling enemies in a wide radius.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.AMETHYST_SHARD;
    }

    @Override
    public int getCooldownSeconds() {
        return 24;
    }

    @Override
    public void activate(Player player) {
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.7f);

        for (double y = 3.2; y >= -0.2; y -= 0.35) {
            double radius = 4.2 - Math.max(0.0, (3.2 - y) * 0.6);
            for (double theta = 0; theta < Math.PI * 2; theta += Math.PI / 16) {
                double x = Math.cos(theta) * radius;
                double z = Math.sin(theta) * radius;
                world.spawnParticle(Particle.END_ROD, player.getLocation().clone().add(x, y, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.FIREWORK, player.getLocation().clone().add(x * 0.75, y * 0.85, z * 0.75), 1, 0, 0, 0, 0);
            }
        }

        for (Entity nearby : player.getNearbyEntities(7.0, 5.0, 7.0)) {
            if (!(nearby instanceof LivingEntity target) || target.equals(player)) {
                continue;
            }
            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, false, true));
            target.setFireTicks(60);
            Vector juggle = target.getVelocity();
            juggle.setY(Math.max(juggle.getY(), 0.55));
            target.setVelocity(juggle);
            target.damage(6.0, player);
        }
    }
}

