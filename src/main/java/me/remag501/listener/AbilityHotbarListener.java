package me.remag501.listener;

import me.remag501.power.PowerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AbilityHotbarListener implements Listener {

    private final PowerManager powerManager;

    public AbilityHotbarListener(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @EventHandler
    public void onAbilityItemClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if the item is an ability item (custom model data indicates ability)
        if (item.getType() != Material.AMETHYST_SHARD || item.getItemMeta() == null) {
            return;
        }

        int customModelData = item.getItemMeta().getCustomModelData();
        if (customModelData < 2000) {
            return; // Not a hotbar ability item
        }

        int hotbarSlot = player.getInventory().getHeldItemSlot();
        PowerManager.AbilityTriggerResult result = powerManager.triggerBoundAbility(player, hotbarSlot);

        switch (result.status()) {
            case SUCCESS -> {
                player.sendMessage(ChatColor.AQUA + result.ability().getDisplayName() + ChatColor.GREEN + " activated!");
                event.setCancelled(true);
            }
            case COOLDOWN -> player.sendMessage(ChatColor.RED + "Cooldown: " + result.remainingSeconds() + "s");
            case NO_POWER -> {
                // Silently ignore - no ability bound
            }
        }
    }
}

