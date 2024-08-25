package org.latuk.prison.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.latuk.prison.Prison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InventoryUtils {
    private final Prison plugin;
    private final ConfigUtils configUtils;
    public InventoryUtils(Prison plugin, ConfigUtils configUtils) {
        this.plugin = plugin;
        this.configUtils = configUtils;
    }

    public static Inventory getShopInventory() {
        // Создаём новый инвентарь с указанным размером и названием
        Inventory emptyInventory = Bukkit.createInventory(null, 27, "Магазин");

        // Возвращаем пустой инвентарь
        return emptyInventory;
    }

    /**
     * Проверяет, есть ли у игрока свободное место в инвентаре.
     *
     * @param player игрок, инвентарь которого нужно проверить
     * @return true, если есть хотя бы один пустой слот; false, если инвентарь полностью заполнен
     */
    public boolean hasFreeSpace(Player player) {
        Inventory inventory = player.getInventory();
        // Если метод firstEmpty() возвращает -1, значит, инвентарь заполнен
        return inventory.firstEmpty() != -1;
    }

    /**
     * Проверяет, есть ли у игрока место для определенного предмета.
     *
     * @param player игрок, чей инвентарь проверяется
     * @param itemStack предмет, для которого нужно проверить наличие места
     * @return true, если есть место для предмета, false в противном случае
     */
    public boolean hasSpaceForItem(Player player, ItemStack itemStack) {
        Inventory inventory = player.getInventory();
        Material itemType = itemStack.getType();
        int maxStackSize = itemStack.getMaxStackSize(); // Максимальное количество предметов в стаке
        int amountToAdd = itemStack.getAmount(); // Количество предметов, которое нужно добавить

        // Проходим по всем слотам инвентаря игрока
        for (ItemStack currentItem : inventory.getContents()) {
            if (currentItem == null) {
                // Пустой слот - можно поместить предмет
                return true;
            }
            if (currentItem.getType() == itemType && currentItem.getAmount() < maxStackSize) {
                // Слот содержит такой же предмет, но он не полностью заполнен
                int spaceLeft = maxStackSize - currentItem.getAmount();
                if (spaceLeft >= amountToAdd) {
                    return true;
                } else {
                    amountToAdd -= spaceLeft;
                }
            }
        }

        // Проверяем, если после всех расчетов осталось количество, которое некуда положить
        return amountToAdd <= 0;
    }

    /**
     * Забирает у игрока один предмет с указанным материалом.
     *
     * @param player   игрок, у которого нужно забрать предмет
     * @param material материал предмета, который нужно забрать
     * @return true, если предмет был найден и успешно удален; false, если предмет не найден
     */
    public static boolean removeItem(Player player, Material material) {
        Inventory inventory = player.getInventory();

        // Проходимся по предметам в инвентаре
        for (ItemStack item : inventory.getContents()) {
            // Если предмет найден и его материал соответствует искомому
            if (item != null && item.getType() == material) {
                // Уменьшаем количество предметов на 1 или удаляем стек полностью
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.remove(item);
                }
                return true; // Предмет найден и удален
            }
        }
        return false; // Предмет не найден
    }

    /**
     * Проверяет, есть ли в инвентаре игрока предмет с указанным ключевым словом.
     *
     * @param player         игрок, чей инвентарь проверяется
     * @param keyword        ключевое слово (например, "PICKAXE")
     * @return true, если предмет с таким ключевым словом найден
     */
    public static boolean hasItemWithKeyword(Player player, String keyword) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().toString().contains(keyword.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает предмет из инвентаря игрока, который содержит указанное ключевое слово.
     *
     * @param player         игрок, чей инвентарь проверяется
     * @param keyword        ключевое слово (например, "PICKAXE")
     * @return ItemStack, если предмет с таким ключевым словом найден; null, если предмет не найден
     */
    public static ItemStack getItemWithKeyword(Player player, String keyword) {
        if (keyword.equalsIgnoreCase("axe")) { keyword = "_AXE"; }
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().toString().contains(keyword.toUpperCase())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Открывает инвентарь магазина с динамическими предметами.
     *
     * @param player игрок, для которого открывается магазин
     */
    public void openShopInventory(Player player) {
        FileConfiguration itemsConfig = configUtils.getCustomConfig("items");
        Inventory shopInventory = Bukkit.createInventory(null, 27, "Магазин");

        String[] itemTypes = {"SWORD", "PICKAXE", "AXE", "SHOVEL", "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET"};
        for (String itemType : itemTypes) {
            int itemLevel = (int) configUtils.getItemLevel(player, itemType);
            addItemToShop(player, shopInventory, itemType, itemLevel, itemsConfig);
        }
        // Заполняем пустые слоты серым стеклом
        ItemStack filler = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < shopInventory.getSize(); i++) {
            if (shopInventory.getItem(i) == null) {
                shopInventory.setItem(i, filler);
            }
        }

        player.openInventory(shopInventory);
    }

    private void addItemToShop(Player player, Inventory shopInventory, String itemType, int itemLevel, FileConfiguration itemsConfig) {
        String materialPath = "items." + itemType + ".levels." + itemLevel + ".material";
        String materialName = itemsConfig.getString(materialPath);
        if (materialName == null) return;

        Material material = Material.getMaterial(materialName);
        if (material == null) return;

        String namePath = "items." + itemType + ".descriptions." + materialName + ".name";
        String lorePath = "items." + itemType + ".descriptions." + materialName + ".lore";
        String name = itemsConfig.getString(namePath, itemType);
        String lore = itemsConfig.getString(lorePath, "Описание отсутствует");

        int price = itemsConfig.getInt("items." + itemType + ".levels." + itemLevel + ".price");
        int blocks = itemsConfig.getInt("items." + itemType + ".levels." + itemLevel + ".blocks");
        int enchantmentLevel = itemsConfig.getInt("items." + itemType + ".levels." + itemLevel + ".enchantment-level");

        int playerBalance = configUtils.getPlayerMoney(player);
        int playerBlocks = configUtils.getPlayerBlocks(player);

        ChatColor priceColor = playerBalance >= price ? ChatColor.GREEN : ChatColor.RED;
        ChatColor blocksColor = playerBlocks >= blocks ? ChatColor.GREEN : ChatColor.RED;
        ChatColor itemColor = (playerBalance >= price && playerBlocks >= blocks) ? ChatColor.GREEN : ChatColor.RED;

        ItemStack shopItem = new ItemStack(material);
        ItemMeta meta = shopItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemColor + name);
            String enchantmentName = "Зачарование: ";
            switch (itemType) {
                case "PICKAXE":
                case "AXE":
                case "SHOVEL":
                    if (enchantmentLevel > 0) meta.addEnchant(Enchantment.DIG_SPEED, enchantmentLevel, true);
                    enchantmentName = "Эффективность: ";
                    break;
                case "SWORD":
                    if (enchantmentLevel > 0) meta.addEnchant(Enchantment.DAMAGE_ALL, enchantmentLevel, true);
                    enchantmentName = "Острота: ";
                    break;
                case "BOOTS":
                case "LEGGINGS":
                case "CHESTPLATE":
                case "HELMET":
                    if (enchantmentLevel > 0) meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel, true);
                    enchantmentName = "Защита: ";
                    break;
            }

            meta.setLore(Arrays.asList(ChatColor.GRAY + lore,
                    ChatColor.YELLOW + "Цена: " + priceColor + ConfigUtils.formatNumberStatic(playerBalance) + ChatColor.GRAY + "/" + ConfigUtils.formatNumberStatic(price) + " монет",
                    ChatColor.YELLOW + "Блоки: " + blocksColor + ConfigUtils.formatNumberStatic(playerBlocks) + ChatColor.GRAY + "/" + ConfigUtils.formatNumberStatic(blocks) + " блоков",
                    ChatColor.BLUE + enchantmentName + enchantmentLevel));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            shopItem.setItemMeta(meta);
            NMS.setNBTTagInt(shopItem, "price", price);
            NMS.setNBTTagInt(shopItem, "blocks", blocks);
        }

        int slot = itemsConfig.getInt("items." + itemType + ".slot", 11);
        shopInventory.setItem(slot, shopItem);

    }

    public void buyItemInShop(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            int balance = configUtils.getPlayerMoney(player);
            int blocks = configUtils.getPlayerBlocks(player);
            int price = NMS.getNBTTagInt(item, "price");
            int needBlocks = NMS.getNBTTagInt(item, "blocks");
            String itemType = getItemTypeFromMaterial(item.getType());

            if (balance >= price && blocks >= needBlocks) {
                givePlayerBoughtItem(player, item, price, itemType);
            } else if (balance < price) {
                player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-money"));
            } else if (blocks < needBlocks) {
                player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-blocks"));
            }
        }
    }

    private void givePlayerBoughtItem(Player player, ItemStack item, int price, String itemType) {
        PlayerInventory playerInventory = player.getInventory();
        if (hasFreeSpace(player)) {
            if (hasItemWithKeyword(player, itemType)) {
                ItemStack existingItem = getItemWithKeyword(player, itemType);
                playerInventory.remove(existingItem);
            }

            configUtils.takePlayerMoney(player, price);
            int itemLevel = configUtils.getItemLevel(player, itemType);
            configUtils.setItemLevel(player, itemType, itemLevel + 1);

            ItemStack newItem = new ItemStack(item.getType());
            ItemMeta meta = newItem.getItemMeta();
            if (meta != null) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
                meta.setUnbreakable(true);
                int enchantmentLevel = 0;
                switch (itemType) {
                    case "PICKAXE":
                    case "AXE":
                    case "SHOVEL":
                        enchantmentLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
                        meta.setLore(Arrays.asList(ChatColor.BLUE + "Эффективность: " + enchantmentLevel));
                        meta.addEnchant(Enchantment.DIG_SPEED, enchantmentLevel, true);
                        break;
                    case "SWORD":
                        enchantmentLevel = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                        meta.setLore(Arrays.asList(ChatColor.BLUE + "Острота: " + enchantmentLevel));
                        meta.addEnchant(Enchantment.DAMAGE_ALL, enchantmentLevel, true);
                        break;
                    case "BOOTS":
                    case "LEGGINGS":
                    case "CHESTPLATE":
                    case "HELMET":
                        enchantmentLevel = item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                        meta.setLore(Arrays.asList(ChatColor.BLUE + "Защита: " + enchantmentLevel));
                        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, enchantmentLevel, true);
                        break;
                }
                newItem.setItemMeta(meta);
            }

            // Надеваем броню, если это броня
            if (itemType.equals("HELMET") || itemType.equals("CHESTPLATE") || itemType.equals("LEGGINGS") || itemType.equals("BOOTS")) {
                switch (itemType) {
                    case "HELMET":
                        playerInventory.setHelmet(newItem);
                        break;
                    case "CHESTPLATE":
                        playerInventory.setChestplate(newItem);
                        break;
                    case "LEGGINGS":
                        playerInventory.setLeggings(newItem);
                        break;
                    case "BOOTS":
                        playerInventory.setBoots(newItem);
                        break;
                }
            } else {playerInventory.addItem(newItem);}

            updateShopInventory(player, "Магазин");
            player.sendMessage(ChatColor.GREEN + configUtils.getMessageFromConfig("item-successfully-bought"));
        } else {
            player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-space-in-inventory"));
        }
    }

    public void givePlayerStartShears(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        if (hasFreeSpace(player) && !hasItemWithKeyword(player, "SHEARS")) {

            configUtils.setItemLevel(player, "SHEARS", 1);

            ItemStack newItem = new ItemStack(Material.SHEARS);
            ItemMeta meta = newItem.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
                int enchantmentLevel = 0;
                meta.setLore(Arrays.asList(ChatColor.BLUE + "Эффективность: " + enchantmentLevel));
                meta.addEnchant(Enchantment.DIG_SPEED, enchantmentLevel, true);
                }
                newItem.setItemMeta(meta);
                playerInventory.addItem(newItem);
        }
    }

    public void givePlayerMenuStar(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        int targetSlot = 8; // 9-й слот инвентаря (индексация с 0)

        // Проверяем, что у игрока еще нет предмета с таким названием.
        if (!hasItemWithKeyword(player, "NETHER_STAR")) {

            // Создание предмета
            ItemStack menuStar = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = menuStar.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.BLUE + "Меню");
                meta.setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.setLore(Arrays.asList(ChatColor.YELLOW + "ПКМ для открытия меню"));
                menuStar.setItemMeta(meta);
            }
            NMS.setNBTTagBoolean(menuStar, "isMenuItem", true);

            // Проверяем, пуст ли 9-й слот
            if (playerInventory.getItem(targetSlot) == null) {
                playerInventory.setItem(targetSlot, menuStar);
            } else {
                // Если 9-й слот занят, но есть свободные слоты в инвентаре, добавляем в свободный слот
                HashMap<Integer, ItemStack> remainingItems = playerInventory.addItem(menuStar);
            }
        }
    }



    public void sellBlocks(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] items = playerInventory.getContents();

        double totalAmount = 0;
        int playerLevel = configUtils.getPlayerLevel(player); // Получаем уровень игрока

        // Итерируем по всем предметам в инвентаре
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                Material material = item.getType();
                double price = configUtils.getBlockPrice(material);
                int blockLevel = configUtils.getBlockLevel(material);

                // Проверяем, есть ли материал в конфиге и соответствует ли уровень
                if (price > 0 && playerLevel >= blockLevel) {
                    // Вычисляем сумму продажи
                    int amount = item.getAmount();
                    totalAmount += amount * price;
                    // Удаляем блоки из инвентаря
                    playerInventory.remove(item);
                }
            }
        }

        configUtils.givePlayerMoney(player, (int) totalAmount);
        player.sendMessage(ChatColor.GREEN + "Вы продали блоки на сумму " + ChatColor.YELLOW + configUtils.formatNumber((int) totalAmount));
    }




    private void updateShopInventory(Player player, String shopTitle) {
        if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().equals(shopTitle)) {
            player.closeInventory();
        }
        openShopInventory(player);
    }

    private String getItemTypeFromMaterial(Material material) {
        if (material.toString().contains("SHEARS")) return "SHEARS";
        if (material.toString().contains("PICKAXE")) return "PICKAXE";
        if (material.toString().contains("SWORD")) return "SWORD";
        if (material.toString().contains("_AXE")) return "AXE";
        if (material.toString().contains("SHOVEL")) return "SHOVEL";
        if (material.toString().contains("BOOTS")) return "BOOTS";
        if (material.toString().contains("LEGGINGS")) return "LEGGINGS";
        if (material.toString().contains("CHESTPLATE")) return "CHESTPLATE";
        if (material.toString().contains("HELMET")) return "HELMET";
        return null;
    }


    public void openMinesMenu(Player player) {
        // Создаем инвентарь с размером 54 слота и названием "Шахты"
        Inventory menu = Bukkit.createInventory(null, 54, "Шахты");

        // Получаем список шахт из конфигурации
        List<Mine> mines = configUtils.getMines();

        // Заполняем инвентарь шахтами
        for (Mine mine : mines) {
            // Создаем предмет для шахты
            ItemStack item = new ItemStack(mine.getGuiMaterial());
            ItemMeta meta = item.getItemMeta();
            int minelevel = mine.getLevel();
            int playerlevel = configUtils.getPlayerLevel(player);
            String mineNumber = mine.getNumber();
            ChatColor mineColor = ChatColor.RED;
            if (playerlevel >= minelevel) mineColor = ChatColor.GREEN;

            if (meta != null) {
                meta.setDisplayName(mineColor + mine.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + mine.getDescription());
                lore.add(ChatColor.RESET + "");
                lore.add(ChatColor.GRAY + "Уровень: " + mineColor + String.valueOf(minelevel));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            NMS.setNBTTagString(item, "mineNumber", mineNumber);

            // Получаем слот для шахты и добавляем предмет в инвентарь
            int slot = mine.getSlot();
            if (slot >= 0 && slot < 54) {
                menu.setItem(slot, item);
            }

            // Заполняем незаполненные слоты серым стеклом
            ItemStack grayGlass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta grayMeta = grayGlass.getItemMeta();
            if (grayMeta != null) {
                grayMeta.setDisplayName(" "); // Без названия
                grayGlass.setItemMeta(grayMeta);
            }

            for (int i = 0; i < 54; i++) {
                if (menu.getItem(i) == null) {
                    menu.setItem(i, grayGlass);
                }
            }
        }

        // Открываем инвентарь для игрока
        player.openInventory(menu);
    }


    public void openLevelUpMenu(Player player) {
        // Получаем текущий уровень игрока
        int currentLevel = configUtils.getPlayerLevel(player);

        // Проверяем, существует ли следующий уровень
        if (!configUtils.getCustomConfig("levels").contains("levels." + (currentLevel + 1))) {
            player.sendMessage(ChatColor.RED + "Вы уже достигли максимального уровня!");
            return;
        }

        // Получаем данные следующего уровня
        int nextLevel = currentLevel + 1;
        double price = configUtils.getLevelPrice(nextLevel);
        int requiredBlocks = configUtils.getLevelBlocks(nextLevel);

        // Получаем текущее количество блоков и денег у игрока
        double playerMoney = configUtils.getPlayerMoney(player);
        int playerBlocks = configUtils.getPlayerBlocks(player);

        // Проверяем, хватает ли игроку блоков и денег
        boolean hasEnoughBlocks = playerBlocks >= requiredBlocks;
        boolean hasEnoughMoney = playerMoney >= price;

        // Создаем меню
        Inventory menu = Bukkit.createInventory(null, 9, "Повышение уровня");

        // Создаем пузырек опыта для повышения уровня
        ItemStack levelUpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = levelUpItem.getItemMeta();

        // Устанавливаем название предмета
        if (hasEnoughBlocks && hasEnoughMoney) {
            meta.setDisplayName(ChatColor.GREEN + "Повышение уровня");
        } else {
            meta.setDisplayName(ChatColor.RED + "Повышение уровня");
        }

        // Описание предмета
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Цена: " + (hasEnoughMoney ? ChatColor.GREEN : ChatColor.RED) + configUtils.formatNumber((long) price));
        lore.add(ChatColor.GRAY + "Блоки: " + (hasEnoughBlocks ? ChatColor.GREEN : ChatColor.RED) + configUtils.formatNumber((long) requiredBlocks));
        meta.setLore(lore);

        levelUpItem.setItemMeta(meta);

        // Добавляем пузырек опыта в центральный слот
        menu.setItem(4, levelUpItem);

        // Заполняем пустые слоты серым стеклом
        ItemStack filler = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, filler);
            }
        }

        // Открываем меню игроку
        player.openInventory(menu);
    }


    public void openMenu(Player player) {
        // Создаем меню
        Inventory menu = Bukkit.createInventory(null, 9, "Меню");

        // Создаем предметы для меню

        // Повышение уровня
        ItemStack levelUpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta lvlupMeta = levelUpItem.getItemMeta();
        lvlupMeta.setDisplayName(ChatColor.GREEN + "Повышение уровня");

        // Описание предмета
        List<String> lvlupLore = new ArrayList<>();
        lvlupLore.add(ChatColor.GRAY + "Повышение уровня");
        lvlupMeta.setLore(lvlupLore);
        levelUpItem.setItemMeta(lvlupMeta);

        menu.setItem(4, levelUpItem);

        // Продажа блоков
        ItemStack sellBlocksItem = new ItemStack(Material.DIAMOND);
        ItemMeta sellBlocksMeta = levelUpItem.getItemMeta();
        sellBlocksMeta.setDisplayName(ChatColor.GREEN + "Продажа блоков");

        // Описание предмета
        List<String> sellBlocksLore = new ArrayList<>();
        sellBlocksLore.add(ChatColor.GRAY + "Продажа блоков");
        sellBlocksMeta.setLore(sellBlocksLore);
        sellBlocksItem.setItemMeta(sellBlocksMeta);

        menu.setItem(1, sellBlocksItem);

        // Магазин
        ItemStack shopItem = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta shopMeta = shopItem.getItemMeta();
        shopMeta.setDisplayName(ChatColor.GREEN + "Магазин");

        // Описание предмета
        List<String> shopLore = new ArrayList<>();
        shopLore.add(ChatColor.GRAY + "Магазин");
        shopMeta.setLore(shopLore);
        shopItem.setItemMeta(shopMeta);

        menu.setItem(7, shopItem);

        // Заполняем пустые слоты серым стеклом
        ItemStack filler = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < menu.getSize(); i++) {
            if (menu.getItem(i) == null) {
                menu.setItem(i, filler);
            }
        }

        // Открываем меню игроку
        player.openInventory(menu);
    }

    public void levelUp(Player player) {
        int currentLevel = configUtils.getPlayerLevel(player);
        int nextLevel = currentLevel + 1;

        // Проверяем, существует ли следующий уровень
        if (!configUtils.getCustomConfig("levels").contains("levels." + nextLevel)) {
            player.sendMessage(ChatColor.RED + "Вы уже достигли максимального уровня!");
            return;
        }

        double price = configUtils.getLevelPrice(nextLevel);
        int requiredBlocks = configUtils.getLevelBlocks(nextLevel);

        double playerMoney = configUtils.getPlayerMoney(player);
        int playerBlocks = configUtils.getPlayerBlocks(player);

        // Проверяем, хватает ли игроку ресурсов
        if (playerBlocks >= requiredBlocks && playerMoney >= price) {
            // Списываем финансы
            configUtils.takePlayerMoney(player, (int) price);

            // Повышаем уровень игрока
            configUtils.givePlayerLevel(player, 1);

            player.sendMessage(ChatColor.GREEN + "Вы повысили уровень до " + nextLevel + "!");
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Вы не можете повысить уровень.");
        }
    }
}
