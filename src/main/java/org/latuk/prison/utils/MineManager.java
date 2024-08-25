package org.latuk.prison.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.latuk.prison.Prison;

import java.util.List;
import java.util.Random;

public class MineManager {

    private final Prison plugin;
    private final ConfigUtils configUtils;
    private final Random random = new Random();

    public MineManager(Prison plugin, ConfigUtils configUtils) {
        this.plugin = plugin;
        this.configUtils = configUtils;
        startMineFillingTask();
    }

    // Запускаем задачу по заполнению шахт каждые 5 минут
    private void startMineFillingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Mine> mines = configUtils.getMines();
                for (Mine mine : mines) {
                    fillMine(mine);
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L); // 6000 тиков = 5 минут
    }

    // Метод для заполнения шахты материалами
    private void fillMine(Mine mine) {
        Location corner1 = mine.getMineCorner1();
        Location corner2 = mine.getMineCorner2();
        List<Material> materials = mine.getMaterials();

        if (materials.isEmpty()) {
            return; // Если нет материалов для заполнения, выходим
        }

        for (int x = Math.min(corner1.getBlockX(), corner2.getBlockX()); x <= Math.max(corner1.getBlockX(), corner2.getBlockX()); x++) {
            for (int y = Math.min(corner1.getBlockY(), corner2.getBlockY()); y <= Math.max(corner1.getBlockY(), corner2.getBlockY()); y++) {
                for (int z = Math.min(corner1.getBlockZ(), corner2.getBlockZ()); z <= Math.max(corner1.getBlockZ(), corner2.getBlockZ()); z++) {
                    Location blockLocation = new Location(mine.getWorld(), x, y, z);
                    Material randomMaterial = materials.get(random.nextInt(materials.size())); // Выбираем случайный материал
                    blockLocation.getBlock().setType(randomMaterial); // Устанавливаем материал в блок
                }
            }
        }
    }

    public void teleportToMine(Player player, String mineNumber) {
        int playerLevel = configUtils.getPlayerLevel(player);
        int mineLevel = 0;
        Location mineSpawn = new Location(Bukkit.getWorld("world"), 0, 100, 0);
        List<Mine> mines = configUtils.getMines();
        for (Mine mine : mines) {
            if (mine.getNumber().equalsIgnoreCase(mineNumber)) {
                mineLevel = mine.getLevel();
                mineSpawn = mine.getSpawn();
                if (playerLevel >= mineLevel) {
                    player.teleport(mineSpawn);
                    player.sendMessage(ChatColor.GREEN + configUtils.getMessageFromConfig("player-successfully-teleported-to-mine"));
                } else {
                    player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-level"));
                }
            }
        }
    }
}
