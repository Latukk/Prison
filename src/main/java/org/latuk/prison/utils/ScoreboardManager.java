package org.latuk.prison.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.latuk.prison.Prison;

import java.util.List;

public class ScoreboardManager {

    private final Prison plugin;
    private final ConfigUtils configUtils;

    public ScoreboardManager(Prison plugin, ConfigUtils configUtils) {
        this.plugin = plugin;
        this.configUtils = configUtils;
        startUpdatingScoreboards();
    }

    // Запускаем обновление Scoreboard для всех игроков
    private void startUpdatingScoreboards() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Обновление каждую секунду (20 тиков)
    }

    // Обновляем Scoreboard для игрока
    private void updateScoreboard(Player player) {
        FileConfiguration scoreboardConfig = configUtils.getCustomConfig("scoreboard");
        String serverName = scoreboardConfig.getString("server-name");
        List<String> scoreboardLines = scoreboardConfig.getStringList("scoreboard");

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("customScoreboard", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&a" + serverName));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int scorePosition = scoreboardLines.size() - 1; // Начинаем с конца

        for (String line : scoreboardLines) {
            line = replacePlaceholders(line, player);

            if (line.isEmpty()) {
                // Используем невидимый символ для пустой строки
                line = ChatColor.RESET + " "; // Можно использовать пробел или другой невидимый символ
            }

            Score score = objective.getScore(line);
            score.setScore(scorePosition--);
        }

        player.setScoreboard(scoreboard);
    }

    // Замена плейсхолдеров на реальные значения
    private String replacePlaceholders(String line, Player player) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        line = line.replace("%server-name%", configUtils.getCustomConfig("scoreboard").getString("server-name"));
        line = line.replace("%blocks%", ConfigUtils.formatNumberStatic(configUtils.getPlayerBlocks(player)));
        line = line.replace("%money%", ConfigUtils.formatNumberStatic(configUtils.getPlayerMoney(player)));
        line = line.replace("%level%", ConfigUtils.formatNumberStatic(configUtils.getPlayerLevel(player)));
        line = line.replace("%player-name%", player.getName());
        line = line.replace("%server-online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        return line;
    }
}
