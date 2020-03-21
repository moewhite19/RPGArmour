package cn.whiteg.rpgArmour.utils;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;


public class Utils {

    /**
     * @param className Name of the class
     * @return Class in {@code net.minecraft.server.[VERSION]} package with the specified name or {@code null} if the class was not found
     */
    public static Class<?> getNMSClass(String className) {
        try{
            return Class.forName("net.minecraft.server." + getServerVersion() + "." + className);
        }catch (ClassNotFoundException e){
            return null;
        }
    }

    /**
     * @param className Name of the class
     * @return Class in {@code org.bukkit.craftbukkit.[VERSION]} package with the specified name or {@code null} if the class was not found
     */
    public static Class<?> getCraftClass(String className) {
        try{
            return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + className);
        }catch (ClassNotFoundException e){
            return null;
        }
    }


    /**
     * Get a free entity ID for use in
     *
     * @return The id or {@code -1} if a free entity ID could not be retrieved.
     */
    public static int getFreeEntityId() {
        try{
            Class<?> entityClass = getNMSClass("Entity");
            Field entityCountField = entityClass.getDeclaredField("entityCount");
            entityCountField.setAccessible(true);
            if (entityCountField.getType() == int.class){
                int id = entityCountField.getInt(null);
                entityCountField.setInt(null,id + 1);
                return id;
            } else if (entityCountField.getType() == AtomicInteger.class){
                return ((AtomicInteger) entityCountField.get(null)).incrementAndGet();
            }

            return -1;
        }catch (Exception e){
            return -1;
        }
    }


    /**
     * Send a packet to a player
     *
     * @param packet Packet to send
     * @param player Player to which the packet should be sent
     * @return {@code true} if the packet was sent, or {@code false} if an exception was thrown
     */
    public static void sendPacket(Packet packet,Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = nmsPlayer.playerConnection;
        playerConnection.sendPacket(packet);
    }

    /**
     * @return The current server version with revision number (e.g. v1_9_R2, v1_10_R1)
     */
    public static String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();

        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }


    /**
     * Encodes an {@link ItemStack} in a Base64 String
     *
     * @param itemStack {@link ItemStack} to encode
     * @return Base64 encoded String
     */
    public static String encode(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i",itemStack);
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes an {@link ItemStack} from a Base64 String
     *
     * @param string Base64 encoded String to decode
     * @return Decoded {@link ItemStack}
     */
    public static ItemStack decode(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try{
            config.loadFromString(new String(Base64.getDecoder().decode(string),StandardCharsets.UTF_8));
        }catch (IllegalArgumentException | InvalidConfigurationException e){
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i",null);
    }


}
