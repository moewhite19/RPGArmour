package cn.whiteg.rpgArmour.utils;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class ItemTypeUtils {
    private static Set<Material> shulkerboxs = new HashSet<>();
    private static Set<Material> doors = new HashSet<>();
    private static Set<Material> containers = new HashSet<>();
    private static Set<Material> buttons = new HashSet<>();
    private static Set<Material> beds = new HashSet<>();
    private static Set<Material> signs = new HashSet<>();
    private static Set<Material> redstons = new HashSet<>();
    private static Set<Material> boats = new HashSet<>();
    private static Set<Material> dyes = new HashSet<>();
    private static Set<Material> petteds = new HashSet<>();
    private static Set<Material> minecrafts = new HashSet<>();
    private static Set<Material> rails = new HashSet<>();
    private static Set<Material> banners = new HashSet<>();
    private static Set<Material> planks = new HashSet<>();
    private static Set<Material> wools = new HashSet<>();
    private static Set<Material> woods = new HashSet<>();
    private static Set<Material> logs = new HashSet<>();


    static {
        for (Material m : org.bukkit.Material.values()) {
            String name = m.toString();
            check(m,"shulker_box",shulkerboxs);
            check(m,"DOOR",doors);
            check(m,"GATE",doors);
            check(m,"BUTTON",buttons);
            check(m,"BED",beds);
            check(m,"SIGN",signs);
            check(m,"BOAT",boats);
            check(m,"DYE",dyes);
            check(m,"potted_",petteds);
            check(m,"MINECART",minecrafts);
            check(m,"RAIL",rails);
            check(m,"_BANNER",banners);
            check(m,"_PLANKS",planks);
            check(m,"_WOOL",wools);
            check(m,"_WOOD",woods);
            check(m,"_LOG",logs);
            buttons.add(Material.LEVER);
        }
        petteds.add(Material.FLOWER_POT);
        redstons.add(Material.REPEATER);
        redstons.add(Material.COMPARATOR);
        containers.addAll(shulkerboxs);
        containers.add(Material.FURNACE);
        containers.add(Material.BLAST_FURNACE);
        containers.add(Material.FURNACE_MINECART);
        containers.add(Material.SMOKER);
        containers.add(Material.BARREL);
        containers.add(Material.CHEST);
        containers.add(Material.TRAPPED_CHEST);
//        flagsMap.put(Material.CAKE,cake);
//        flagsMap.put(Material.BEACON,beacon);
//        flagsMap.put(Material.ANVIL,use);
//        addBlockSet(containers,container);
//        addBlockSet(redstons,redstone);
//        addBlockSet(buttons,button);
//        addBlockSet(doors,door);


    }

    public static Set<Material> getBanners() {
        return banners;
    }

    public static Set<Material> getSigns() {
        return signs;
    }

    public static Set<Material> getBeds() {
        return beds;
    }

    public static Set<Material> getButtons() {
        return buttons;
    }

    public static Set<Material> getContainers() {
        return containers;
    }

    public static Set<Material> getDoors() {
        return doors;
    }

    public static Set<Material> getShulkerboxs() {
        return shulkerboxs;
    }

    public static Set<Material> getPlanks() {
        return planks;
    }

    public static Set<Material> getRedstons() {
        return redstons;
    }

    public static Set<Material> getWools() {
        return wools;
    }

    public static Set<Material> getBoats() {
        return boats;
    }

    public static Set<Material> getDyes() {
        return dyes;
    }

    public static Set<Material> getPetteds() {
        return petteds;
    }

    public static Set<Material> getMinecrafts() {
        return minecrafts;
    }

    public static Set<Material> getRails() {
        return rails;
    }

    public static Set<Material> getWoods() {
        return woods;
    }

    public static Set<Material> getLogs() {
        return logs;
    }

    private static void check(Material mat,String name,Set<Material> mats) {
        if (mat.toString().contains(name.toUpperCase())) mats.add(mat);
    }
}
