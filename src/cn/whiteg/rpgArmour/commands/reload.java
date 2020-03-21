package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class reload extends CommandInterface {
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if ((!sender.hasPermission("rpgarmour.reload"))){
            sender.sendMessage("阁下没有权限使用这个指令");
            return true;
        }
        RPGArmour.plugin.onDisable();
        RPGArmour.plugin.onLoad();
        RPGArmour.plugin.onEnable();
        sender.sendMessage("重载完成");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
