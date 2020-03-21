package cn.whiteg.rpgArmour;

import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommand implements CommandExecutor, TabCompleter {
    final private Map<String, CommandInterface> cmdmap;

    public SubCommand(Map<String, CommandInterface> commandmap) {
        cmdmap = commandmap;
    }

    public SubCommand() {
        cmdmap = new HashMap<>();
    }

    public void regCommand(String name,CommandInterface command) {
        cmdmap.put(name,command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender,Command command,String s,String[] strings) {
        final CommandInterface ci = cmdmap.get(command.getName());
        if (ci == null) return false;
        String[] args = new String[strings.length + 1];
        args[0] = command.getName();

        System.arraycopy(strings,0,args,1,strings.length);
        ci.onCommand(commandSender,command,s,args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
        CommandInterface ci = RPGArmour.plugin.commandManager.commands.get(command.getName());
        if (ci == null) return null;
        String[] args = new String[strings.length + 1];
        args[0] = command.getName();
        System.arraycopy(strings,0,args,1,strings.length);
        return ci.onTabComplete(commandSender,command,s,args);
    }
}
