package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Random;

public class EnderHat extends CustItem_CustModle implements Listener {
    public static Field entityRandom;

    static {
        for (Field field : Entity.class.getDeclaredFields()) {
            if (field.getType().equals(RandomSource.class) && !Modifier.isStatic(field.getModifiers())){
                field.setAccessible(true);
                entityRandom = field;
                break;
            }
        }
        Objects.requireNonNull(entityRandom);
    }

    private float spawnChance = 0.05F;
    private float itemDropChance = 0.15F;

    public EnderHat() {
        super(Material.SHEARS,7,"§9末影使者");
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
        }
    }

    static public void onTp(LivingEntity entity,ItemStack hat) {
        EntityEquipment le = entity.getEquipment();
        //PlayerInventory pi = ((Player) event.getEntity()).getInventory();
        //if (((Player) event.getEntity()).hasCooldown(mat)) return;
        Location loc = entity.getLocation();
        double d0 = loc.getX();
        double d1 = loc.getY();
        double d2 = loc.getZ();

        net.minecraft.world.entity.LivingEntity entityliving = (net.minecraft.world.entity.LivingEntity) NMSUtils.getNmsEntity(entity);


        for (int i = 0; i < 16; ++i) {
            var random = getEntityRandom(entityliving);
            double d3 = d0 + (random.nextDouble() - 0.5D) * 16.0D;
            Location l = entity.getLocation();
            double d4 = Mth.length(l.getY() + (double) (random.nextInt(16) - 8),0.0D,entity.getWorld().getMaxHeight() - 1);
            double d5 = l.getZ() + (random.nextDouble() - 0.5D) * 16.0D;
            if (entity.getVehicle() != null){
//                entity.getVehicle().removePassenger(entityliving.getBukkitEntity());
                entityliving.stopRiding(true);
            }

            if (entityliving.randomTeleport(d3,d4,d5,true)){
                //item.chorus_fruit.teleport
//                entityliving.level().addParticle(d0,
//                        d1,
//                        d2,
//                        SoundEffects.cV,
//                        SoundCategory.h,
//                        1.0F,
//                        1.0F,
//                        false);
//                entityliving.a(SoundEffects.cV,1.0F,1.0F);
                if (hat != null && ItemToolUtil.damage(hat,2)){
                    le.setHelmet(null);
                    return;
                }
                break;
            }
        }
    }

    public static RandomSource getEntityRandom(Entity entity) {
        try{
            return (RandomSource) entityRandom.get(entity);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && event.getFinalDamage() >= 1){
            EntityEquipment le = entity.getEquipment();
            if (le == null || !is(le.getHelmet())) return;
            ItemStack hat = le.getHelmet();
            onTp(entity,hat);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (EntityUtils.isSpawner(entity)) return;
        EntityEquipment ej = entity.getEquipment();
        Random random = RandomUtil.getRandom();
        if (ej == null) return;
        if (entity instanceof Skeleton){
            if (random.nextDouble() < 0.10){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.15F);
                ItemToolUtil.copyEnchat(ej.getHelmet(),item);
                ej.setHelmet(item);
                ej.setHelmetDropChance(0.15F);
            }
            return;
        }

        if (entity instanceof Zombie && entity.getType() != EntityType.ZOMBIFIED_PIGLIN){
            if (random.nextDouble() < spawnChance){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.2F);
                ej.setHelmet(item);
                ej.setHelmetDropChance(itemDropChance);
            }
            return;
        }

        if (entity.getType() == EntityType.PILLAGER){
            if (random.nextDouble() < 0.15){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.2F);
                ej.setHelmet(item);
                ej.setHelmetDropChance(0.15F);
            }
        }
    }
}
