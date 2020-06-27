package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftSnowball;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BeeEgg extends CustItem_CustModle implements Listener {
    private final String TAG = this.getClass().getSimpleName().toLowerCase();

    public BeeEgg() {
        super(Material.SNOWBALL,4,"§6精灵球",Arrays.asList(" ","§9里面装着:","§e蜜蜂"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        CraftSnowball snowball = (CraftSnowball) event.getEntity();
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        Location loc = snowball.getLocation();
        try{
            Bee bee = loc.getWorld().spawn(loc,Bee.class);
        }catch (IllegalArgumentException e){
            loc.getWorld().createExplosion(loc,1F,false,false);
//            e.printStackTrace();
        }
    }


}

