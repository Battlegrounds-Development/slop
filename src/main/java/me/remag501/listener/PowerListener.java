package me.remag501.listener;

import me.remag501.gui.PowerSelectionMenu;
import me.remag501.power.PowerManager;
import me.remag501.power.PowerType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;

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
    public void onAbilityTrigger(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            return;
        }

        PowerManager.AbilityTriggerResult result = powerManager.triggerAbility(player);
        switch (result.status()) {
            case SUCCESS -> {
                player.sendMessage(ChatColor.AQUA + result.ability().getDisplayName() + ChatColor.GREEN + " activated!");
                event.setCancelled(true);
            }
            case COOLDOWN -> player.sendMessage(ChatColor.RED + "Ability cooldown: " + result.remainingSeconds() + "s");
            case NO_POWER -> {
            }
        }
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

