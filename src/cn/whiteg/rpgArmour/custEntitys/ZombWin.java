package cn.whiteg.rpgArmour.custEntitys;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.api.CustEntityName;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.custItems.Muramasa;
import cn.whiteg.rpgArmour.custItems.SamuraiSword;
import cn.whiteg.rpgArmour.custItems.XiaoChou;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import net.minecraft.server.v1_15_R1.EntityGiantZombie;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftGiant;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZombWin extends CustEntityName implements Listener, CommandExecutor, TabCompleter, CustEntityChunkEvent {
    private Map<String, Float> spawnChance = new HashMap<>();
    private int dropexp = 600;
    private float def_spawnChance = 0.01f;
    private float itemDropChance = 0.8f;
//    private float spawnChance = 0.03f;

    public ZombWin() {
        //super(Material.SHEARS,10,"§b僵尸王");
        super("§b僵尸王",Giant.class);
//        RPGArmour.plugin.regListener(this);
        ConfigurationSection config = Setting.getCustEntitySetting(this);
        if (config != null){
            ConfigurationSection s = config.getConfigurationSection("spawnChance");
            if (s != null){
                def_spawnChance = (float) s.getDouble("_def_",def_spawnChance);
                for (String st : s.getKeys(false)) {
                    spawnChance.put(st,(float) s.getDouble(st,def_spawnChance));
                }
                spawnChance.remove("_def_");
            }
//            spawnChance = (float) config.getDouble("spawnChance");
            itemDropChance = (float) config.getDouble("itemDropChance",itemDropChance);
            dropexp = config.getInt("dropexp",dropexp);
        }
//        RPGArmour.plugin.commandManager.addComd(this.getClass() , "win");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        final float sc = spawnChance.getOrDefault(event.getEntity().getWorld().getName(),def_spawnChance);
        if (sc == 0) return;
        if (!(event.getEntity() instanceof Zombie)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.fromMobSpawner() || entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            return;
        if (entity.getType() == EntityType.ZOMBIE){
            if (RandomUtil.getRandom().nextDouble() < sc){
                for (Entity e : entity.getLocation().getChunk().getEntities()) {
                    if (is(e)){
                        return;
                    }
                }
                Location loc = entity.getLocation();
                for (int i = 0; i < 20; i++) {
                    loc.setY(loc.getY() + 1);
                    if (loc.getBlock().getType() != Material.AIR) return;
                }
                this.init(entity);
            }
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
        if (!(entity instanceof LivingEntity) || !super.init(entity)){
            throw new IllegalArgumentException("目标不是活动实体");
        }
        final LivingEntity livent = (LivingEntity) entity;
        ItemStack hat = getHat().createItem();
        EntityEquipment ej = ((LivingEntity) entity).getEquipment();
        ej.setHelmet(hat);
        ej.setHelmetDropChance(0);
        AttributeInstance ab = livent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        ab.setBaseValue(600);
        ab = livent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        ab.setBaseValue(0.3);
        ab = livent.getAttribute(Attribute.GENERIC_ARMOR);
        ab.setBaseValue(10);
        ab = livent.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        ab.setBaseValue(1);
        livent.setHealth(600);
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
        if (is(event.getEntity())){

        }
    }
//
//    boolean check(Location l) {
//        return l.getBlock().getType() != Material.AIR;
//    }

    void ontp(Entity livent) {
        Entity[] es = livent.getLocation().getChunk().getEntities();
        for (Entity e : es) {
            if (e instanceof Player){
                livent.teleport(e);
            }
        }
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
    public void load(Entity entity) {
        if (entity instanceof Giant){
            CraftGiant giant = (CraftGiant) entity;
            EntityGiantZombie nmsE = giant.getHandle();
            giant.setAI(true);
        }
    }

    @Override
    public void unload(Entity entity) {

    }
}



