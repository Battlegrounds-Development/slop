package me.remag501.listener;

import me.remag501.gui.PowerSelectionMenu;
import me.remag501.power.PowerManager;
import me.remag501.power.PowerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class PowerListener implements Listener {

    private final PowerManager powerManager;
    private final PowerSelectionMenu powerSelectionMenu;

    public PowerListener(PowerManager powerManager, PowerSelectionMenu powerSelectionMenu) {
        this.powerManager = powerManager;
        this.powerSelectionMenu = powerSelectionMenu;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        powerManager.reapplyPower(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        powerManager.reapplyPower(event.getPlayer());
    }

    @EventHandler
    public void onPowerMenuClick(InventoryClickEvent event) {
        if (!powerSelectionMenu.isPowerMenu(event.getView().getTopInventory())) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        int slot = event.getRawSlot();
        Optional<PowerType> selectedPower = powerSelectionMenu.getPowerForSlot(slot);
        if (selectedPower.isPresent()) {
            powerManager.setPower(player, selectedPower.get());
            player.sendMessage(ChatColor.GREEN + "You now have the " + ChatColor.AQUA + selectedPower.get().getDisplayName() + ChatColor.GREEN + " power.");
            player.sendMessage(ChatColor.YELLOW + "Abilities were added to your hotbar.");
            player.closeInventory();
            return;
        }

        if (powerSelectionMenu.isClearSlot(slot)) {
            powerManager.clearPower(player);
            player.sendMessage(ChatColor.YELLOW + "Power cleared.");
            player.closeInventory();
        }
    }

    @EventHandler
    public void onPowerMenuDrag(InventoryDragEvent event) {
        if (powerSelectionMenu.isPowerMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }
}
