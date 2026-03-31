package me.remag501;

import me.remag501.command.PowerCommand;
import me.remag501.gui.PowerSelectionMenu;
import me.remag501.listener.PowerListener;
import me.remag501.power.PowerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class slop extends JavaPlugin {

    private PowerManager powerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.powerManager = new PowerManager(this);
        this.powerManager.load();
        PowerSelectionMenu powerSelectionMenu = new PowerSelectionMenu(this.powerManager);

        if (getCommand("power") != null) {
            PowerCommand powerCommand = new PowerCommand(this.powerManager, powerSelectionMenu);
            getCommand("power").setExecutor(powerCommand);
            getCommand("power").setTabCompleter(powerCommand);
        } else {
            getLogger().severe("Command 'power' is missing from plugin.yml");
        }

        getServer().getPluginManager().registerEvents(new PowerListener(this.powerManager, powerSelectionMenu), this);
        getLogger().info("slop enabled: superpowers are ready.");
    }

    @Override
    public void onDisable() {
        if (this.powerManager != null) {
            this.powerManager.save();
        }
        getLogger().info("slop disabled: powers saved.");
    }
}
