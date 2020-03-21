package cn.whiteg.rpgArmour.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class ArmourChangeEvent extends PlayerEvent implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    private ItemStack item;
    ArmourType type;
    boolean wear;
    boolean cancelled;

    public ArmourChangeEvent(Player who ,ItemStack itemStack , ArmourType type , boolean isWear) {
        super(who);
        item = itemStack;
        this.type = type;
        wear = isWear;
        cancelled = false;
    }

    public ArmourType getType() {
        return type;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item){
        this.item = item;
    }

    public boolean isWear() {
        return wear;
    }

    public void call(){
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
    public static HandlerList getHandlerList(){
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public enum ArmourType{
        HELMET , CHESTPLATE ,LEGGINGS ,BOOTS;
    }
}
