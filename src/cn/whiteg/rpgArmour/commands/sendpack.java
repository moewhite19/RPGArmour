package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class sendpack extends CommandInterface {

    public static final String PERMISSION_SELF = "rpgarmour.sendpack.self";
    public static final String PERMISSION_OTHER = "rpgarmour.sendpack.other";

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0 && sender instanceof Player){
            if (!sender.hasPermission(PERMISSION_SELF)){
                sender.sendMessage("§b阁下没有权限");
                return false;
            }
            String[] s = ResourcePackManage.getConfig();
            if (s == null){
                sender.sendMessage("无资源包");
                return false;
            }
            ResourcePackManage.sendPack((Player) sender,s[0],s[1]);
            sender.sendMessage("已发送资源包");
            return true;
        }
        if (args.length == 1){
            if (!sender.hasPermission(PERMISSION_OTHER)){
                sender.sendMessage("§b阁下没有权限");
                return false;
            }

            String[] s = ResourcePackManage.getConfig();
            if (s == null){
                sender.sendMessage("无资源包");
                return false;
            }
            if (args[0].equals("@a")){
                Bukkit.getOnlinePlayers().forEach(p -> {
                    ResourcePackManage.sendPack(p,s[0],s[1]);
                });
                sender.sendMessage("给所有玩家发送资源包");
            } else {
                Player p = Bukkit.getPlayer(args[0]);
                if (p != null){
                    ResourcePackManage.sendPack(p,s[0],s[1]);
                    sender.sendMessage("已发送资源包");
                } else {
                    sender.sendMessage("找不到玩家");
                    return false;
                }
            }
            return true;
        } else {
            sender.sendMessage("§a/ride <玩家id>§b请求骑乘");
        }
        sender.sendMessage("无效参数");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return PlayersList(args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender commandSender) {
        return commandSender.hasPermission(PERMISSION_SELF);
    }

    @Override
    public String getDescription() {
        return "发送资源包";
    }

    public static void updateBoard() {
        //资源包更新完成
        String str = "ra sendpack";
        ComponentBuilder cb = new ComponentBuilder(" §b当前服务器资源包已更新§a/" + str + "§b§l >>点击更新<<");
        cb.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/" + str));
        cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent("点我使用指令")}));
        BaseComponent[] c = cb.create();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(PERMISSION_SELF)) player.spigot().sendMessage(c);
        }
    }
}
