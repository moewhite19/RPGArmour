package cn.whiteg.rpgArmour.utils;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {
    public static void sendPacket(Packet packet,Player player) {
        if (player.isOnline()){
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            PlayerConnection playerConnection = nmsPlayer.playerConnection;
            playerConnection.sendPacket(packet);
        }
    }
}
