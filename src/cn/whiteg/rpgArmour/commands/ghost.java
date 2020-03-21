package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.EulerAngle;

import java.util.List;

public class ghost extends HasCommandInterface {
    moveListener listener;

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (listener != null){
            listener.unreg();
        }
        if (sender instanceof Player){
            ((Player) sender).setGameMode(GameMode.SPECTATOR);
            ArmorStand as = ((Player) sender).getWorld().spawn(((Player) sender).getLocation(),ArmorStand.class);
            as.setHelmet(((Player) sender).getEquipment().getHelmet());
            as.setVisible(false);
            as.setMarker(true);
            listener = new moveListener((Player) sender,as);
            RPGArmour.plugin.regListener(listener);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }


    static class moveListener implements Listener {
        final Player player;
        final ArmorStand ghost;

        moveListener(Player player,ArmorStand ghost) {
            this.player = player;
            this.ghost = ghost;
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                if (player.getGameMode() != GameMode.SPECTATOR) unreg();
                final Location loc = player.getLocation();
                ghost.teleport(loc);
                ghost.setHeadPose(new EulerAngle(loc.getPitch() / 45,0,0));
            }
        }

        @EventHandler
        public void onExit(PlayerQuitEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                unreg();
            }
        }

        @EventHandler
        public void onMoeleChan(PlayerGameModeChangeEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                unreg();
            }
        }

        public void unreg() {
            RPGArmour.plugin.unregListener(this);
            ghost.remove();
        }
    }

    @Override
    public String getDescription() {
        return "显示帽子的过程中模式";
    }
}
