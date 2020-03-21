package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.whiteg.rpgArmour.RPGArmour.debuger;

public class worldentity extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 1){
                Player player = (Player) sender;
                Location pl = player.getLocation();
                List<Entity> es = pl.getWorld().getEntities();
                //  debuger.logout("玩家" + player.getName() + " 位置 " + pl.toString());
                int iar = 0;
                Map<EntityType, Integer> map = new HashMap<>();
                for (Entity entity : es) {
                    if (!entity.getName().equals(player.getName())){
                        debuger.logout("符合");
                        map.put(entity.getType(),map.getOrDefault(entity.getType(),1) + 1);
                        iar++;
                    }
                }
                for (Map.Entry m : map.entrySet()) {
                    TextComponent a1 = new TextComponent(m.getKey() + " * " + m.getValue());
                    a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clearconfirm " + m.getKey()));
                    a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("清除这类").color(ChatColor.BLUE).create()));
                    sender.spigot().sendMessage(a1);
                }
                TextComponent a1 = new TextComponent("发现了" + iar + "个实体");
                a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clearconfirm"));
                a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("清除全部").color(ChatColor.BLUE).create()));
                sender.spigot().sendMessage(a1);
            }
        } else {
            sender.sendMessage("阁下没有权限");
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
