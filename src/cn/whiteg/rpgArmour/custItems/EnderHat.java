package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.SoundCategory;
import net.minecraft.server.v1_16_R3.SoundEffects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
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

import java.util.Random;

public class EnderHat extends CustItem_CustModle implements Listener {
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
        CraftLivingEntity cp = (CraftLivingEntity) entity;
        EntityLiving entityliving = cp.getHandle();
        for (int i = 0; i < 16; ++i) {
            double d3 = d0 + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;
            Location l = entity.getLocation();
            double d4 = MathHelper.a(l.getY() + (double) (entityliving.getRandom().nextInt(16) - 8),0.0D,(double) (entityliving.getWorld().getHeight() - 1));
            double d5 = l.getZ() + (entityliving.getRandom().nextDouble() - 0.5D) * 16.0D;
            if (entityliving.isPassenger()){
                entityliving.stopRiding();
            }

            if (entityliving.a(d3,d4,d5,true)){
                entityliving.getWorld().a(d0,d1,d2,SoundEffects.ITEM_CHORUS_FRUIT_TELEPORT,SoundCategory.PLAYERS,1.0F,1.0F,false);
                //entityliving.a(SoundEffects.ITEM_CHORUS_FRUIT_TELEPORT,1.0F,1.0F);
                if (hat != null && ItemToolUtil.damage(hat,2)){
                    le.setHelmet(null);
                    return;
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getFinalDamage() >= 1){
            LivingEntity entity = (LivingEntity) event.getEntity();
            EntityEquipment le = entity.getEquipment();
            if (le == null || !is(le.getHelmet())) return;
            ItemStack hat = le.getHelmet();
            onTp(entity,hat);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
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
            return;
        }
    }
}
