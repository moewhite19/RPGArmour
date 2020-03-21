package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class setsize extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        {
            try{
                if (sender.hasPermission("whiteg.test")){
                    if (!(sender instanceof Player)) return false;
                    if (args.length == 2){
                        float r = Float.valueOf(args[1]);
                        Player player = (Player) sender;
                        Entity entity = player.getSpectatorTarget();
                        if (entity == null){
                            sender.sendMessage("找不到实体");
                            return false;
                        }
                        EntityUtils.setEntitySize(entity,r,r , true);
                    } else if (args.length == 3){
                        float w = Float.valueOf(args[1]);
                        float h = Float.valueOf(args[2]);
                        Player player = (Player) sender;
                        Entity entity = player.getSpectatorTarget();
                        if (entity == null){
                            sender.sendMessage("找不到实体");
                            return false;
                        }
                        EntityUtils.setEntitySize(entity,w,h , true);
                    }
                } else {
                    sender.sendMessage("阁下没有权限");
                }
            }catch (NumberFormatException e){
                sender.sendMessage("参数有误");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
