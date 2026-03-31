package me.remag501.listener;

import me.remag501.power.PowerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PowerListener implements Listener {

    private final PowerManager powerManager;

    public PowerListener(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        powerManager.reapplyPower(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        powerManager.reapplyPower(event.getPlayer());
    }
}

