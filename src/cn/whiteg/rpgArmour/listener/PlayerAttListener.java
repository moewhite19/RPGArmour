package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.event.PlayerAttackMissEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerAttListener implements Listener {
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

}
