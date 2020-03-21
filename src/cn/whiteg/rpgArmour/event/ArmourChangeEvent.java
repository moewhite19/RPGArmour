package cn.whiteg.rpgArmour.event;

import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArmourChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    static {
        RPGArmour.plugin.regListener(new Listener() {
            @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
            public void onInventoryClickEvent(InventoryClickEvent even) {
                Inventory localInventory1 = even.getView().getTopInventory();
                Inventory inv;
                HumanEntity player = even.getWhoClicked();
                if (even.getRawSlot() < 0){
                    inv = null;
                } else {
                    inv = even.getRawSlot() < localInventory1.getSize() ? localInventory1 : even.getView().getBottomInventory();
                }
                if (inv != null && inv.getType() == InventoryType.PLAYER && player.hasPermission("mmo.hat")){
                    PlayerInventory PlayerInv;
                    if (even.getSlot() == 39){
                        PlayerInv = (PlayerInventory) inv;
                        ItemStack item = even.getCursor();
                        ItemStack hd = PlayerInv.getHelmet();
                        if (!isAir(item) && isAir(hd)){
                            if (even.getClick() != ClickType.LEFT) return;
                            even.setCancelled(true);
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,item,ArmourChangeEvent.ArmourType.HELMET,true);
                            event.call();
                            item = event.getItem();
                            if (event.isCancelled()){
                                return;
                            }
                            even.setCurrentItem(item);
                            player.setItemOnCursor(null);
                        } else if (!isAir(hd)){
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,hd,ArmourChangeEvent.ArmourType.HELMET,false);
                            event.call();
                        }
                    } else if (even.getSlot() == 38){
                        PlayerInv = (PlayerInventory) inv;
                        ItemStack item = even.getCursor();
                        ItemStack hd = PlayerInv.getChestplate();
                        if (!isAir(item) && isAir(hd)){
                            if (even.getClick() != ClickType.LEFT) return;
                            even.setCancelled(true);
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,item,ArmourChangeEvent.ArmourType.CHESTPLATE,true);
                            event.call();
                            item = event.getItem();
                            if (event.isCancelled()){
                                return;
                            }
                            even.setCurrentItem(item);
                            player.setItemOnCursor(null);
                        } else if (isAir(item) && !isAir(hd)){
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,hd,ArmourChangeEvent.ArmourType.CHESTPLATE,false);
                            event.call();
                        }
                    } else if (even.getSlot() == 37){
                        PlayerInv = (PlayerInventory) inv;
                        ItemStack item = even.getCursor();
                        ItemStack hd = PlayerInv.getLeggings();
                        if (!isAir(item) && isAir(hd)){
                            if (even.getClick() != ClickType.LEFT) return;
                            even.setCancelled(true);
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,item,ArmourChangeEvent.ArmourType.LEGGINGS,true);
                            event.call();
                            item = event.getItem();
                            if (event.isCancelled()){
                                return;
                            }
                            even.setCurrentItem(item);
                            player.setItemOnCursor(null);
                        } else if (!isAir(hd)){
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,hd,ArmourChangeEvent.ArmourType.LEGGINGS,false);
                            event.call();
                        }
                    } else if (even.getSlot() == 36){
                        PlayerInv = (PlayerInventory) inv;
                        ItemStack item = even.getCursor();
                        ItemStack hd = PlayerInv.getBoots();
                        if (!isAir(item) && isAir(hd)){
                            if (even.getClick() != ClickType.LEFT) return;
                            even.setCancelled(true);
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,item,ArmourChangeEvent.ArmourType.BOOTS,true);
                            event.call();
                            if (event.isCancelled()){
                                return;
                            }
                            item = event.getItem();
                            even.setCurrentItem(item);
                            player.setItemOnCursor(null);
                        } else if (!isAir(hd)){
                            ArmourChangeEvent event = new ArmourChangeEvent((Player) player,hd,ArmourChangeEvent.ArmourType.BOOTS,false);
                            event.call();
                            if (event.isCancelled()) even.setCancelled(true);
                        }

//                PlayerInv = (PlayerInventory) inv;
//                if (PlayerInv.getBoots() != null || even.getCursor() == null) return;
//                even.setCancelled(true);
//                even.setCurrentItem(even.getCursor());
//                player.setItemOnCursor(null);
                    }
/*

            if (even.getSlot() == 38){
                PlayerInv = (PlayerInventory) inv;
                if (PlayerInv.getHelmet() != null || even.getCursor() == null) return;
                even.setCancelled(true);
                even.setCurrentItem(even.getCursor());
                even.setCursor(null);
            }

            if (even.getSlot() == 37){
                PlayerInv = (PlayerInventory) inv;
                if (PlayerInv.getHelmet() != null || even.getCursor() == null) return;
                even.setCancelled(true);
                even.setCurrentItem(even.getCursor());
                even.setCursor(null);
            }

            if (even.getSlot() == 36){
                PlayerInv = (PlayerInventory) inv;
                if (PlayerInv.getHelmet() != null || even.getCursor() == null) return;
                even.setCancelled(true);
                even.setCurrentItem(even.getCursor());
                even.setCursor(null);
            }
*/


                }

            }

            boolean isAir(ItemStack item) {
                return item == null || item.getType() == Material.AIR;
            }
        });
    }

    ArmourType type;
    boolean wear;
    boolean cancelled;
    private ItemStack item;

    public ArmourChangeEvent(Player who,ItemStack itemStack,ArmourType type,boolean isWear) {
        super(who);
        item = itemStack;
        this.type = type;
        wear = isWear;
        cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ArmourType getType() {
        return type;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isWear() {
        return wear;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
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

    public enum ArmourType {
        HELMET,CHESTPLATE,LEGGINGS,BOOTS
    }
}
