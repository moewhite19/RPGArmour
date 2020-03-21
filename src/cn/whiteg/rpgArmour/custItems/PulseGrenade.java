package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class PulseGrenade extends CustItem_CustModle implements Listener {
    private final float power = 4;
    private final float range = 4;
    private final String TAG = this.getClass().getSimpleName();

    public PulseGrenade() {
        super(Material.SNOWBALL,1,"§b脉冲手雷");
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        Location loc1 = snowball.getLocation();
        ProjectileSource damager = snowball.getShooter();
        if (damager instanceof LivingEntity){
            for (Entity entity : snowball.getNearbyEntities(range,range,range)) {
                final EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent((Entity) damager,entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0);
                Bukkit.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) return;
                Location loc2 = entity.getLocation();
                loc2.setY(loc2.getY() + 0.2);
                float yaw = VectorUtils.getLocYaw(loc1,loc2);
                float pitch = VectorUtils.getLocPitch(loc1,loc2);
                final double distance = loc1.distance(loc2); //距离
                final float mult = (float) (distance / range); //距离衰减
                loc2.setYaw(yaw);
                loc2.setPitch(pitch);
                Vector v = VectorUtils.viewVector(loc2);
                v.setY(v.getY() * 0.5);
                v.multiply(power - (3 * mult));
                entity.setVelocity(v);
//                    ((Entity) damager).sendMessage("雪球" + v);
            }
        }

    }
}

