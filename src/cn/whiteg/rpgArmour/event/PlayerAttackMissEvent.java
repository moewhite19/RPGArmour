package cn.whiteg.rpgArmour.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static cn.whiteg.rpgArmour.RPGArmour.plugin;

public class PlayerAttackMissEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handers = new HandlerList();

    static {
        Bukkit.getScheduler().runTask(plugin,() -> {
            plugin.regListener(new Listener() {
                private Player player;

                @EventHandler
                public void onLeft_Click(PlayerInteractEvent event) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
                        PlayerAttackMissEvent e = new PlayerAttackMissEvent(event.getPlayer());
                        Bukkit.getPluginManager().callEvent(e);
                    }
                }

                @EventHandler
                public void onEntityDamage(EntityDamageByEntityEvent event) {
                    if (player == null) return;
                    if (event.getDamager() instanceof Player){
                        player = null;
                    }
                }
            });
        });
    }

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
