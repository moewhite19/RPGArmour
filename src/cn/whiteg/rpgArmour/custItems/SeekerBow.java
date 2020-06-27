package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class SeekerBow extends CustItem_CustModle implements Listener {
    private float spawnChance = 0.05f;

    public SeekerBow() {
        super(Material.BOW,4,"§e维维诺斯锚击弓");
        ConfigurationSection c = Setting.getCustItemConfig(this);
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShor(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Arrow) || !is(event.getBow())) return;
        final Arrow p = (Arrow) event.getProjectile();
        if (!(p.getShooter() instanceof LivingEntity)) return;
        final LivingEntity shooter = (LivingEntity) p.getShooter();
        if (event.getForce() < 0.5F) return;
        final BukkitRunnable br = new BukkitRunnable() {
            final double r = 6;
            short i = 0;
            LivingEntity livingEntity = null;

            @Override
            public void run() {
                i++;
                if (i > 100 || p.isInBlock() || p.isDead()){
                    cancel();
                    return;
                }
                Location loc = p.getLocation();
                if (livingEntity == null){
                    if (shooter instanceof HumanEntity){
                        for (Entity entity : p.getNearbyEntities(r,r,r)) {
                            if (entity instanceof Mob){
                                final LivingEntity le = (LivingEntity) entity;
                                if (entity.getLocation().distance(loc) < r){
                                    if (le.equals(shooter)) continue;
                                    livingEntity = le;
                                    break;
                                }
                            }
                        }
                    } else {
                        for (Entity entity : p.getNearbyEntities(r,r,r)) {
                            if (entity instanceof HumanEntity){
                                final LivingEntity le = (LivingEntity) entity;
                                if (entity.getLocation().distance(loc) < r){
                                    if (le.equals(shooter)) continue;
                                    livingEntity = le;
                                    break;
                                }
                            }
                        }
                    }

                }
                if (livingEntity != null){
                    if (livingEntity.getLocation().distance(loc) > r || livingEntity.isDead()){
                        livingEntity = null;
                        return;
                    }
                    Location el = livingEntity.getLocation();
                    double ex = el.getX();
                    double ey = el.getY() + livingEntity.getBoundingBox().getHeight();
                    double ez = el.getZ();
                    double px = loc.getX();
                    double py = loc.getY();
                    double pz = loc.getZ();
                    double di = loc.distance(el) / 3;
                    Vector nv = new Vector(ex - px,ey - py,ez - pz);
                    Vector v = p.getVelocity();
                    double sp = Math.abs(v.getX()) + Math.abs(v.getY()) + Math.abs(v.getZ()) / 3;
                    nv.multiply(di * sp * 0.2);
                    v.add(nv);
                    p.setVelocity(v);

                }

            }
        };
        br.runTaskTimer(RPGArmour.plugin,4,1);
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        Entity entity = event.getEntity();
        Random random = RandomUtil.getRandom();
        if (EntityUtils.isSpawner(entity)) return;
        if (random.nextDouble() < spawnChance){
            EntityEquipment ej = ((Skeleton) entity).getEquipment();
            if (ej != null){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.1F);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(0.02F);
            }
        }

    }
}
