package me.remag501.power.ability;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Glide ability for Skybound power - slow fall for 8 seconds.
 */
public class GlideAbility implements Ability {

    @Override
    public String getId() {
        return "glide";
    }

    @Override
    public String getDisplayName() {
        return "Glide";
    }

    @Override
    public String getDescription() {
        return "Float gracefully through the air for 8 seconds.";
    }

    @Override
    public int getCooldownSeconds() {
        return 11;
    }

    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 8 * 20, 0, false, false));
    }
}

