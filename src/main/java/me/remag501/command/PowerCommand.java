package me.remag501.command;

import me.remag501.gui.PowerSelectionMenu;
import me.remag501.power.PowerManager;
import me.remag501.power.PowerType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PowerCommand implements CommandExecutor, TabCompleter {

    private final PowerManager powerManager;
    private final PowerSelectionMenu powerSelectionMenu;

    public PowerCommand(PowerManager powerManager, PowerSelectionMenu powerSelectionMenu) {
        this.powerManager = powerManager;
        this.powerSelectionMenu = powerSelectionMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("slop.power.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            powerSelectionMenu.openFor(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(player, label);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "list" -> {
                player.sendMessage(ChatColor.AQUA + "Available powers: " + ChatColor.WHITE + powerManager.listPowerKeys());
                return true;
            }
            case "gui", "open" -> {
                powerSelectionMenu.openFor(player);
                return true;
            }
            case "info" -> {
                Optional<PowerType> current = powerManager.getPower(player.getUniqueId());
                if (current.isEmpty()) {
                    player.sendMessage(ChatColor.YELLOW + "You have no power selected.");
                } else {
                    PowerType type = current.get();
                    player.sendMessage(ChatColor.GREEN + "Current power: " + ChatColor.AQUA + type.getDisplayName());
                    player.sendMessage(ChatColor.GRAY + type.getDescription());
                    for (var ability : type.getAbilities()) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Ability: " + ChatColor.AQUA + ability.getDisplayName() + ChatColor.GRAY + " (" + ability.getCooldownSeconds() + "s cooldown)");
                        player.sendMessage(ChatColor.GRAY + "  " + ability.getDescription());
                    }
                }
                return true;
            }
            case "ability", "cast" -> {
                PowerManager.AbilityTriggerResult result = powerManager.triggerAbility(player);
                switch (result.status()) {
                    case NO_POWER -> player.sendMessage(ChatColor.YELLOW + "Choose a power first with /" + label + ".");
                    case COOLDOWN -> player.sendMessage(ChatColor.RED + "Ability cooldown: " + result.remainingSeconds() + "s");
                    case SUCCESS -> player.sendMessage(ChatColor.AQUA + result.ability().getDisplayName() + ChatColor.GREEN + " activated!");
                }
                return true;
            }
            case "choose", "set" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /" + label + " choose <power>");
                    return true;
                }

                Optional<PowerType> selected = PowerType.fromInput(args[1]);
                if (selected.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "Unknown power. Try: " + powerManager.listPowerKeys());
                    return true;
                }

                powerManager.setPower(player, selected.get());
                player.sendMessage(ChatColor.GREEN + "You now have the " + ChatColor.AQUA + selected.get().getDisplayName() + ChatColor.GREEN + " power.");
                return true;
            }
            case "clear" -> {
                powerManager.clearPower(player);
                player.sendMessage(ChatColor.YELLOW + "Power cleared.");
                return true;
            }
            default -> {
                sendHelp(player, label);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterByPrefix(List.of("help", "gui", "open", "list", "info", "ability", "cast", "choose", "set", "clear"), args[0]);
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("choose") || args[0].equalsIgnoreCase("set"))) {
            List<String> powerNames = new ArrayList<>();
            for (PowerType type : PowerType.values()) {
                powerNames.add(type.getKey());
            }
            return filterByPrefix(powerNames, args[1]);
        }

        return List.of();
    }

    private List<String> filterByPrefix(List<String> input, String prefix) {
        List<String> result = new ArrayList<>();
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);
        for (String value : input) {
            if (value.startsWith(normalizedPrefix)) {
                result.add(value);
            }
        }
        return result;
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage(ChatColor.GOLD + "Superpower commands:");
        player.sendMessage(ChatColor.YELLOW + "/" + label + ChatColor.GRAY + " - open power selector");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " gui" + ChatColor.GRAY + " - open power selector");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " list" + ChatColor.GRAY + " - show powers");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " info" + ChatColor.GRAY + " - show your power");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " ability" + ChatColor.GRAY + " - activate your power ability");
        player.sendMessage(ChatColor.GRAY + "Tip: Sneak + right-click with an empty hand to proc abilities quickly.");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " choose <power>" + ChatColor.GRAY + " - pick a power");
        player.sendMessage(ChatColor.YELLOW + "/" + label + " clear" + ChatColor.GRAY + " - remove your power");
    }
}

