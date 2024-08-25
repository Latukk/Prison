package org.latuk.prison;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.latuk.prison.handlers.Commands;
import org.latuk.prison.handlers.Events;
import org.latuk.prison.utils.*;

import java.io.File;
import java.util.List;

public final class Prison extends JavaPlugin implements Listener {

    private ConfigUtils configUtils;
    private NMS nms;
    private InventoryUtils inventoryUtils;
    private FileConfiguration config;
    private File configFile;

    @Override
    public void onEnable() {
        configUtils = new ConfigUtils(this);
        nms = new NMS(this);
        new ScoreboardManager(this, configUtils);
        new MineManager(this, configUtils);
        inventoryUtils = new InventoryUtils(this, configUtils);
        getServer().getPluginManager().registerEvents(new Events(this, configUtils, nms, inventoryUtils, new MineManager(this, configUtils)), this);
        getCommand("shop").setExecutor(new Commands(this, inventoryUtils));
        getCommand("sell").setExecutor(new Commands(this, inventoryUtils));
        getCommand("mines").setExecutor(new Commands(this, inventoryUtils));
        getCommand("lvl").setExecutor(new Commands(this, inventoryUtils));
        getCommand("menu").setExecutor(new Commands(this, inventoryUtils));


        // Загружаем основные конфиги при старте плагина
        String[] cfgs = {"players", "levels", "mines", "items", "blocks", "messages", "scoreboard"};
        for (String cfg: cfgs) configUtils.setupConfig(cfg);
    }

}
