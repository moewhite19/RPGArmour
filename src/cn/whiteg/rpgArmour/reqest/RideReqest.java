package cn.whiteg.rpgArmour.reqest;

import cn.whiteg.mmocore.container.PlayerReqest;
import cn.whiteg.rpgArmour.utils.RideManage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RideReqest extends PlayerReqest {
    public RideReqest(Player p) {
        super(p,"ride");
    }

    @Override
    public void onAccept(CommandSender sender) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            Location loc1 = p.getLocation();
            Location loc2 = getSender().getLocation();
            if (!loc1.getWorld().equals(loc2.getWorld()) || loc1.distance(loc2) > 64){
                p.sendMessage("§b错误，你们距离太远了");
                return;
            }
            if (RideManage.Ride(getSender(),p)){
                getSender().sendMessage(p.getDisplayName() + "§r§b已接受");
                p.sendMessage("§b已接受§r" + p.getDisplayName() + "§r§b的请求");
            } else {
                getSender().sendMessage(p.getDisplayName() + "§r§b没有骑上对方");
                p.sendMessage("§b已接受§r" + p.getDisplayName() + "§r§b没有骑上对方");

            }
        }


//                    p2.sendMessage("§b正在传送至" + p1.getDisplayName());
    }
}
