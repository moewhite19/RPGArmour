package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.manager.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class recipe extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1 && sender instanceof final Player player){
            final String id = args[0];
            GUIManager.GUIAbs inv = RPGArmour.plugin.getRecipeManage().getRecipeInv(player,id);
            if (inv == null){
                sender.sendMessage("无效ID");
            }
        }
/*        if (args.length == 3 || args.length == 4){
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
        }*/
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


}
