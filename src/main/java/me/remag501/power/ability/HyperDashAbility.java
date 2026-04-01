package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HyperDashAbility implements Ability {

    @Override
    public String getId() {
        return "hyper_dash";
    }

    @Override
    public String getDisplayName() {
        return "Hyper Dash";
    }

    @Override
    public String getDescription() {
        return "Launch at absurd speed and carve a plasma trail behind you.";
    }

    @Override
    public Material getItemMaterial() {
        return Material.PRISMARINE_CRYSTALS;
    }

    @Override
    public int getCooldownSeconds() {
        return 11;
    }

    @Override
    public void activate(Player player) {
        World world = player.getWorld();
        Vector direction = player.getLocation().getDirection().normalize();
        Vector velocity = direction.multiply(2.9);
        velocity.setY(Math.max(0.35, velocity.getY()));
        player.setVelocity(velocity);

        world.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.9f, 1.6f);

        for (int i = 0; i < 18; i++) {
            double offset = i * 0.35;
            Vector back = direction.clone().multiply(-offset);
            world.spawnParticle(Particle.FIREWORK, player.getLocation().clone().add(back), 3, 0.08, 0.08, 0.08, 0.0);
            world.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().clone().add(back), 2, 0.06, 0.06, 0.06, 0.0);
        }
    }
}

