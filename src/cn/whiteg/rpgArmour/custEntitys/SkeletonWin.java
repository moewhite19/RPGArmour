package cn.whiteg.rpgArmour.custEntitys;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustEntityName;
import cn.whiteg.rpgArmour.custItems.EnderHat;
import cn.whiteg.rpgArmour.custItems.SeekerBow;
import cn.whiteg.rpgArmour.custItems.WingHat;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkeletonWin extends CustEntityName implements Listener, CommandExecutor, TabCompleter {
    private final Map<String, Float> spawnChance = new HashMap<>();
    private int dropexp = 600;
    private float itemDropChance = 0.25f;
    private float def_spawnChance = 0.02f;

    public SkeletonWin() {
        super("§f§l骷髅王",Skeleton.class);
        ConfigurationSection config = Setting.getCustEntitySetting(getClass().getSimpleName());
        if (config != null){
            ConfigurationSection s = config.getConfigurationSection("spawnChance");
            if (s != null){
                for (String st : s.getKeys(false)) {
                    spawnChance.put(st,(float) s.getDouble(st,def_spawnChance));
                }

                //默认
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
        if (!(event.getEntity() instanceof Skeleton)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (EntityUtils.isSpawner(entity)) return;

        EntityEquipment ej = entity.getEquipment();
        if (ej == null) return;
        if (entity instanceof Skeleton){
            if (RandomUtil.getRandom().nextDouble() < sc){
                int amn = 0;
                for (Entity e : entity.getNearbyEntities(8,8,8)) {
                    if (is(e)){
                        return;
                    }
                    if (e instanceof Skeleton){
                        amn++;
                        if (amn >= 4) return;
                    }
                }
                for (BlockState bs : entity.getLocation().getChunk().getTileEntities()) {
                    if (bs instanceof CreatureSpawner bs1){
                        if (bs1.getSpawnedType() == EntityType.SKELETON){
                            return;
                        }
                    }
                }
                init(entity);
            }
        }
    }
//
//    @EventHandler
//    public void onClick(PlayerInteractEvent event){
//        if(event.getAction()== Action.RIGHT_CLICK_BLOCK){
//            BlockState st = event.getClickedBlock().getState();
//            CraftBlockState cst = (CraftBlockState) st;
//            IBlockData bs = cst.getHandle();
//
//            event.getPlayer().sendMessage(cst.getClass().toString() + "\n " + cst.toString() + " \n" + cst.getBlockData() + " \n " + cst.getClass().getMethods());
//
//        }
//    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!is(event.getEntity())) return;
        if (RandomUtil.getRandom().nextDouble() < 0.3){
            EnderHat.onTp((LivingEntity) event.getEntity(),null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!is(event.getEntity())) return;
        final Location loc = event.getEntity().getLocation();
        float y = VectorUtils.getLocYaw(loc,event.getDamager().getLocation());
        float i = VectorUtils.getIncludedAngle(y,loc.getYaw());
        if (Math.abs(i) < 45){
            if (event.getDamager() instanceof Projectile){
                event.setCancelled(true);
            } else {
                event.setDamage(event.getDamage() / 2);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDie(EntityDeathEvent event) {
        if (!is(event.getEntity())) return;
        final Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        killer.sendMessage("击杀" + getName());
        event.setDroppedExp(dropexp);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean init(Entity entity) {
        if (!(entity instanceof final LivingEntity livent)) return false;
        super.init(entity);
        ItemStack hat = WingHat.get().createItem();
        EntityEquipment ej = livent.getEquipment();
//        ItemMeta im = hat.getItemMeta();
//        AttributeModifier ah = new AttributeModifier(UUID.randomUUID(),"hed",600,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD);
//        im.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,ah);
//        ah = new AttributeModifier(UUID.randomUUID(),"hed",20,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD);
//        im.addAttributeModifier(Attribute.GENERIC_ARMOR,ah);
//        hat.setItemMeta(im);
        AttributeInstance ab = livent.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        ab.setBaseValue(500);
        ab = livent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        ab.setBaseValue(0.3);
        ab = livent.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        ab.setBaseValue(128);
        livent.setHealth(150);
        ej.setHelmet(hat);
        ej.setHelmetDropChance(0);
        ItemStack item = RPGArmour.plugin.getItemManager().createItem(SeekerBow.class.getName());
        ej.setItemInMainHand(item);
        ej.setItemInMainHandDropChance(itemDropChance);
        item = new ItemStack(Material.SHIELD);
        ej.setItemInOffHand(item);
        return true;
    }


    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
        return null;
    }
}



