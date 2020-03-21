package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class openinv extends HasCommandInterface {
    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length >= 1){
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage("没有找到玩家");
                return true;
            }
            PlayerInventory inv = player.getInventory();
            if (sender instanceof Player sp){
                sp.openInventory(inv);
            }
            return true;
        }
        sender.sendMessage(getDescription());
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "打开玩家背包: §7<玩家ID>";
    }
}
