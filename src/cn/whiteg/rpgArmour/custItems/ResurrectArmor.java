package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.mmocore.util.CoolDownUtil;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_Lore;
import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import cn.whiteg.rpgArmour.listener.UndyingListener;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class ResurrectArmor extends CustItem_Lore implements Listener {

    private short cooldown = 120;

    public ResurrectArmor() {
        super(Material.CHAINMAIL_CHESTPLATE,"§b妖精庇护",Arrays.asList("","§b唯一被动:","§3森の妖精的庇护"),2);
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            cooldown = (short) c.getInt("cooldown",cooldown);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 1 || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getEntity() instanceof LivingEntity entity){
            if (is(entity.getEquipment().getChestplate())){
                Entity damager = event.getDamager();
                if (damager instanceof LivingEntity){
                    Location loc1 = entity.getLocation();
                    Location loc2 = damager.getLocation();
                    final double distance = loc1.distance(loc2); //距离
                    if (distance > 4) return;
                    final EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity,damager,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0);
                    Bukkit.getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) return;
                    loc2.setY(loc2.getY() + 0.2);
                    float yaw = VectorUtils.getLocYaw(loc1,loc2);
                    float pitch = VectorUtils.getLocPitch(loc1,loc2);
                    final float mult = (float) (distance / 4D); //距离衰减
                    loc2.setYaw(yaw);
                    loc2.setPitch(pitch);
                    Vector v = VectorUtils.viewVector(loc2);
                    double damage = event.getFinalDamage();
                    double pow = damage - (2 * mult);
                    if (pow <= 0) return;
                    v.setY(v.getY() * 0.3);
                    v.multiply(pow);
                    damager.setVelocity(v);
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDie(PlayerDeathPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack che = inv.getChestplate();
        if (che != null && che.getType() == getMaterial()){
            if (!CoolDownUtil.hasCd(player.getName(),getLoreStr())) return;
            if (ItemToolUtil.hasLore(che,getLoreStr())){
                CoolDownUtil.setCd(player.getName(),getLoreStr(),cooldown * 1000);
                //undyingListener.useOffHand(inv,new ItemStack(Material.TOTEM_OF_UNDYING));
                UndyingListener.EntityResurrect(player,che);
                event.setCancelled(true);
                Location loc1 = player.getLocation();
                for (Entity entity : player.getNearbyEntities(4D,4D,4D)) {
                    final EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(player,entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0);
                    Bukkit.getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) return;
                    Location loc2 = entity.getLocation();
                    loc2.setY(loc2.getY() + 0.2);
                    float yaw = VectorUtils.getLocYaw(loc1,loc2);
                    float pitch = VectorUtils.getLocPitch(loc1,loc2);
                    final double distance = loc1.distance(loc2); //距离
                    final float mult = (float) (distance / 4D); //距离衰减
                    loc2.setYaw(yaw);
                    loc2.setPitch(pitch);
                    Vector v = VectorUtils.viewVector(loc2);
                    v.setY(v.getY() * 0.5);
                    v.multiply(5 - (3 * mult));
                    entity.setVelocity(v);
                }

            }
        }
    }
}


