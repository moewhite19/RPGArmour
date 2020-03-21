package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class reload extends HasCommandInterface {
    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        var plugin = RPGArmour.plugin;
//        plugin.onDisable();
//        plugin.onLoad();
//        plugin.onEnable();
//        plugin.getRecipeManage().onSync();
        Setting.reload();
        sender.sendMessage("重载完成");
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

}
