package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class setsize extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        try{
            if (!(sender instanceof Player)) return false;
            if (args.length == 1){
                float r = Float.parseFloat(args[0]);
                Player player = (Player) sender;
                Entity entity = player.getSpectatorTarget();
                if (entity == null){
                    sender.sendMessage("找不到实体");
                    return false;
                }
                EntityUtils.setEntitySize(entity,r,r,true);
            } else if (args.length == 2){
                float w = Float.parseFloat(args[0]);
                float h = Float.parseFloat(args[1]);
                Player player = (Player) sender;
                Entity entity = player.getSpectatorTarget();
                if (entity == null){
                    sender.sendMessage("找不到实体");
                    return false;
                }
                EntityUtils.setEntitySize(entity,w,h,true);
            }
        }catch (NumberFormatException e){
            sender.sendMessage("参数有误");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "设置实体大小";
    }
}
