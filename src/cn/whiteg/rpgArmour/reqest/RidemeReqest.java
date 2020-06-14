package cn.whiteg.rpgArmour.reqest;

import cn.whiteg.mmocore.container.PlayerReqest;
import cn.whiteg.rpgArmour.utils.RideManage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RidemeReqest extends PlayerReqest {
    public RidemeReqest(Player player) {
        super(player,"rideme");
    }

    @Override
    public void onAccept(CommandSender sender) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            Location loc1 = player.getLocation();
            Location loc2 = getSender().getLocation();
            if (!loc1.getWorld().getName().equals(loc2.getWorld().getName()) || loc1.distance(loc2) > 64){
                player.sendMessage("§b错误，你们距离太远了");
                return;
            }
            RideManage.Ride(player,getSender());
            getSender().sendMessage(player.getDisplayName() + "§r§b已接受");
            player.sendMessage("§b已接受§r" + getSender().getDisplayName() + "§r§b的请求");
        }

    }
}
