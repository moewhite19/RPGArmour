package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import net.minecraft.server.v1_16_R2.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ddBow extends CustItem_CustModle implements Listener {
    private final String TAG = getClass().getSimpleName();

    public ddBow() {
        super(Material.BOW,6,"§e穿云弓");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShor(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Arrow) || !is(event.getBow())) return;
        final Arrow p = (Arrow) event.getProjectile();
        if (!(p.getShooter() instanceof LivingEntity)) return;
//        final LivingEntity shooter = (LivingEntity) p.getShooter();
        p.addScoreboardTag(TAG);
        p.setTicksLived(3);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getEntity();
        //命中事件
        Entity entity = event.getHitEntity();
        if (checkArrow(arrow) && entity instanceof LivingEntity){
            EntityLiving lv = ((CraftLivingEntity) entity).getHandle();

        }
    }


    public boolean checkArrow(Entity entity) {
        return entity.getScoreboardTags().contains(TAG);
    }


//    @EventHandler(priority = EventPriority.HIGH)
//    public void onEntityAtt(EntityDamageByEntityEvent event) {
//        if (ents[0] == null) return;
//        if (!event.isCancelled()){
//            Entity who = ents[0];
//            Entity hit = ents[1];
//            Location el = hit.getLocation();
//            Location sl = who.getLocation();
//            who.teleport(el);
//            hit.teleport(sl);
//        }
//        ents[0] = null;
//        ents[1] = null;
//    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onEntitySpwawn(EntitySpawnEvent event) {
//        if (!(event.getEntity() instanceof Skeleton)) return;
//        Random random = RandomUtil.getRandom();
//        Entity entity = event.getEntity();
//        if (entity.getType() == EntityType.WITHER_SKELETON){
//            if (random.nextDouble() < 0.15){
//                EntityEquipment ej = ((Skeleton) entity).getEquipment();
//                if (ej != null){
//                    ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.1F);
//                    ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
//                    ej.setItemInMainHand(item);
//                    ej.setItemInMainHandDropChance(0F);
//                }
//            }
//        }
//
//    }
}
