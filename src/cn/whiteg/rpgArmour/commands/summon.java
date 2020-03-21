package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class summon extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            if (!(sender instanceof final Player player)){
                sender.sendMessage("这个指令只有玩家能用");
                return true;
            }
            final String id = args[0];
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
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        List<String> ls = new ArrayList<>(RPGArmour.plugin.getEntityManager().getCustEntityKeys());
        return getMatches(ls,args);
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "生成自定义实体";
    }
}
