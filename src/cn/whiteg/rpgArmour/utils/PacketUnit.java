package cn.whiteg.rpgArmour.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.entity.Player;

public class PacketUnit {
    public static void sendPacket(Packet<?> packet,Player player) {
        if (player.isOnline()){
            EntityPlayer nmsPlayer = EntityUtils.getNmsPlayer(player);
            var pc = EntityUtils.getPlayerConnection(nmsPlayer);
            pc.b(packet);
        }
    }

    public static void sendPacket(Packet<?> packet,PlayerConnection connection) {
        connection.b(packet);
    }
}
