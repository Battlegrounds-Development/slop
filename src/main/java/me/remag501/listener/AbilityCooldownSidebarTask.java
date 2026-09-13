package me.remag501.listener;

import me.remag501.power.PowerManager;
import me.remag501.power.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Updates a sidebar scoreboard with bound ability cooldowns.
 */
public class AbilityCooldownSidebarTask extends BukkitRunnable {

    private static final String OBJECTIVE_ID = "slop_cd";
    private final PowerManager powerManager;

    public AbilityCooldownSidebarTask(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateSidebar(player);
        }
    }

    private void updateSidebar(Player player) {
        if (!powerManager.hasBoundAbilities(player)) {
            clearSidebar(player);
            return;
        }

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_ID);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(OBJECTIVE_ID, "dummy", ChatColor.DARK_AQUA + "Ability Cooldowns");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        resetObjectiveEntries(scoreboard, objective);

        List<Map.Entry<Integer, Ability>> entries = new ArrayList<>(powerManager.getBoundAbilities(player).entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getKey));

        int score = entries.size();
        for (Map.Entry<Integer, Ability> entry : entries) {
            int slot = entry.getKey();
            Ability ability = entry.getValue();
            int remaining = powerManager.getRemainingCooldownSeconds(player, ability);

            String status = remaining > 0
                    ? ChatColor.RED + String.valueOf(remaining) + "s"
                    : ChatColor.GREEN + "READY";
            String line = ChatColor.AQUA + "[" + (slot + 1) + "] " + ChatColor.WHITE + ability.getDisplayName() + " " + status;

            // Scoreboard entries must be unique and <= 40 chars; append hidden color code key.
            String unique = trimToMax(line, 38) + String.valueOf(ChatColor.COLOR_CHAR) + Integer.toHexString(slot % 10);
            objective.getScore(unique).setScore(score--);
        }
    }

    private void resetObjectiveEntries(Scoreboard scoreboard, Objective objective) {
        for (String entry : scoreboard.getEntries()) {
            if (objective.getScore(entry).isScoreSet()) {
                scoreboard.resetScores(entry);
            }
        }
    }

    private String trimToMax(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    private void clearSidebar(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_ID);
        if (objective != null) {
            objective.unregister();
        }
    }
}
