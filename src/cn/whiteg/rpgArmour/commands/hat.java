package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.event.ArmourChangeEvent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class hat extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length < 2){
            if(!(sender instanceof Player))return true;
            if (!sender.hasPermission("mmo.hat")){
                sender.sendMessage("§b阁下没有权限使用这个指令");
                return true;
            }
            Player player = (Player) sender;
            PlayerInventory pi = player.getInventory();
            if(pi.getHelmet()!=null){
                player.sendMessage("§b阁下头上已经有一个帽子了");
                return false;
            }
            ItemStack item = pi.getItemInMainHand();
            if(item.getType() == Material.AIR){
                player.sendMessage("§b阁下想把自己的右手到带头上吗？");
                return false;
            }
            pi.setHelmet(item);
            pi.setItemInMainHand(null);
            ArmourChangeEvent event = new ArmourChangeEvent(player , item , ArmourChangeEvent.ArmourType.HELMET , true);
            event.call();
            if(event.isCancelled())return false;
            player.sendMessage("§b享受阁下的新帽子吧");
            return true;
        }
        return false;
    }
}
