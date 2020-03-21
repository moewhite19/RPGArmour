package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.moetp.utils.EntityTpUtils;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Random;

public class EnderBow extends CustItem_CustModle implements Listener {
    private final String endTag = "end_bow";
    private final Entity[] ents = new Entity[2];
    private boolean swapVehicle;
    private float itemDropChance = 0.05f;
    private float spawnChance = 0.075f;

    public EnderBow() {
        super(Material.BOW,1,"§9末影弓");
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
            swapVehicle = c.getBoolean("swapVehicle",true);
        }
    }

    public void swapLoc(Entity e,Entity f) {
        if (!e.isValid() || !f.isValid() || e == f) return;
        Location el = e.getLocation();
        Entity ev = e.getVehicle();

        Location fl = f.getLocation();
        Entity fv = f.getVehicle();
        tpLoc(e,fv,fl);
        tpLoc(f,ev,el);
    }

    public void tpLoc(Entity e,Entity v,Location loc) {
        EntityTpUtils.forgeStopRide(e);
        if (swapVehicle && (v == e || v != null && v.isValid() && v.addPassenger(e))) return;
        EntityTpUtils.enderTeleportTo(e,loc);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShor(final EntityShootBowEvent event) {
        if (!is(event.getBow())) return;
        final Entity p = event.getProjectile();
        p.addScoreboardTag(endTag);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(final ProjectileHitEvent event) {
        final ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof LivingEntity who){
            if (!event.getEntity().getScoreboardTags().contains(endTag)) return;
            if (event.getHitEntity() != null && (event.getHitEntity() instanceof Mob || event.getHitEntity() instanceof HumanEntity) || event.getHitEntity() instanceof Vehicle){
                ents[0] = who;
                ents[1] = event.getHitEntity();
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityAtt(final EntityDamageByEntityEvent event) {
        if (ents[0] == null) return;
        if (!event.isCancelled() && ents[1].getUniqueId().equals(event.getEntity().getUniqueId())){
            Entity who = ents[0];
            Entity hit = ents[1];
            swapLoc(who,hit);
        }

        ents[0] = null;
        ents[1] = null;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(final EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof Skeleton)) return;
        final Entity entity = event.getEntity();
        if (EntityUtils.isSpawner(entity)) return;
        final Random random = RandomUtil.getRandom();
        if (random.nextDouble() < spawnChance){
            final EntityEquipment ej = ((Skeleton) entity).getEquipment();
            if (ej != null){
                final ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.15F);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(itemDropChance);
            }
        }

    }
}
