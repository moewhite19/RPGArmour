package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class give extends HasCommandInterface {
    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1 && sender instanceof final Player player){
            final String id = args[0];
            final ItemStack item = RPGArmour.plugin.getItemManager().createItem(id);
            if (item != null){
                final String im = item.getItemMeta().getDisplayName();
                sender.sendMessage("§b给与§r " + im);
//                player.sendMessage("§b收到来自§r" + sender.getName() + "给与的" + im);
                player.getInventory().addItem(item);
            } else {
                sender.sendMessage("没有找到ID " + id);
            }
        } else if (args.length >= 2){
            final Player player = Bukkit.getPlayer(args[1]);
            if (player == null){
                sender.sendMessage("没有找到玩家");
                return false;
            }
            final String id = args[0];
            ItemStack item;
            if (args.length > 2){
                List<String> arg = new ArrayList<>(args.length - 2);
                arg.addAll(Arrays.asList(args).subList(2,args.length));
                item = RPGArmour.plugin.getItemManager().createItem(id,arg);
            } else {
                item = RPGArmour.plugin.getItemManager().createItem(id);
            }
            if (item == null){
                sender.sendMessage("没有找到ID " + id);
                return false;
            }
            if (args.length == 3){
                try{
                    final int i = Integer.parseInt(args[2]);
                    item.setAmount(i);
                }catch (NumberFormatException e){
                    sender.sendMessage("无效数量参数");
                }
            }
            final String im = item.getItemMeta().getDisplayName();
            sender.sendMessage("§b给与" + player.getName() + " §r " + im);
            if (sender != player) player.sendMessage("§b收到来自§r" + sender.getName() + "给与的" + im);
            player.getInventory().addItem(item);
            return true;
        }
        return false;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            final List<String> ls = new ArrayList<>(RPGArmour.plugin.getItemManager().getItemNames());
            return getMatches(ls,args);
        } else if (args.length == 2){
            return PlayersList(args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("rpgarmour.give");
    }

    @Override
    public String getDescription() {
        return "给与物品";
    }
}
