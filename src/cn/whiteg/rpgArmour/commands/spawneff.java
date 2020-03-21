package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class spawneff extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1 && sender instanceof Player player){
            try{
                byte b = Byte.parseByte(args[0]);
                boruadeff(player,b);
            }catch (NumberFormatException e){
                sender.sendMessage("无效参数");
            }
        } else if (args.length == 2){
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage("找不到玩家");
                return true;
            }
            try{
                byte b = Byte.parseByte(args[1]);
                boruadeff(player,b);
            }catch (NumberFormatException e){
                sender.sendMessage("无效参数");
            }
        }
        return true;
    }

    public void boruadeff(Entity entity,byte b) {
//        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
//        nmsEntity.t.broadcastEntityEffect(nmsEntity,b);
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return PlayersList(args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "生成效果（测试）";
    }
}
