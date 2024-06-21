package cn.whiteg.rpgArmour.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

public class PacketUnit {
    public static void sendPacket(Packet<?> packet,Player player) {
        if (player.isOnline()){
            ServerPlayer nmsPlayer = EntityUtils.getNmsPlayer(player);
            var pc = EntityUtils.getServerGamePacketListenerImpl(nmsPlayer);
            pc.sendPacket(packet);
        }
    }

    public static void sendPacket(Packet<?> packet,ServerGamePacketListenerImpl connection) {
        connection.sendPacket(packet);
    }
}
