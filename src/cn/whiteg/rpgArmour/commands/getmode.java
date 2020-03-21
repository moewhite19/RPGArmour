package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class getmode extends HasCommandInterface {
    static WeakReference<List<String>> ls = new WeakReference<>(null);

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2 && sender instanceof Player player){
            try{
                Material mat = Material.valueOf(args[0]);
                int id = Integer.parseInt(args[1]);
                ItemStack item = new ItemStack(mat);
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(id);
                im.setDisplayName("ID: " + id);
                item.setItemMeta(im);
                player.getInventory().addItem(item);
            }catch (Exception e){
                sender.sendMessage("错误参数");
            }
        }
        return false;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            List<String> l = ls.get();
            if (l == null){
                l = new ArrayList<>();
                for (Material mat : Material.values()) {
                    l.add(mat.toString());
                }
                ls = new WeakReference<>(l);
            }
            return getMatches(l,args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "获取指定物品模型";
    }
}
