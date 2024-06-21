package cn.whiteg.rpgArmour.manager;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.rpgArmour.Setting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static cn.whiteg.rpgArmour.RPGArmour.logger;

public class ResourcePackManage {
    static FieldAccessor<DedicatedServerSettings> server_setting;
    static FieldAccessor<DedicatedServerProperties> serverSetting_serverProp;
    static FieldAccessor<Optional<MinecraftServer.ServerResourcePackInfo>> serverProp_ServerPackInfo;

    static {
        try{
            server_setting = new FieldAccessor<>(ReflectUtil.getFieldFormType(DedicatedServer.class,DedicatedServerSettings.class));
            serverSetting_serverProp = new FieldAccessor<>(ReflectUtil.getFieldFormType(DedicatedServerSettings.class,DedicatedServerProperties.class));
        }catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }

        findField:
        {
            for (Field field : DedicatedServerProperties.class.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(Optional.class)){
                    serverProp_ServerPackInfo = new FieldAccessor<>(field);
                    break findField;
                }
            }
            throw new RuntimeException("Cant find field: serverProp_ServerPackInfo");
        }
    }

    public static void set(String url,String sha1) {
        set(url,sha1,Setting.require,Setting.prompt);
    }

    public static void set(String url,String sha1,boolean require,String prompt) {
        DedicatedServer con = NMSUtils.getNmsServer();
        final Optional<MinecraftServer.ServerResourcePackInfo> packInfo = Optional.of(new MinecraftServer.ServerResourcePackInfo(UUID.nameUUIDFromBytes(sha1.getBytes(StandardCharsets.UTF_8)),url,sha1,require,Component.literal(prompt)));
        final DedicatedServerSettings serverSettings = server_setting.get(con);
        final DedicatedServerProperties serverProperties = serverSetting_serverProp.get(serverSettings);
        serverProp_ServerPackInfo.set(serverProperties,packInfo);
//        con.u.a().S = Optional.of(new MinecraftServer.ServerResourcePackInfo(url,sha1,require,Component.a(prompt)));
        logger.info("设置资源包 " + url + "  " + sha1);
    }

    /**
     * 将SHA1字符串转换为十六进制数组
     *
     * @param str 字符串
     * @return 如果错误会输出null
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")){
            return null;
        }
        byte[] bytes;
        try{
            bytes = new byte[str.length() / 2];
            for (int i = 0; i < str.length() / 2; i++) {
                String subStr = str.substring(i * 2,i * 2 + 2);
                bytes[i] = (byte) Integer.parseInt(subStr,16);
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            return null;
        }
        return bytes;
    }

    public static void load() {
        String[] sc = getConfig();
        if (sc != null){
            set(sc[0],sc[1]);
        }
    }

    public static String[] getConfig() {
        FileConfiguration st = Setting.getStorage();
        ConfigurationSection sc = st.getConfigurationSection("resourcepack");
        if (sc != null){
            return new String[]{sc.getString("url"),sc.getString("sha1"),sc.getString("md5")};
        }
        return null;
    }

    public static void saveConfig(String url,String sha1,String md5) {
        FileConfiguration st = Setting.getStorage();
        ConfigurationSection sc = st.createSection("resourcepack");
        sc.set("url",url);
        sc.set("sha1",sha1);
        sc.set("md5",md5);
        Setting.saveStorage();
    }

    public static void sendPack(Player p,String url,String sha1) {
        byte[] bytes = toBytes(sha1);
        if (bytes == null) p.setResourcePack(url);
        else p.setResourcePack(url,bytes);
    }
}
