package me.remag501.power.ability;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Utility class for creating ability item representations in the inventory.
 */
public class AbilityItemBuilder {

    public static ItemStack buildAbilityItem(Ability ability, int hotbarSlot) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + ability.getDisplayName());
        meta.setLore(List.of(
                ChatColor.GRAY + ability.getDescription(),
                "",
                ChatColor.AQUA + "Slot: " + (hotbarSlot + 1),
                ChatColor.AQUA + "Cooldown: " + ability.getCooldownSeconds() + "s",
                "",
                ChatColor.YELLOW + "Place in hotbar to bind"
        ));

        // Add custom NBT tag to identify ability items
        meta.setCustomModelData(1000 + hotbarSlot);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack buildAbilityHotbarItem(Ability ability, int hotbarSlot) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + ability.getDisplayName());
        meta.setLore(List.of(
                ChatColor.GRAY + ability.getDescription(),
                ChatColor.AQUA + "Cooldown: " + ability.getCooldownSeconds() + "s",
                "",
                ChatColor.GREEN + "Right-click or use to activate"
        ));

        meta.setCustomModelData(2000 + hotbarSlot);
        item.setItemMeta(meta);
        return item;
    }
}

