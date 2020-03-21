package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDammangeListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.PLAYER) return;
        Player player = (Player) entity;
        if (event.getFinalDamage() >= player.getHealth()){
            PlayerInventory inv = player.getInventory();
            PlayerDeathPreprocessEvent e = new PlayerDeathPreprocessEvent(player);
            ItemStack item = inv.getItemInMainHand();
            if (item.getType() == Material.TOTEM_OF_UNDYING){
                //player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,5,1));
                //event.setCancelled(true);
                e.setCancelled(true);
                e.call();
                return;
            }
            item = inv.getItemInOffHand();
            if (item.getType() == Material.TOTEM_OF_UNDYING){
                //player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,5,1));
                //event.setCancelled(true);
                e.setCancelled(true);
                e.call();
                return;
            }
            e.call();
            if (e.isCancelled()){
                event.setDamage(0);
            }
        }
    }
//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onDamage(EntityDamageEvent event) {
//        Entity entity = event.getEntity();
//        if (entity.getType() != EntityType.PLAYER) return;
//        Player player = (Player) entity;
//        if(event.getDamage() >= player.getHealth()&&player.getLocation().getY()<0){
//            Location init = WarpManager.getWarp("init");
//            event.setCancelled(true);
//        }
//
//    }
}
