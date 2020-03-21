package cn.whiteg.rpgArmour.reqest;

import cn.whiteg.mmocore.container.PlayerReqest;
import cn.whiteg.rpgArmour.utils.RideManage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RidemeReqest extends PlayerReqest {
    public RidemeReqest(Player player,Player player1) {
        super(player,player1,"rideme");
    }

    @Override
    public void acceptEvent() {
        Location loc1 = getPlayer().getLocation();
        Location loc2 = getSender().getLocation();
        if (!loc1.getWorld().getName().equals(loc2.getWorld().getName()) || loc1.distance(loc2) > 64){
            getPlayer().sendMessage("§b错误，你们距离太远了");
            return;
        }
        RideManage.Ride(getPlayer(),getSender());
        getSender().sendMessage(getPlayer().getDisplayName() + "§r§b已接受");
        getPlayer().sendMessage("§b已接受§r" + getSender().getDisplayName() + "§r§b的请求");

//                    p2.sendMessage("§b正在传送至" + p1.getDisplayName());
    }
}
