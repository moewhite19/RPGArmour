package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class summon extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("rpgarmour.summon")){
            sender.sendMessage("阁下没有权限");
            return false;
        }
        if (args.length == 2){
            if (!(sender instanceof Player)){
                sender.sendMessage("这个指令只有玩家能用");
                return true;
            }
            final Player player = (Player) sender;
            final String id = args[1];
            CustEntity ce = RPGArmour.plugin.getEntityManager().getCustEntity(id);
            if (ce != null){
                ce.summon(player.getLocation());
                sender.sendMessage("生成" + ce.getClass().getSimpleName());
                return true;
            } else {
                sender.sendMessage("没有找到ID " + id);
            }
        } else {
            sender.sendMessage("无效参数");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        List<String> ls = new ArrayList<>(RPGArmour.plugin.getEntityManager().getEntityNames());
        return getMatches(ls,args);
    }
}
