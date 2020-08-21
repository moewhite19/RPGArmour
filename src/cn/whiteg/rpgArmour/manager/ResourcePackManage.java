package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.Setting;
import net.minecraft.server.v1_16_R2.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static cn.whiteg.rpgArmour.RPGArmour.logger;

public class ResourcePackManage {
    public static void set(String s,String s1) {
        Server ser = Bukkit.getServer();
        try{
            Field console_f = ser.getClass().getDeclaredField("console");
            console_f.setAccessible(true);
            DedicatedServer con = (DedicatedServer) console_f.get(ser);
            con.setResourcePack(s,s1);

            FileConfiguration st = Setting.getStorage();
            ConfigurationSection sc = st.createSection("resourcepack");
            sc.set("url",s);
            sc.set("sha1",s1);
            Setting.saveStorage();
            logger.info("设置资源包 " + s + "  " + s1);
        }catch (Exception e){
            logger.info("设置资源包" + e.getMessage());
        }
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

    public static void set() {
        String[] sc = get();
        if (sc != null){
            set(sc[0],sc[1]);
        }
    }

    public static String[] get() {
        FileConfiguration st = Setting.getStorage();
        ConfigurationSection sc = st.getConfigurationSection("resourcepack");
        if (sc != null){
            return new String[]{sc.getString("url"),sc.getString("sha1")};
        }
        return null;
    }

    public static void sendPack(Player p,String url,String sha1) {
        byte[] bytes = toBytes(sha1);
        if (bytes == null) p.setResourcePack(url);
        else p.setResourcePack(url,bytes);
    }
}
