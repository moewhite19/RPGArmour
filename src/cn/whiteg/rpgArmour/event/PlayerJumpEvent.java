package cn.whiteg.rpgArmour.event;

import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerJumpEvent extends PlayerEvent implements Cancellable {
    private static HandlerList handlerList = new HandlerList();

    static {
        RPGArmour.plugin.regListener(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onMove(PlayerMoveEvent event) {
                final double y = event.getTo().getY() - event.getFrom().getY();
                if (y != 0.41999998688697815D && y != 0.5199999809265137 && y != 0.6200000047683716D)
                    return;
                PlayerJumpEvent pj = new PlayerJumpEvent(event.getPlayer());
                pj.call();
            }
        });
    }

    private boolean c = false;

    public PlayerJumpEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    @Override
    public void setCancelled(boolean b) {
        c = b;
    }
}
