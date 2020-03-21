package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class give extends CommandInterface {
    final static String pex = "rpgarmour.give";

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission(pex)){
            return false;
        }
        if (args.length == 2 && sender instanceof Player){
            final Player player = (Player) sender;
            final String id = args[1];
            final ItemStack item = RPGArmour.plugin.getItemManager().createItem(id);
            if (item != null){
                final String im = item.getItemMeta().getDisplayName();
                sender.sendMessage("§b给与§r " + im);
                player.sendMessage("§b收到来自§r" + sender.getName() + "给与的" + im);
                player.getInventory().addItem(item);
            } else {
                sender.sendMessage("没有找到ID " + id);
            }
        } else if (args.length == 3 || args.length == 4){
            final Player player = Bukkit.getPlayer(args[2]);

            if (player == null){
                sender.sendMessage("没有找到玩家");
                return false;
            }
            final String id = args[1];
            final ItemStack item = RPGArmour.plugin.getItemManager().createItem(id);
            if (item != null){
                if (args.length == 4){
                    try{
                        final int i = Integer.valueOf(args[3]);
                        item.setAmount(i);
                    }catch (NumberFormatException e){
                        sender.sendMessage("无效数量参数");
                    }
                }
                final String im = item.getItemMeta().getDisplayName();
                sender.sendMessage("§b给与" + player.getName() + " §r " + im);
                player.sendMessage("§b收到来自§r" + sender.getName() + "给与的" + im);
                player.getInventory().addItem(item);
            } else {
                sender.sendMessage("没有找到ID " + id);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission(pex)) return null;
        if (args.length == 2){
            final List<String> ls = new ArrayList<>(RPGArmour.plugin.getItemManager().getItemNames());
            return getMatches(ls,args);
        } else if (args.length == 3){
            return PlayersList(args);
        }
        return null;
    }
}
