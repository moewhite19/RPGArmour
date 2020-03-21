package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class QiubtBow extends CustItem_CustModle implements Listener {
    //箭TAG标签
    private final String TAG = getClass().getSimpleName();
    //范围
    private final double r = 1.2;

    public QiubtBow() {
        super(Material.BOW,3,"§d丘比特の弓");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShor(EntityShootBowEvent event) {
        if (event.getForce() < 0.3f){
            return;
        }
        if (!(event.getProjectile() instanceof final Arrow p) || !is(event.getBow())) return;
        if (!(p.getShooter() instanceof LivingEntity)) return;
        p.addScoreboardTag(TAG);
        new BukkitRunnable() {
            int i = 5000;

            @Override
            public void run() {
                if (p.isDead() || p.isInBlock() || i <= 0){
                    cancel();
                    return;
                }
                final Location loc = p.getLocation();
                loc.getWorld().spawnParticle(Particle.HEART,loc,1);
                i--;
            }
        }.runTaskTimer(RPGArmour.plugin,1,2);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof final Arrow arrow)) return;
        //命中事件
        if (checkArrow(arrow)){
            final Location loc = arrow.getLocation();
            loc.getWorld().spawnParticle(Particle.HEART,loc,25,r,r,r);
            final List<Entity> l = arrow.getNearbyEntities(r,r,r);
            if (!l.isEmpty()){
                for (Entity e : l) {
                    if (e instanceof LivingEntity){
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20,3));
                    }
                }
            }
            arrow.remove();
            loc.getWorld().playSound(loc,"minecraft:block.wet_grass.break",SoundCategory.AMBIENT,1f,1.5f);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow) || !checkArrow(event.getDamager())){
            return;
        }
        event.setDamage(0);
        event.setCancelled(true);
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
