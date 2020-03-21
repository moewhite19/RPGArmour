package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Optional;

import static cn.whiteg.rpgArmour.RPGArmour.logger;

public class ResourcePackManage {
    static Method setPacketMethod;

    static {
        for (Method method : DedicatedServer.class.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 2 && (parameterTypes[0] == String.class && parameterTypes[1] == String.class)){
                setPacketMethod = method;
                break;
            }
        }
    }

    public static void set(String url,String sha1) {
        set(url,sha1,Setting.require,Setting.prompt);
    }

    public static void set(String url,String sha1,boolean require,String prompt) {
        DedicatedServer con = NMSUtils.getNmsServer();
        con.u.a().S = Optional.of(new MinecraftServer.ServerResourcePackInfo(url,sha1,require,IChatBaseComponent.a(prompt)));
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
