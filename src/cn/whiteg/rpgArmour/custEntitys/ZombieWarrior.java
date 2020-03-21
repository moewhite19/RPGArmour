package cn.whiteg.rpgArmour.custEntitys;

import cn.whiteg.moetp.utils.EntityTpUtils;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustEntityName;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.custItems.Muramasa;
import cn.whiteg.rpgArmour.custItems.SamuraiSword;
import cn.whiteg.rpgArmour.custItems.XiaoChou;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.*;

public class ZombieWarrior extends CustEntityName implements Listener, CommandExecutor, TabCompleter {
    private final Map<String, Float> spawnChance = new HashMap<>();
    private int dropexp = 600;
    private float def_spawnChance = 0.03f;
    private float itemDropChance = 0.8f;
    private final WeakHashMap<UUID, Location> locMap = new WeakHashMap<>();

    public ZombieWarrior() {
        super("§b僵尸武士",Zombie.class);
        ConfigurationSection config = Setting.getCustEntitySetting(this);
        if (config != null){
            ConfigurationSection s = config.getConfigurationSection("spawnChance");
            if (s != null){
                for (String st : s.getKeys(false)) {
                    spawnChance.put(st,(float) s.getDouble(st,def_spawnChance));
                }
                //默认配置
                Float f = spawnChance.remove("_def_");
                if (f != null) def_spawnChance = f;
            }
            itemDropChance = (float) config.getDouble("itemDropChance",itemDropChance);
            dropexp = config.getInt("dropexp",dropexp);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        final float sc = spawnChance.getOrDefault(event.getEntity().getWorld().getName(),def_spawnChance);
        if (sc == 0) return;
        if (!(event.getEntity() instanceof Zombie)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (EntityUtils.isSpawner(entity)) return;
        if (entity.getType() == EntityType.ZOMBIE){
            if (RandomUtil.getRandom().nextDouble() < sc){
                int amn = 0;
                for (Entity e : entity.getNearbyEntities(8,8,8)) {
                    if (is(e)){
                        return;
                    }
                    if (e instanceof Zombie){
                        amn++;
                        if (amn >= 4) return;
                    }
                }
                for (BlockState bs : entity.getLocation().getChunk().getTileEntities()) {
                    if (bs instanceof CreatureSpawner bs1){
                        if (bs1.getSpawnedType() == EntityType.ZOMBIE){
                            return;
                        }
                    }
                }
                this.init(entity);
            }
        } else if (entity.getType() == EntityType.DROWNED && is(entity)){
//            RPGArmour.logger.info("僵尸武士变溺尸");
            init(entity);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDie(EntityDeathEvent event) {
        if (!is(event.getEntity())) return;
        final Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        event.setDroppedExp(dropexp);
    }


    @Override
    public boolean init(final Entity entity) {
//        entity.setCustomNameVisible(true);
        //if (entity.getClass() != entityClass) return false;
        if (!(entity instanceof final LivingEntity livent) || !super.init(entity)){
            throw new IllegalArgumentException("目标不是活动实体");
        }
        ItemStack hat = getHat().createItem();
        EntityEquipment ej = ((LivingEntity) entity).getEquipment();
        ej.setHelmet(hat);
        ej.setHelmetDropChance(0);
        AttributeInstance ab = livent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        ab.setBaseValue(80);
        ab = livent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        ab.setBaseValue(0.3);
        ab = livent.getAttribute(Attribute.GENERIC_ARMOR);
        ab.setBaseValue(10);
        ab = livent.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        ab.setBaseValue(0.5D);
        livent.setHealth(80);
        if (RandomUtil.getRandom().nextDouble() < 0.25){
            final ItemStack item = RPGArmour.plugin.getItemManager().createItem(Muramasa.class.getName());
            ej.setItemInMainHand(item);
            ej.setItemInMainHandDropChance(itemDropChance);
        } else {
            final ItemStack item = RPGArmour.plugin.getItemManager().createItem(SamuraiSword.class.getName());
            ej.setItemInMainHand(null);
            ej.setItemInOffHand(item);
            ej.setItemInOffHandDropChance(itemDropChance);
        }
        livent.addPotionEffect(new PotionEffect(PotionEffectType.POISON,300,3));
        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (is(entity)){
            if (RandomUtil.getRandom().nextDouble() > 0.5) return;
            final Location orgl = locMap.get(event.getEntity().getUniqueId());
            final Location newl = event.getEntity().getLocation();
            if (orgl != null && orgl.getWorld() == newl.getWorld() && orgl.distanceSquared(newl) < 0.5){
                Entity damager = event.getDamager();
                if (damager instanceof Projectile){
                    ProjectileSource shooter = ((Projectile) damager).getShooter();
                    if (shooter instanceof Entity){
                        damager = (Entity) shooter;
                    } else {
                        return;
                    }
                }
                if (damager instanceof LivingEntity && entity instanceof Mob){
                    EntityUtils.setGoalTarget(((Mob) entity),((LivingEntity) damager));
                }
                teleportToTheTargetSBack(entity,damager);
            } else {
                locMap.put(event.getEntity().getUniqueId(),newl);
            }
        }
    }
//
//    boolean check(Location l) {
//        return l.getBlock().getType() != Material.AIR;
//    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            return;
        Entity entity = event.getEntity();
        if (is(entity)){
            LivingEntity damager = ((Zombie) entity).getTarget();
            if (damager == null) return;
            if (RandomUtil.getRandom().nextDouble() > 0.5) return;
            final Location orgl = locMap.get(event.getEntity().getUniqueId());
            final Location newl = event.getEntity().getLocation();
            if (orgl != null && orgl.getWorld() == newl.getWorld() && orgl.distanceSquared(newl) < 0.5){
                teleportToTheTargetSBack(entity,damager);
            } else {
                locMap.put(event.getEntity().getUniqueId(),newl);
            }
        }
    }


    //传送到目标后背
    void teleportToTheTargetSBack(Entity entity,Entity target) {
        Location loc = target.getLocation();
        Vector v = VectorUtils.viewVector(loc);
        v.multiply(-0.5F);
        loc.add(v);
        EntityTpUtils.forgeStopRide(entity);
        EntityTpUtils.enderTeleportTo(entity,loc);
    }

    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){

        }
        return true;
    }

    public CustItem getHat() {
        return XiaoChou.get();
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
        return null;
    }

    @Override
    public boolean is(Entity entity) {
        return entity instanceof Zombie && super.is(entity);
    }
}



