package cn.whiteg.rpgArmour.manager;

import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    public Map<String, CommandInterface> commands = new HashMap<>();
    private String[] cmds;

    public CommandManager() {
        cmds = new String[]{"clear","clearconfirm","reload","worldentity","rideo","ride","rideme","test","give","summon","show","openinv","hat","getmode","eject","spawneff","setpack","sendpack","ghost","setsize","setbox" , "recipe"};
        for (String s : cmds) {
            try{
                commands.put(s,(CommandInterface) Class.forName("cn.whiteg.rpgArmour.commands." + s).newInstance());
            }catch (InstantiationException | IllegalAccessException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    public static List<String> getMatches(String value,List<String> list) {
        List<String> result = new ArrayList<>();
        int size = list.size();
        for (String str : list) {
            if (str.startsWith(value)){
                result.add(str);
            }
        }
        return result;
    }

    public void addComd(Class<? extends CommandExecutor> cls,String name) {
        try{
            commands.put(name,(CommandInterface) cls.newInstance());
        }catch (InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            sender.sendMessage("rpgArmour by 某白");
            return true;
        }
        if (commands.containsKey(args[0])){
            return commands.get(args[0]).onCommand(sender,cmd,label,args);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {

        if (args.length > 0){
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].toLowerCase();
            }
            if (commands.containsKey(args[0])){
                return commands.get(args[0]).onTabComplete(sender,cmd,label,args);
            }
            ArrayList<String> localArrayList = new ArrayList<>(Arrays.asList(cmds));
            return getMatches(args[0],localArrayList);
        }
        return null;
    }
}