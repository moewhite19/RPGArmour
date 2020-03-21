package cn.whiteg.rpgArmour.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerAttackMissEvent extends PlayerEvent implements Cancellable {
    private static HandlerList handers = new HandlerList();
    private boolean cancelled = false;

    public PlayerAttackMissEvent(Player player) {
        super(player);
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
}
