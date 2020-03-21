package cn.whiteg.rpgArmour.custItems;

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
        if (event.getEntity() instanceof final FishHook fh){
            if (!(fh.getShooter() instanceof final Player p)) return;
            if (is(p.getInventory().getItemInMainHand())){

                if (p.isSneaking()){
//                    final Location location = fh.getLocation();
//                    final Boat boat = location.getWorld().spawn(location,Boat.class);
//                    if (!boat.isDead()){
//                        fh.addPassenger(boat);
//                    }
                    return;
                }

                if (p.getVehicle() != null){
                    fh.addPassenger(p.getVehicle());
                    return;
                }

                fh.addPassenger(p);

            }
        }
    }

}


