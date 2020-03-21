package cn.whiteg.rpgArmour.listener;

import cn.whiteg.moetp.utils.EntityTpUtils;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class RideListenetr implements Listener {
    @EventHandler(ignoreCancelled = false)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) return;
        Entity damanger = event.getDamager();
        if (damanger instanceof Player){
            Entity rer = event.getEntity();
            if (rer.getVehicle() == damanger){
                Location loc = damanger.getLocation();
                double yaw = (loc.getYaw() % 360) * Math.PI / 180;
                double pitch = loc.getPitch() * Math.PI / 180;
                double px = -Math.sin(yaw) * Math.cos(pitch);
                double py = -Math.sin(pitch);
                double pz = Math.cos(yaw) * Math.cos(pitch);
                float sp = 3;
                Vector v = damanger.getVelocity();
                v.setX(px * sp);
                v.setY(py * sp * 0.6);
                v.setZ(pz * sp);
                loc.setY(loc.getY() + 1);
                if (!rer.isEmpty()) rer.eject();
//                rer.teleport(loc);
                EntityTpUtils.forgeStopRide(rer);
                rer.setVelocity(v);
                loc.getWorld().playSound(loc,"minecraft:entity.player.attack.knockback",SoundCategory.AMBIENT,1f,0.5f);
            }
        }
    }
}
