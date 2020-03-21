package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class sendpack extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0 && sender instanceof Player){
            if (!sender.hasPermission("rpgarmour.sendpack.self")){
                sender.sendMessage("§b阁下没有权限");
                return false;
            }
            String[] s = ResourcePackManage.getConfig();
            if (s == null){
                sender.sendMessage("无资源包");
                return false;
            }
            ResourcePackManage.sendPack((Player) sender,s[0],s[1]);
            sender.sendMessage("已发送资源包");
            return true;
        }
        if (args.length == 1){
            if (!sender.hasPermission("rpgarmour.sendpack.other")){
                sender.sendMessage("§b阁下没有权限");
                return false;
            }

            String[] s = ResourcePackManage.getConfig();
            if (s == null){
                sender.sendMessage("无资源包");
                return false;
            }
            if (args[0].equals("@a")){
                Bukkit.getOnlinePlayers().forEach(p -> {
                    ResourcePackManage.sendPack(p,s[0],s[1]);
                });
                sender.sendMessage("给所有玩家发送资源包");
                return true;
            }
            Player p = Bukkit.getPlayer(args[0]);
            if (p == null){
                sender.sendMessage("找不到玩家");
                return false;
            }
            ResourcePackManage.sendPack(p,s[0],s[1]);
            sender.sendMessage("已发送资源包");
            return true;
        } else {
            sender.sendMessage("§a/ride <玩家id>§b请求骑乘");
        }
        sender.sendMessage("无效参数");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return PlayersList(args);
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "发送资源包";
    }
}
