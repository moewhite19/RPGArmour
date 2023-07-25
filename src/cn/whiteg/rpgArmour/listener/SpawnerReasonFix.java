package cn.whiteg.rpgArmour.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.inventory.EntityEquipment;

public class SpawnerReasonFix implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Mob){
//            RPGArmour.logger.info("刷怪笼生成" + e.getType());
            EntityEquipment ee = ((Mob) e).getEquipment();
            ee.setHelmetDropChance(0F);
            ee.setItemInMainHandDropChance(0f);
            ee.setItemInOffHandDropChance(0F);
        }
    }
}
