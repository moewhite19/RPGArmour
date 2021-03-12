package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class eject extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0 && sender instanceof Player){
            Player player = (Player) sender;
            ((Player) sender).eject();
        } else if (args.length == 1){
            if (!sender.hasPermission("mmo.eject.other")){
                sender.sendMessage("阁下没有权限");
                return true;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage("找不到玩家");
                return true;
            }
            player.eject();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            List<String> ls = PlayersList(args);
            ls.remove(sender.getName());
            return ls;
        }
        return null;
    }
}
