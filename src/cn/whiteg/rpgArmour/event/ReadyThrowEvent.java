package cn.whiteg.rpgArmour.event;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ReadyThrowEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    static {
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            RPGArmour.plugin.regListener(
                    new Listener() {
                        @EventHandler()
                        public void onUse(PlayerInteractEvent event) {
                            ItemStack item = event.getItem();
                            if (item == null) return;
                            Material type = item.getType();
                            if (type == Material.BOW || type == Material.TRIDENT){
                                Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                                    int useTick = EntityUtils.getItemUseTimeLeft(event.getPlayer());
                                    if (useTick == getMaxUseTick()){
                                        ReadyThrowEvent e = new ReadyThrowEvent(event);
                                        e.call();
                                    }
                                });
                            }
                        }
                    });
        });
    }

    private final PlayerInteractEvent handler;

    private boolean c = false;

    public ReadyThrowEvent(PlayerInteractEvent handler) {
        super(handler.getPlayer());
        this.handler = handler;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public static int getMaxUseTick() {
        return 72000;
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

    public PlayerInteractEvent getHandler() {
        return handler;
    }
}
