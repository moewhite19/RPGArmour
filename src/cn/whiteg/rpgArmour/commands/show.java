package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class show extends HasCommandInterface {
    Reference<List<String>> ls = new WeakReference<>(null);

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length >= 1){
            final Player player = (Player) sender;
            final Inventory inv = Bukkit.createInventory(null,54,"§b模型列表");
            int page;
            final Material mat;
            try{
                mat = Material.valueOf(args[0]);
            }catch (IllegalArgumentException e){
                sender.sendMessage("无效物品");
                return false;
            }
            if (args.length == 2){
                try{
                    page = Integer.parseInt(args[1]);
                }catch (NumberFormatException e){
                    //eject.printStackTrace();
                    page = -1;
                }
            } else {
                page = 0;
            }
            if (page > 64 || page < 0){
                sender.sendMessage("请输入正确的数值");
                return true;
            }
            for (int i = page * 54; i < (page * 54) + 54; i++) {
                final ItemStack item;
                item = new ItemStack(mat);
                ItemMeta im = item.getItemMeta();
                if (im != null){
                    im.setCustomModelData(i);
                    im.setDisplayName("§7ID: §f" + i);
                    item.setItemMeta(im);
                }
                //im.getAttributeModifiers().clear();
                inv.addItem(item);
            }
            player.openInventory(inv);
        }
        return true;
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
        return sender.hasPermission("rpgarmour.show");
    }

    @Override
    public String getDescription() {
        return "获取模型菜单";
    }
}
