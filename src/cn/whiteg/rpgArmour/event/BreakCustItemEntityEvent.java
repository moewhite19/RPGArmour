package cn.whiteg.rpgArmour.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

public class BreakCustItemEntityEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handers = new HandlerList();
    private ItemStack itemStack;
    private boolean cancelled = false;
    private Entity damager;

    public BreakCustItemEntityEvent(Entity entity,ItemStack itemStack) {
        super(entity);
        this.itemStack = itemStack;
    }

    public BreakCustItemEntityEvent(Entity entity,ItemStack itemStack,Entity damager) {
        super(entity);
        this.itemStack = itemStack;
        this.damager = damager;
    }

    public static HandlerList getHandlerList() {
        return handers;
    }

    @Override
    public HandlerList getHandlers() {
        return handers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public Entity getDamager() {
        return damager;
    }

    public ItemStack getDropStack() {
        return itemStack;
    }

    public void setDropItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
