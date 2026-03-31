package me.remag501.power.ability;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Sprint ability for Speedster power - grants speed boost for a duration.
 */
public class SprintAbility implements Ability {

    @Override
    public String getId() {
        return "sprint";
    }

    @Override
    public String getDisplayName() {
        return "Sprint";
    }

    @Override
    public String getDescription() {
        return "Gain a powerful speed boost for 5 seconds.";
    }

    @Override
    public int getCooldownSeconds() {
        return 12;
    }

    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2, false, false));
    }
}

