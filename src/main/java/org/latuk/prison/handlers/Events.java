package org.latuk.prison.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.latuk.prison.Prison;
import org.latuk.prison.utils.ConfigUtils;
import org.latuk.prison.utils.InventoryUtils;
import org.latuk.prison.utils.MineManager;
import org.latuk.prison.utils.NMS;

import java.util.Map;

public class Events implements Listener {
    private final Prison plugin;
    private final ConfigUtils configUtils;
    private final NMS nms;
    private final InventoryUtils inventoryUtils;
    private final MineManager mineManager;
    public Events(Prison plugin, ConfigUtils configUtils, NMS nms, InventoryUtils inventoryUtils, MineManager mineManager) {
        this.plugin = plugin;
        this.configUtils = configUtils;
        this.nms = nms;
        this.inventoryUtils = inventoryUtils;
        this.mineManager = mineManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!configUtils.isPlayerExistsInCFG(player)) configUtils.addNewPlayerToCFG(player);
        inventoryUtils.givePlayerStartShears(player);
        inventoryUtils.givePlayerMenuStar(player);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Магазин")) {
            event.setCancelled(true); // Предотвращаем перемещение предметов в магазине

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE) return;

            Player player = (Player) event.getWhoClicked();
            inventoryUtils.buyItemInShop(player, clickedItem);
        }

        if (event.getView().getTitle().equals("Шахты")) {
            event.setCancelled(true); // Предотвращаем перемещение предметов в меню

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE) return;

            Player player = (Player) event.getWhoClicked();
            mineManager.teleportToMine(player, NMS.getNBTTagString(clickedItem, "mineNumber"));
        }

        if (event.getView().getTitle().equals("Повышение уровня")) {
            event.setCancelled(true); // Запрещаем любые действия в этом меню

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Проверяем, что игрок кликнул по пузырьку опыта
            if (clickedItem != null && clickedItem.getType() == Material.EXPERIENCE_BOTTLE) {
                inventoryUtils.levelUp(player);
            }
        }

        if (event.getView().getTitle().equals("Меню")) {
            event.setCancelled(true); // Запрещаем любые действия в этом меню

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() == Material.EXPERIENCE_BOTTLE) {
                player.closeInventory();
                inventoryUtils.openLevelUpMenu(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND) {
                inventoryUtils.sellBlocks(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND_PICKAXE) {
                player.closeInventory();
                inventoryUtils.openShopInventory(player);
            }
        }


        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // Проверяем, если слот связан с бронёй
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);

            // Убираем предмет с курсора, если игрок пытается его переместить
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.LEFT) {
                event.setCursor(null);
            }
        }

        // Если игрок пытается надеть другую броню поверх заблокированной
        if (clickedItem != null && clickedItem.getType() != Material.AIR &&
                isArmorItem(clickedItem) && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        // Отменяем любое взаимодействие с броней
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Предотвращаем попытку надеть или снять броню через быстрые клавиши
        if (itemInHand.getType() != Material.AIR && isArmorItem(itemInHand)) {
            event.setCancelled(true);
        }


        if (NMS.getNBTTagBoolean(itemInHand, "isMenuItem")) {
            event.setCancelled(true); // Отменяем стандартное действие
            inventoryUtils.openMenu(player); // Функция для открытия меню
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        // Проверяем, является ли выброшенный предмет инструментом
        if (isToolItem(droppedItem)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (configUtils.isBlockInMine(block)) {
            Inventory playerInventory = player.getInventory();
            ItemStack item = new ItemStack(block.getType());

            if (inventoryUtils.hasSpaceForItem(player, item)) {
                Map<Integer, ItemStack> leftoverItems = playerInventory.addItem(item);

                if (leftoverItems.isEmpty()) {
                    // Если все предметы были добавлены успешно, меняем блок на воздух
                    block.setType(Material.AIR);
                    configUtils.givePlayerBlocks(player, 1);
                } else {
                    player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-space-in-inventory"));
                }
            } else {
                player.sendMessage(ChatColor.RED + configUtils.getMessageFromConfig("not-enough-space-in-inventory"));
            }
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        event.setCancelled(true);
    }


    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);

        event.getBlock().setType(Material.AIR);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Отменяем событие спавна мобов
        event.setCancelled(true);
    }

    // Проверяем, является ли предмет инструментом
    private boolean isToolItem(ItemStack item) {
        if (item == null) return false;
        switch (item.getType()) {
            case SHEARS:
            case NETHER_STAR:
            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
                return true;
            default:
                return false;
        }
    }

    // Проверяем, является ли предмет бронёй
    private boolean isArmorItem(ItemStack item) {
        if (item == null) return false;
        switch (item.getType()) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_LEGGINGS:
            case GOLDEN_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return true;
            default:
                return false;
        }
    }
}
