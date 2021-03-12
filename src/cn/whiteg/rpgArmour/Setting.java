package cn.whiteg.rpgArmour;

import cn.whiteg.mmocore.sound.Sound;
import cn.whiteg.rpgArmour.api.CustEntity;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.custItems.BambooDragonfly;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class Setting {
    private final static int CONFIGVER = 9;
    public static boolean DEBUG;
    public static boolean forgeResourcePack = false;
    private static ConfigurationSection custItemConfit;
    private static ConfigurationSection custEntitySetting;
    private static FileConfiguration storage;
    private static FileConfiguration config;
    private static Map<Material, Sound> eatSoundMap = new EnumMap<>(Material.class);

    public static void reload() {
        File file = new File(RPGArmour.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        //config = RPGArmour.plugin.getConfig();
        //自动更新配置文件
        if (config.getInt("ver") < CONFIGVER){
            RPGArmour.plugin.saveResource("config.yml",true);
            config.set("ver",CONFIGVER);
            final FileConfiguration newcon = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = newcon.getKeys(true);
            for (String k : keys) {
                if (config.isSet(k)) continue;
                config.set(k,newcon.get(k));
                RPGArmour.logger.info("新增配置节点: " + k);
            }
            try{
                config.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        DEBUG = config.getBoolean("debug");
        BambooDragonfly.disableWorld.add(Bukkit.getWorld("world_the_end"));
        custItemConfit = config.getConfigurationSection("CustItemSetting");
        custEntitySetting = config.getConfigurationSection("CustEntitySetting");
        forgeResourcePack = config.getBoolean("forgeResourcePack",forgeResourcePack);

        //加载声音
        @Nullable ConfigurationSection cs = config.getConfigurationSection("EatSound");
        if (cs != null){
            for (String key : cs.getKeys(false)) {
                try{
                    Material mat = Material.valueOf(key.toUpperCase());
                    eatSoundMap.put(mat,Sound.parseYml(cs.get(key)));
                }catch (IllegalArgumentException e){
                    RPGArmour.logger.warning("无效配置ID: " + key);
                }
            }
        }

        file = new File(file.getParentFile(),"storage.yml");
        if (file.exists()){
            storage = YamlConfiguration.loadConfiguration(file);
        } else {
            storage = new YamlConfiguration();
        }
    }

    public static void saveStorage() {
        File file = new File(RPGArmour.plugin.getDataFolder(),"storage.yml");
        try{
            storage.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Map<Material, Sound> getEatSoundMap() {
        return eatSoundMap;
    }

    public static ConfigurationSection getCustEntitySetting(String name) {
        if (custEntitySetting != null)
            return custEntitySetting.getConfigurationSection(name);
        return null;
    }

    public static ConfigurationSection getCustEntitySetting(CustEntity custEntity) {
        ConfigurationSection c = getCustItemConfit();
        if (c != null) return c.getConfigurationSection(custEntity.getClass().getSimpleName());
        return null;
    }

    public static ConfigurationSection getCustItemConfit(String name) {
        if (custItemConfit != null)
            return custItemConfit;
        return null;
    }

    public static ConfigurationSection getCustItemConfig(CustItem custItem) {
        ConfigurationSection c = getCustItemConfit();
        if (c != null) return c.getConfigurationSection(custItem.getClass().getSimpleName());
        return null;
    }

    public static ConfigurationSection getCustItemConfit() {
        return custItemConfit;
    }

    public static ConfigurationSection getCustEntitySetting() {
        return custEntitySetting;
    }

    public static FileConfiguration getStorage() {
        return storage;
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}
