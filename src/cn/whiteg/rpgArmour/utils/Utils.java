package cn.whiteg.rpgArmour.utils;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
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
