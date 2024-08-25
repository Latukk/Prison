package org.latuk.prison.utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.latuk.prison.Prison;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class NMS {

    private final Prison plugin;
    public NMS(Prison plugin) {
        this.plugin = plugin;
    }


    /**
     * Устанавливает NBT тег на предмет.
     *
     * @param item  предмет
     * @param key   ключ тега
     * @param value значение тега
     */
    public static void setNBTTagString(ItemStack item, String key, String value) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtTagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        nbtTagCompound.setString(key, value);
        nmsItemStack.setTag(nbtTagCompound);
        item.setItemMeta(CraftItemStack.getItemMeta(nmsItemStack));  // Обновляем ItemStack
    }

    public static void setNBTTagInt(ItemStack item, String key, int value) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtTagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        nbtTagCompound.setInt(key, value);
        nmsItemStack.setTag(nbtTagCompound);
        item.setItemMeta(CraftItemStack.getItemMeta(nmsItemStack));  // Обновляем ItemStack
    }

    public static void setNBTTagBoolean(ItemStack item, String key, boolean value) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtTagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        nbtTagCompound.setBoolean(key, value);
        nmsItemStack.setTag(nbtTagCompound);
        item.setItemMeta(CraftItemStack.getItemMeta(nmsItemStack));  // Обновляем ItemStack
    }

    /**
     * Получает значение NBT тега.
     *
     * @param item предмет
     * @param key  ключ тега
     * @return значение тега
     */
    public static String getNBTTagString(ItemStack item, String key) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        if (nmsItemStack.hasTag()) {
            NBTTagCompound nbtTagCompound = nmsItemStack.getTag();
            return nbtTagCompound.getString(key);
        }
        return null;
    }

    public static int getNBTTagInt(ItemStack item, String key) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        if (nmsItemStack.hasTag()) {
            NBTTagCompound nbtTagCompound = nmsItemStack.getTag();
            return nbtTagCompound.getInt(key);
        }
        return 0;
    }

    public static boolean getNBTTagBoolean(ItemStack item, String key) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        if (nmsItemStack.hasTag()) {
            NBTTagCompound nbtTagCompound = nmsItemStack.getTag();
            return nbtTagCompound.getBoolean(key);
        }
        return false;
    }


    /**
     * Удаляет NBT тег с предмета.
     *
     * @param item  предмет
     * @param key   ключ тега
     */
    public static void removeNBTTag(ItemStack item, String key) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbtTagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        nbtTagCompound.remove(key);
        nmsItemStack.setTag(nbtTagCompound);
        item.setItemMeta(CraftItemStack.getItemMeta(nmsItemStack));  // Обновляем ItemStack
    }


    /**
     * Обновляет ItemStack Bukkit из NMS ItemStack.
     *
     * @param bukkitItemStack предмет Bukkit
     * @param nmsItemStack    предмет NMS
     */
    private static void updateBukkitItemStack(ItemStack bukkitItemStack, net.minecraft.server.v1_16_R3.ItemStack nmsItemStack) {
        try {
            java.lang.reflect.Field field = bukkitItemStack.getClass().getDeclaredField("handle");
            field.setAccessible(true);
            field.set(bukkitItemStack, nmsItemStack);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
