package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.SoftReference;
import java.util.*;

public class clear extends CommandInterface {
    static public SoftReference<List<Entity>> ClearComfirm = new SoftReference<>(null);

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            try{
                if (args.length == 2){
                    if (args[1].equals("confirm")){
                        List<Entity> entitys = ClearComfirm.get();
                        if (entitys == null){
                            sender.sendMessage("没有实体可以清理");
                            return false;
                        }
                        for (Entity entity : entitys) {
                            entity.remove();
                        }
                        sender.sendMessage("清理了" + entitys.size() + "个实体");
                        ClearComfirm.clear();
                    } else if (sender instanceof Player){
                        double r;
                        String d = args[1];
                        if (d.equals("*")){
                            r = Double.MAX_VALUE;
                        } else r = Double.valueOf(args[1]);
                        Player player = (Player) sender;
                        onClear(player,r,sender);
                    }
                } else if (args.length == 3){
                    if (args[1].equals("confirm")){
                        List<Entity> entitys = ClearComfirm.get();
                        if (entitys == null){
                            sender.sendMessage("没有实体可以清理");
                            return false;
                        }
                        EntityType et;
                        try{
                            et = EntityType.valueOf(args[2]);
                        }catch (IllegalArgumentException e){
                            sender.sendMessage("无效ID");
                            return false;
                        }
                        int num = 0;
                        for (int i = entitys.size() - 1; i >= 0; i--) {
                            if (entitys.get(i).getType() != et) continue;
                            entitys.get(i).remove();
                            entitys.remove(i);
                            num++;
                        }
                        sender.sendMessage("清理了" + num + "个实体");
                    } else {
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null){
                            sender.sendMessage("找不到玩家");
                            return false;
                        }
                        double r;
                        String d = args[1];
                        if (d.equals("*")){
                            r = Double.MAX_VALUE;
                        } else r = Double.valueOf(args[1]);
                        onClear(player,r,sender);
                    }

                }
            }catch (NumberFormatException e){
                sender.sendMessage("无效参数");
            }
        } else {
            sender.sendMessage("阁下没有权限");
        }
        return false;
    }

    public void onClear(Player player,double r,CommandSender sender) {
        Collection<Entity> es;
        if (r == Double.MAX_VALUE){
            es = player.getWorld().getEntities();
        } else {
            es = player.getNearbyEntities(r,r,r);
        }
        //  debuger.logout("玩家" + player.getName() + " 位置 " + pl.toString());
        List<Entity> entities = new ArrayList<>();
        int iar = 0;
        Map<EntityType, Integer> map = new EnumMap<EntityType, Integer>(EntityType.class);
        for (Entity entity : es) {
            if (es instanceof Player){
                continue;
            }
            entities.add(entity);
            map.put(entity.getType(),map.getOrDefault(entity.getType(),0) + 1);
            iar++;
        }
        ClearComfirm = new SoftReference<>(entities);
        for (Map.Entry m : map.entrySet()) {
            TextComponent a1 = new TextComponent(m.getKey() + " * " + m.getValue());
            a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clear confirm " + m.getKey()));
            a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("清除这类").color(ChatColor.BLUE).create()));
            sender.spigot().sendMessage(a1);
        }
        TextComponent a1 = new TextComponent("发现了" + iar + "个实体");
        a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clear confirm"));
        a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("清除全部").color(ChatColor.BLUE).create()));
        sender.spigot().sendMessage(a1);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
