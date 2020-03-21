package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import static cn.whiteg.rpgArmour.RPGArmour.ClearComfirm;

import java.util.List;

public class clearconfirm extends CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b阁下没有权限哦");
            return true;
        }
        List<Entity> entitys = ClearComfirm.get();           if(entitys==null){
            sender.sendMessage("没有实体可以清理");
            return true;
        }
        if (args.length == 1){
            if (entitys.size() > 0){
                for (Entity entity : entitys) {
                    entity.remove();
                }
            }
            sender.sendMessage("清理了" + entitys.size() + "个实体");
            ClearComfirm.clear();
        } else if (args.length == 2){
            EntityType et = null;
            try{
                et = EntityType.valueOf(args[1]);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            if (et != null){
                int num = 0;
                for (int i = entitys.size() - 1; i >= 0; i--) {
                    if (entitys.get(i).getType() != et) continue;
                    entitys.get(i).remove();
                    entitys.remove(i);
                    num++;
                }
                sender.sendMessage("清理了" + num + "个实体");

/*                        for (Entity entity : ClearComfirm) {
                            if (entity.getType() == et) entity.remove();
                        }*/
            } else {
                sender.sendMessage("未知实体");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
