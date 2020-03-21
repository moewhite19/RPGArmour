package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
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

public class ghost extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            if (sender.hasPermission("whiteg.test")){
                ((Player) sender).setGameMode(GameMode.SPECTATOR);
                ArmorStand as = ((Player) sender).getWorld().spawn(((Player) sender).getLocation(),ArmorStand.class);
                as.setHelmet(((Player) sender).getEquipment().getHelmet());
                as.setVisible(false);
                as.setMarker(true);
                RPGArmour.plugin.regListener("other@" + sender.getName(),new moveListener((Player) sender,as));
            } else {
                sender.sendMessage("阁下没有权限");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    class moveListener implements Listener {
        final Player player;
        final ArmorStand ghost;

        moveListener(Player player,ArmorStand ghost) {
            this.player = player;
            this.ghost = ghost;
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                final Location loc = player.getLocation();
                ghost.teleport(loc);
                ghost.setHeadPose(new EulerAngle(loc.getPitch() / 45,0,0));
            }
        }

        @EventHandler
        public void onExit(PlayerQuitEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                RPGArmour.plugin.unregListener("other@Q" + player.getName());
            }
        }

        @EventHandler
        public void onMoeleChan(PlayerGameModeChangeEvent event) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                RPGArmour.plugin.unregListener("other@Q" + player.getName());
            }
        }

        public void unreg() {
            PlayerQuitEvent.getHandlerList().unregister(this);
            PlayerMoveEvent.getHandlerList().unregister(this);
            PlayerGameModeChangeEvent.getHandlerList().unregister(this);
            ghost.remove();
        }

    }
}
