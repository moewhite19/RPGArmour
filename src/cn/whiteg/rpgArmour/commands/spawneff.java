package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import net.minecraft.server.v1_16_R1.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class spawneff extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            return true;
        }
        if (args.length == 2 && sender instanceof Player){
            Player player = (Player) sender;
            try{
                byte b = Byte.valueOf(args[1]);
                boruadeff(player,b);
            }catch (NumberFormatException e){
                sender.sendMessage("无效参数");
            }
        } else if (args.length == 3){
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null){
                sender.sendMessage("找不到玩家");
                return true;
            }
            try{
                byte b = Byte.valueOf(args[2]);
                boruadeff(player,b);
            }catch (NumberFormatException e){
                sender.sendMessage("无效参数");
            }
        }
        return true;
    }

    public void boruadeff(Entity entity,byte b) {
        EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();
        nmsEntity.world.broadcastEntityEffect(nmsEntity,b);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            List<String> ls = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                ls.add(p.getName());
            }
            ls.remove(sender.getName());
            return getMatches(args[1],ls);
        }
        return null;
    }
}
