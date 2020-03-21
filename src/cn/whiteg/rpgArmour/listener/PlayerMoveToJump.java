package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.event.PlayerJumpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveToJump implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        final double y = event.getTo().getY() - event.getFrom().getY();
        if (y != 0.41999998688697815D && y != 0.5199999809265137 && y != 0.6200000047683716D)
            return;
        PlayerJumpEvent pj = new PlayerJumpEvent(event.getPlayer());
        pj.call();
    }
}
