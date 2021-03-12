package cn.whiteg.rpgArmour.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class HasCommandInterface extends CommandInterface {
    public abstract boolean executor(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String str,@NotNull String[] args);

    public List<String> completer(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String str,@NotNull String[] args) {
        return PlayersList(args);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String str,@NotNull String[] args) {
        if (canUseCommand(sender)){
            return executor(sender,cmd,str,args);
        } else {
            sender.sendMessage("§b阁下当前不能使用这条指令");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (canUseCommand(sender)){
            return completer(sender,cmd,label,args);
        } else return null;
    }
}
