package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.PlayerAttackMissEvent;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Muramasa extends CustItem_CustModle implements Listener {
    private final String sound_att = "minecraft:entity.player.attack.sweep";


    public Muramasa() {
        super(Material.SHEARS,25,"§d妖刀村正");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAnmi(PlayerAttackMissEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory pi = player.getInventory();
        if (is(pi.getItemInMainHand())){
            player.setCooldown(getMaterial(),10);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 0.5 || !(event.getDamager() instanceof LivingEntity damager && (event.getEntity() instanceof Mob || event.getEntity() instanceof HumanEntity) && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK))
            return;
        if (damager instanceof Player && ((Player) damager).hasCooldown(getMaterial())) return;
        EntityEquipment pi = damager.getEquipment();
        ItemStack item = pi.getItemInMainHand();
        if (!is(item)) return;
        final double damage = event.getDamage() + 14;
        event.setDamage(damage);
        double ph = damager.getHealth();
        AttributeInstance att = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        final double max = att.getBaseValue() / 2;
        if (ph < max){
            ph += (event.getFinalDamage() / 2);
            if (ph > max) ph = max;
            damager.setHealth(ph);
        }
        Location loc = event.getEntity().getLocation();
        if (damager instanceof Player) ((Player) damager).setCooldown(getMaterial(),10);
        if (ItemToolUtil.damage(item,1)){
            loc.getWorld().playSound(loc,"minecraft:entity.player.attack.crit",1,1);
            pi.setItemInMainHand(null);
            return;
        }
        loc.getWorld().playSound(loc,sound_att,1,1);
    }

//    private boolean hasCd(Entity player) {
//        if (attMap.containsKey(player.getName())){
//            return attMap.get(player.getName()) < System.currentTimeMillis();
//        }
//        return true;
//    }
//
//    @SuppressWarnings("all")
//    private void setCd(Entity player) {
//        attMap.put(new String(player.getName()),System.currentTimeMillis() + 500);
//    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onUse(PlayerInteractEvent event) {
//        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_AIR) return;
//        ItemStack item = event.getItem();
//        if (is(item)){
//            ItemMeta im = item.getItemMeta();
//            int i = im.getCustomModelData();
//            if (i == id){
//                im.setCustomModelData(id2);
//            } else {
//                im.setCustomModelData(id);
//            }
//        }
//    }
//
//    @SuppressWarnings("all")
//    @Override
//    public boolean is(ItemStack item) {
//        if (item == null || item.getType() != mat || item.hasItemMeta()) return false;
//        ItemMeta im = item.getItemMeta();
//        if (im.hasCustomModelData()){
//            int i = im.getCustomModelData();
//            if (i == id || i == id2) return true;
//        }
//        return false;
//    }
}
