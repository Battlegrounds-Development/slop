package me.remag501.gui;

import me.remag501.power.PowerManager;
import me.remag501.power.PowerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class PowerSelectionMenu {

    private static final int INVENTORY_SIZE = 27;
    private static final String TITLE = ChatColor.DARK_AQUA + "Select Your Superpower";
    private static final int SPEEDSTER_SLOT = 11;
    private static final int TITAN_SLOT = 13;
    private static final int SKYBOUND_SLOT = 15;
    private static final int CLEAR_SLOT = 22;

    private final PowerManager powerManager;

    public PowerSelectionMenu(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public void openFor(Player player) {
        Inventory inventory = Bukkit.createInventory(new PowerMenuHolder(), INVENTORY_SIZE, TITLE);

        Optional<PowerType> current = powerManager.getPower(player.getUniqueId());
        inventory.setItem(SPEEDSTER_SLOT, buildPowerItem(PowerType.SPEEDSTER, current));
        inventory.setItem(TITAN_SLOT, buildPowerItem(PowerType.TITAN, current));
        inventory.setItem(SKYBOUND_SLOT, buildPowerItem(PowerType.SKYBOUND, current));
        inventory.setItem(CLEAR_SLOT, buildClearItem(current.isPresent()));

        player.openInventory(inventory);
    }

    public boolean isPowerMenu(Inventory inventory) {
        return inventory.getHolder() instanceof PowerMenuHolder;
    }

    public Optional<PowerType> getPowerForSlot(int slot) {
        return switch (slot) {
            case SPEEDSTER_SLOT -> Optional.of(PowerType.SPEEDSTER);
            case TITAN_SLOT -> Optional.of(PowerType.TITAN);
            case SKYBOUND_SLOT -> Optional.of(PowerType.SKYBOUND);
            default -> Optional.empty();
        };
    }

    public boolean isClearSlot(int slot) {
        return slot == CLEAR_SLOT;
    }

    private ItemStack buildPowerItem(PowerType type, Optional<PowerType> current) {
        Material material = switch (type) {
            case SPEEDSTER -> Material.SUGAR;
            case TITAN -> Material.IRON_SWORD;
            case SKYBOUND -> Material.FEATHER;
        };

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        boolean selected = current.map(type::equals).orElse(false);
        meta.setDisplayName(ChatColor.AQUA + type.getDisplayName());
        meta.setLore(List.of(
                ChatColor.GRAY + type.getDescription(),
                ChatColor.LIGHT_PURPLE + "Ability: " + ChatColor.AQUA + type.getAbilityName(),
                ChatColor.GRAY + type.getAbilityDescription(),
                ChatColor.GRAY + "Cooldown: " + type.getAbilityCooldownSeconds() + "s",
                "",
                selected ? ChatColor.GREEN + "Currently selected" : ChatColor.YELLOW + "Click to select"
        ));


        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildClearItem(boolean hasPower) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(ChatColor.RED + "Clear Power");
        meta.setLore(List.of(
                hasPower ? ChatColor.YELLOW + "Click to remove your current power" : ChatColor.GRAY + "No power selected"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static final class PowerMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}

