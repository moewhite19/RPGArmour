package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class setbox extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        try{
            if (sender.hasPermission("whiteg.test")){
                if (!(sender instanceof Player)) return false;
                if (args.length == 1){
                    Player player = (Player) sender;
                    Entity entity = player.getSpectatorTarget();
                    if (entity == null){
                        sender.sendMessage("找不到实体");
                        return false;
                    }
                    EntityUtils.setBoundingBox(entity,player.getBoundingBox());
                }
            } else {
                sender.sendMessage("阁下没有权限");
            }
        }catch (NumberFormatException e){
            sender.sendMessage("参数有误");
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

    @Override
    public String getDescription() {
        return "设置碰撞箱";
    }
}
