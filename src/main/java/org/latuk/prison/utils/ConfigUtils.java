package org.latuk.prison.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.latuk.prison.Prison;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigUtils {

    private final Prison plugin;
    private final Map<String, FileConfiguration> configMap = new HashMap<>();
    private final Map<String, File> configFileMap = new HashMap<>();

    public ConfigUtils(Prison plugin) {
        this.plugin = plugin;
    }

    public void setupConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
        configFileMap.put(fileName, configFile);

        if (!configFile.exists()) {
            plugin.saveResource(fileName + ".yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configMap.put(fileName, config);
    }

    public FileConfiguration getCustomConfig(String fileName) {
        return configMap.get(fileName);
    }

    public void saveCustomConfig(String fileName) {
        File configFile = configFileMap.get(fileName);
        FileConfiguration config = configMap.get(fileName);

        if (configFile != null && config != null) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getMessageFromConfig(String message) {
        String result = getCustomConfig("messages").getString("messages." + message);
        return result != null ? result : "Произошла неизвестная ошибка!";
    }

    public void addNewPlayerToCFG(Player player) {
        FileConfiguration config = getCustomConfig("players");
        String playerName = player.getName();
        String path = "players." + playerName;

        if (!config.contains(path)) {
            config.set(path + ".level", 1);
            config.set(path + ".blocks", 0);
            config.set(path + ".money", 0);
            config.set(path + ".items.SHEARS.level", 0);
            saveCustomConfig("players");
        }
    }

    public Map<String, Object> getPlayerInfo(Player player) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;
        Map<String, Object> playerInfo = new HashMap<>();

        if (config.contains(path)) {
            playerInfo.put("level", config.getInt(path + ".level"));
            playerInfo.put("blocks", config.getInt(path + ".blocks"));
            playerInfo.put("money", config.getInt(path + ".money"));
            playerInfo.put("pickaxeLevel", config.getInt(path + ".items.PICKAXE.level", 1)); // Возвращаем 1, если уровень кирки не указан
        } else {
            addNewPlayerToCFG(player);
            return getPlayerInfo(player); // Рекурсивный вызов после добавления игрока
        }

        return playerInfo;
    }

    public void setItemLevel(Player player, String itemName, int level) {
        FileConfiguration config = getCustomConfig("players");
        String playerName = player.getName();
        String path = "players." + playerName + ".items." + itemName + ".level";

        // Устанавливаем уровень независимо от того, существует ли путь
        config.set(path, level);
        saveCustomConfig("players");
    }

    public int getItemLevel(Player player, String itemName) {
        FileConfiguration config = getCustomConfig("players");
        String playerName = player.getName();
        String itemPath = "players." + playerName + ".items." + itemName + ".level";

        // Если путь существует, возвращаем уровень предмета
        if (config.contains(itemPath)) {
            return config.getInt(itemPath);
        }

        // Если игрок или предмет отсутствуют в конфиге, добавляем их
        if (!config.contains("players." + playerName)) {
            addNewPlayerToCFG(player);
        }

        // Устанавливаем начальный уровень 1 и возвращаем его
        setItemLevel(player, itemName, 1);
        return 1;
    }

    public int getPlayerMoney(Player player) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            return config.getInt(path + ".money");
        } else {
            addNewPlayerToCFG(player);
            return getPlayerMoney(player);
        }
    }

    public int getPlayerLevel(Player player) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            return config.getInt(path + ".level");
        } else {
            addNewPlayerToCFG(player);
            return getPlayerLevel(player);
        }
    }

    public int getPlayerBlocks(Player player) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            return config.getInt(path + ".blocks");
        } else {
            addNewPlayerToCFG(player);
            return getPlayerBlocks(player);
        }
    }

    public void givePlayerMoney(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".money");
            config.set(path + ".money", currentAmount+amount);
        } else {
            addNewPlayerToCFG(player);
            givePlayerMoney(player, amount);
        }
        saveCustomConfig("players");
    }

    public void givePlayerLevel(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".level");
            config.set(path + ".level", currentAmount+amount);
        } else {
            addNewPlayerToCFG(player);
            givePlayerLevel(player, amount);
        }
        saveCustomConfig("players");
    }

    public void givePlayerBlocks(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".blocks");
            config.set(path + ".blocks", currentAmount+amount);
        } else {
            addNewPlayerToCFG(player);
            givePlayerBlocks(player, amount);
        }
        saveCustomConfig("players");
    }

    public void takePlayerMoney(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".money");
            config.set(path + ".money", currentAmount-amount);
        } else {
            addNewPlayerToCFG(player);
            takePlayerMoney(player, amount);
        }
        saveCustomConfig("players");
    }

    public void takePlayerLevel(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".level");
            config.set(path + ".level", currentAmount-amount);
        } else {
            addNewPlayerToCFG(player);
            takePlayerLevel(player, amount);
        }
        saveCustomConfig("players");
    }

    public void takePlayerBlocks(Player player, int amount) {
        String playerName = player.getName();
        FileConfiguration config = getCustomConfig("players");
        String path = "players." + playerName;

        if (config.contains(path)) {
            int currentAmount = config.getInt(path + ".blocks");
            config.set(path + ".blocks", currentAmount-amount);
        } else {
            addNewPlayerToCFG(player);
            takePlayerBlocks(player, amount);
        }
        saveCustomConfig("players");
    }

    public double getLevelPrice(int level) {
        FileConfiguration config = getCustomConfig("levels");
        String path = "levels." + level + ".price";
        if (config.contains(path)) {
            return config.getDouble(path);
        } else {
            return -1; // Возвращаем -1 если уровень не найден
        }
    }

    public int getLevelBlocks(int level) {
        FileConfiguration config = getCustomConfig("levels");
        String path = "levels." + level + ".blocks";
        if (config.contains(path)) {
            return config.getInt(path);
        } else {
            return -1; // Возвращаем -1 если уровень не найден
        }
    }

    public double getBlockPrice(Material blockMaterial) {
        FileConfiguration config = getCustomConfig("blocks");
        String path = "blocks." + blockMaterial.toString();
        if (config.contains(path)) return config.getDouble(path + ".price", 0.0);
        return 0.0;
    }

    public int getBlockLevel(Material blockMaterial) {
        FileConfiguration config = getCustomConfig("blocks");
        String path = "blocks." + blockMaterial.toString();
        if (config.contains(path)) return config.getInt(path + ".level", 0);
        return 0;
    }

    public boolean isPlayerExistsInCFG(Player player) {
        FileConfiguration config = getCustomConfig("players");
        String playerName = player.getName();
        String path = "players." + playerName;

        return config.contains(path);
    }

    public static String formatNumberStatic(long number) {
        if (number < 1_000) return String.valueOf(number);
        if (number < 1_000_000) return String.format("%.2fK", number / 1_000.0);
        if (number < 1_000_000_000) return String.format("%.2fM", number / 1_000_000.0);
        if (number < 1_000_000_000_000L) return String.format("%.2fB", number / 1_000_000_000.0);
        if (number < 1_000_000_000_000_000L) return String.format("%.2fT", number / 1_000_000_000_000.0);
        return String.format("%.2fQa", number / 1_000_000_000_000_000.0); // Qi = 1 quadrillion
    }

    public String formatNumber(long number) {
        if (number < 1_000) return String.valueOf(number);
        if (number < 1_000_000) return String.format("%.2fK", number / 1_000.0);
        if (number < 1_000_000_000) return String.format("%.2fM", number / 1_000_000.0);
        if (number < 1_000_000_000_000L) return String.format("%.2fB", number / 1_000_000_000.0);
        if (number < 1_000_000_000_000_000L) return String.format("%.2fT", number / 1_000_000_000_000.0);
        return String.format("%.2fQa", number / 1_000_000_000_000_000.0); // Qi = 1 quadrillion
    }


    public List<Mine> getMines() {
        List<Mine> mines = new ArrayList<>();
        FileConfiguration config = getCustomConfig("mines");
        ConfigurationSection minesSection = config.getConfigurationSection("mines");
        if (minesSection == null) {
            Bukkit.getLogger().severe("Секция 'mines' не найдена в mines.yml!");
            return new ArrayList<>(); // Возвращаем пустой список, если секция отсутствует
        }
        Set<String> mineKeys = minesSection.getKeys(false);


        for (String key : mineKeys) {
            String path = "mines." + key;
            String name = config.getString(path + ".name");
            String description = config.getString(path + ".description");
            int level = config.getInt(path + ".level");

            List<Material> materials = new ArrayList<>();
            for (String materialName : config.getStringList(path + ".material")) {
                materials.add(Material.valueOf(materialName));
            }

            World world = Bukkit.getWorld(config.getString(path + ".world"));
            if (world == null) {
                Bukkit.getLogger().warning("Мир " + config.getString(path + ".world") + " не найден!");
                continue;
            }

            Location spawn = new Location(world,
                    config.getDouble(path + ".spawn.x"),
                    config.getDouble(path + ".spawn.y"),
                    config.getDouble(path + ".spawn.z"));

            Location mineCorner1 = new Location(world,
                    config.getDouble(path + ".mine.x1"),
                    config.getDouble(path + ".mine.y1"),
                    config.getDouble(path + ".mine.z1"));

            Location mineCorner2 = new Location(world,
                    config.getDouble(path + ".mine.x2"),
                    config.getDouble(path + ".mine.y2"),
                    config.getDouble(path + ".mine.z2"));

            Material guiMaterial = Material.valueOf(config.getString(path + ".gui-material"));
            int slot = config.getInt(path + ".slot");

            Mine mine = new Mine(key, name, description, level, materials, world, spawn, mineCorner1, mineCorner2, guiMaterial, slot);
            mines.add(mine);
        }

        return mines;
    }

    // Метод, который проверяет, находится ли блок внутри какой-либо из шахт
    public boolean isBlockInMine(Block block) {
        Location blockLocation = block.getLocation();
        List<Mine> mines = getMines();

        for (Mine mine : mines) {
            if (isLocationInsideMine(blockLocation, mine)) {
                return true; // Блок находится в одной из шахт
            }
        }
        return false; // Блок не находится ни в одной шахте
    }

    // Метод для проверки, находится ли координата внутри шахты
    private boolean isLocationInsideMine(Location location, Mine mine) {
        Location corner1 = mine.getMineCorner1();
        Location corner2 = mine.getMineCorner2();

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());

        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());

        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
                location.getY() >= minY && location.getY() <= maxY &&
                location.getZ() >= minZ && location.getZ() <= maxZ &&
                location.getWorld().equals(mine.getWorld()); // Проверяем, что мир совпадает
    }
}
