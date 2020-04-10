package cn.whiteg.rpgArmour.reqest;

import cn.whiteg.mmocore.container.PlayerReqest;
import cn.whiteg.rpgArmour.utils.RideManage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RideReqest extends PlayerReqest {
    public RideReqest(Player player,Player player1) {
        super(player,player1,"ride");
    }

    @Override
    public void onAccept() {
        Location loc1 = getPlayer().getLocation();
        Location loc2 = getSender().getLocation();
        if (!loc1.getWorld().equals(loc2.getWorld()) || loc1.distance(loc2) > 64){
            getPlayer().sendMessage("§b错误，你们距离太远了");
            return;
        }
        if (RideManage.Ride(getSender(),getPlayer())){
            getSender().sendMessage(getPlayer().getDisplayName() + "§r§b已接受");
            getPlayer().sendMessage("§b已接受§r" + getPlayer().getDisplayName() + "§r§b的请求");
        } else {
            getSender().sendMessage(getPlayer().getDisplayName() + "§r§b没有骑上对方");
            getPlayer().sendMessage("§b已接受§r" + getPlayer().getDisplayName() + "§r§b没有骑上对方");

        }

//                    p2.sendMessage("§b正在传送至" + p1.getDisplayName());
    }
}
