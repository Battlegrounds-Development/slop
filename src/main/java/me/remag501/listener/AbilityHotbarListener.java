package me.remag501.listener;

import me.remag501.power.PowerManager;
import org.bukkit.ChatColor;
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

        // Identify hotbar ability items by custom model data range.
        if (item.getItemMeta() == null || !item.getItemMeta().hasCustomModelData()) {
            return;
        }

        int customModelData = item.getItemMeta().getCustomModelData();
        if (customModelData < 2000 || customModelData > 2008) {
            return;
        }

        int hotbarSlot = player.getInventory().getHeldItemSlot();
        PowerManager.AbilityTriggerResult result = powerManager.triggerBoundAbility(player, hotbarSlot);

        switch (result.status()) {
            case SUCCESS -> {
                int cooldownTicks = Math.max(0, result.ability().getCooldownSeconds()) * 20;
                player.setCooldown(item.getType(), cooldownTicks);
                player.sendMessage(ChatColor.AQUA + result.ability().getDisplayName() + ChatColor.GREEN + " activated!");
                event.setCancelled(true);
            }
            case COOLDOWN -> player.sendMessage(ChatColor.RED + "Cooldown: " + result.remainingSeconds() + "s");
            case NO_POWER -> {
                // Silently ignore - no ability bound.
            }
        }
    }
}
