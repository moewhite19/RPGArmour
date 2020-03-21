package cn.whiteg.rpgArmour.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.entity.Player;

public class Utils {
    public static void sendPacket(Packet<?> packet,Player player) {
        if (player.isOnline()){
            EntityPlayer nmsPlayer = EntityUtils.getNmsPlayer(player);
            var pc = EntityUtils.getPlayerConnection(nmsPlayer);
            pc.a(packet);
        }
    }
}
