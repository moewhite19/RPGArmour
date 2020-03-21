package cn.whiteg.rpgArmour.event;

import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerDeathPreprocessEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    static {
        RPGArmour.plugin.regListener(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onPlayerDamage(EntityDamageEvent event) {
                if(event.getEntity() instanceof Player player){
                    if (event.getFinalDamage() >= player.getHealth()){
                        PlayerInventory inv = player.getInventory();
                        PlayerDeathPreprocessEvent e = new PlayerDeathPreprocessEvent(player,event.getCause());
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

        });
    }

    private ItemStack item;
    private boolean cancelled;
    private EntityDamageEvent.DamageCause damageCause;

    public PlayerDeathPreprocessEvent(Player who ,EntityDamageEvent.DamageCause damageCause) {
        super(who);
        this.damageCause = damageCause;
        cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
