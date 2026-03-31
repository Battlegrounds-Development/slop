package me.remag501.power.ability;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Fortress ability for Titan power - temporary damage reduction.
 */
public class FortressAbility implements Ability {

    @Override
    public String getId() {
        return "fortress";
    }

    @Override
    public String getDisplayName() {
        return "Fortress";
    }

    @Override
    public String getDescription() {
        return "Harden your body, gaining resistance for 6 seconds.";
    }

    @Override
    public int getCooldownSeconds() {
        return 15;
    }

    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 6 * 20, 2, false, false));
    }
}

