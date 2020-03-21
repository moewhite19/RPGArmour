package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
public class Hook extends CustItem_CustModle implements Listener {

    public Hook() {
        super(Material.FISHING_ROD,1,"§5随心所欲杆");
    }

    @EventHandler
    public void onHook(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof FishHook){
            final FishHook fh = (FishHook) event.getEntity();
            if (!(fh.getShooter() instanceof Player)) return;
            final Player p = (Player) fh.getShooter();
            if (is(p.getInventory().getItemInMainHand())){
                fh.addPassenger(p);
            }
        }
    }
}


