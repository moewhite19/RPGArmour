package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.utils.RideManage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class rideo extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            if (sender.hasPermission("whiteg.test")){
                if (args.length == 2){
                    Player p = (Player) sender;
                    Player o = Bukkit.getPlayer(args[1]);
                    if (RideManage.Ride(p,o)){
                        sender.sendMessage("已骑上去");
                    } else {
                        sender.sendMessage("未知原因没有骑上去");
                    }
                }
                if (args.length == 3){
                    Player p1 = Bukkit.getPlayer(args[1]);
                    Player p2 = Bukkit.getPlayer(args[2]);
                    if (p1 == null || p2 == null){
                        sender.sendMessage("§b玩家不存在");
                    }
                    if (RideManage.Ride(p1,p2)){
                        sender.sendMessage("已骑上去");
                    } else {
                        sender.sendMessage("未知原因没有骑上去");
                    }
                }
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
}
