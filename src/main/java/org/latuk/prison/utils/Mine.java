package org.latuk.prison.utils;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class Mine {
    private String number;
    private String name;
    private String description;
    private int level;
    private List<Material> materials;
    private World world;
    private Location spawn;
    private Location mineCorner1;
    private Location mineCorner2;
    private Material guiMaterial;
    private int slot;

    // Конструктор
    public Mine(String number, String name, String description, int level, List<Material> materials, World world,
                Location spawn, Location mineCorner1, Location mineCorner2, Material guiMaterial, int slot) {
        this.number = number;
        this.name = name;
        this.description = description;
        this.level = level;
        this.materials = materials;
        this.world = world;
        this.spawn = spawn;
        this.mineCorner1 = mineCorner1;
        this.mineCorner2 = mineCorner2;
        this.guiMaterial = guiMaterial;
        this.slot = slot;
    }

    // Геттеры для всех полей

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public World getWorld() {
        return world;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getMineCorner1() {
        return mineCorner1;
    }

    public Location getMineCorner2() {
        return mineCorner2;
    }

    public Material getGuiMaterial() {
        return guiMaterial;
    }

    public int getSlot() {
        return slot;
    }
}