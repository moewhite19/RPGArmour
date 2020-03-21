package cn.whiteg.rpgArmour.custEntitys;

import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustEntityName;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimeWin extends CustEntityName implements Listener, CommandExecutor, TabCompleter {
    private final Map<String, Float> spawnChance = new HashMap<>();
    private float def_spawnChance = 0F;
    private int size = 10;

    public SlimeWin() {
        super("§a史莱姆王",Slime.class);
        ConfigurationSection config = Setting.getCustEntitySetting(getClass().getSimpleName());
        if (config != null){
            ConfigurationSection s = config.getConfigurationSection("spawnChance");
            if (s != null){
                for (String st : s.getKeys(false)) {
                    spawnChance.put(st,(float) s.getDouble(st,def_spawnChance));
                }
                Float f = spawnChance.remove("_def_");
                if (f != null) def_spawnChance = f;
            }
            size = config.getInt("size",size);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();
        Entity damager = event.getDamager();
        if (e == damager) return;
        if (damager instanceof Projectile && is(e)){
            event.setCancelled(true);
            damager.remove();
            final Slime slime = (Slime) event.getEntity();
            slime.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20,2));
            slime.getWorld().playSound(slime.getLocation(),"minecraft:block.wet_grass.break",SoundCategory.AMBIENT,1f,0.5f);
        } else if (is(damager)){
            final Slime slime = (Slime) event.getDamager();
            slime.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,40,2));
            if (e.getVehicle() == damager) return;
            damager.addPassenger(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        final float sc = spawnChance.getOrDefault(event.getEntity().getWorld().getName(),def_spawnChance);
        if (sc == 0) return;
        if (!(event.getEntity() instanceof Slime slim)) return;
        if (RandomUtil.getRandom().nextDouble() < sc){
            int amn = 0;
            for (Entity e : slim.getNearbyEntities(8,8D,8D)) {
                if (is(e)){
                    return;
                }
                if (e instanceof Slime){
                    amn++;
                    if (amn >= 2) return;
                }
            }
            final Location loc = event.getLocation();
            for (int i = 0; i < 9; i++) {
                loc.setY(loc.getY() + 1);
//                if (loc.getBlock().getType().isSolid()) return;
                if (loc.getBlock().getType() != Material.AIR) return;
            }
            this.init(slim);
            slim.setSize(10);
        }
    }

    @Override
    public Entity summon(Location location) {
        final Entity e = super.summon(location);
        if (e instanceof Slime){
            ((Slime) e).setSize(size);
        }
        return e;
    }

    @Override
    public boolean is(Entity entity) {
        return entity instanceof Slime && super.is(entity);
    }

    @Override
    public boolean init(final Entity entity) {
        if (entity instanceof Slime slim && super.init(entity)){
            slim.getEquipment().setHelmet(new ItemStack(Material.SLIME_BLOCK));
            AttributeInstance ab = slim.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
            ab.setBaseValue(1);
            ab = slim.getAttribute(Attribute.GENERIC_ARMOR);
            ab.setBaseValue(10);
        }
        return false;
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



