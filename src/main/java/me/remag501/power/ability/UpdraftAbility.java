package me.remag501.power.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Updraft ability for Skybound power - jump boost enhancement.
 */
public class UpdraftAbility implements Ability {

    @Override
    public String getId() {
        return "updraft";
    }

    @Override
    public String getDisplayName() {
        return "Updraft";
    }

    @Override
    public String getDescription() {
        return "Feel the wind carry you upward with enhanced jump power.";
    }

    @Override
    public int getCooldownSeconds() {
        return 9;
    }

    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 4 * 20, 3, false, false));
    }

    @Override
    public Material getItemMaterial() {
        return Material.GHAST_TEAR;
    }
}
