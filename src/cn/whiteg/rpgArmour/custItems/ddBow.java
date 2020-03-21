package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.Material;
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
        if (!(event.getProjectile() instanceof final Arrow p) || !is(event.getBow())) return;
        if (!(p.getShooter() instanceof LivingEntity)) return;
//        final LivingEntity shooter = (LivingEntity) p.getShooter();
        p.addScoreboardTag(TAG);
        p.setTicksLived(3);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        //命中事件
        Entity entity = event.getHitEntity();
        if (checkArrow(arrow) && entity instanceof LivingEntity){
            var lv = EntityUtils.getNmsEntity(entity);

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
