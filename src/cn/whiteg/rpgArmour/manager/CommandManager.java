package cn.whiteg.rpgArmour.manager;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.command.*;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    final private String[] cmds = new String[]{"clear","reload","rideo","ride","rideme","test","give","summon","show","openinv","hat","getmode","eject","spawneff","setpack","sendpack","ghost","setsize","setbox","recipe"};
    final public Map<String, CommandInterface> commands = new HashMap<>(cmds.length);

    public CommandManager() {
        SubCommand subCommand = new SubCommand();
        for (String cmd : cmds) {
            try{
                commands.put(cmd,(CommandInterface) Class.forName("cn.whiteg.rpgArmour.commands." + cmd).newInstance());
                PluginCommand pc = RPGArmour.plugin.getCommand(cmd);
                if (pc != null){
                    pc.setExecutor(subCommand);
                    pc.setTabCompleter(subCommand);
                }
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

    public class SubCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(CommandSender commandSender,Command command,String s,String[] strings) {
            final CommandInterface ci = commands.get(command.getName());
            if (ci == null) return false;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            ci.onCommand(commandSender,command,s,args);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
            CommandInterface ci = commands.get(command.getName());
            if (ci == null) return null;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            return ci.onTabComplete(commandSender,command,s,args);
        }
    }
}