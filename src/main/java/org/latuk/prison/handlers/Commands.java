package org.latuk.prison.handlers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.latuk.prison.Prison;
import org.latuk.prison.utils.InventoryUtils;

public class Commands implements CommandExecutor {
    private final Prison plugin;
    private final InventoryUtils inventoryUtils;

    public Commands(Prison plugin, InventoryUtils inventoryUtils) {
        this.plugin = plugin;
        this.inventoryUtils = inventoryUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("shop")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                inventoryUtils.openShopInventory(player);
            } else {
                Bukkit.getLogger().warning("Это команда для игрока!");
            }
        } else if (command.getName().equalsIgnoreCase("sell")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                inventoryUtils.sellBlocks(player);
            } else {
                Bukkit.getLogger().warning("Это команда для игрока!");
            }
        } else if (command.getName().equalsIgnoreCase("mines")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                inventoryUtils.openMinesMenu(player);
            } else {
                Bukkit.getLogger().warning("Это команда для игрока!");
            }
        } else if (command.getName().equalsIgnoreCase("lvl")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                inventoryUtils.openLevelUpMenu(player);
            } else {
                Bukkit.getLogger().warning("Это команда для игрока!");
            }
        } else if (command.getName().equalsIgnoreCase("menu")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                inventoryUtils.openMenu(player);
            } else {
                Bukkit.getLogger().warning("Это команда для игрока!");
            }
        }
        return true;
    }
}
