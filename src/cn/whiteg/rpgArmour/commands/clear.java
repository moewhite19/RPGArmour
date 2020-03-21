package cn.whiteg.rpgArmour.commands;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.mmocore.common.HasCommandInterface;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.stream.Collectors;

public class clear extends HasCommandInterface {
    static public SoftReference<List<Entity>> ClearComfirm = new SoftReference<>(null);

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        try{
            if (args.length == 1){
                if (args[0].equals("confirm")){
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
                } else if (sender instanceof Player player){
                    double r;
                    String d = args[0];
                    if (d.equals("*")){
                        r = Double.MAX_VALUE;
                    } else r = Double.parseDouble(args[0]);
                    onClear(player,r,sender);
                }
            } else if (args.length == 2){
                if (args[0].equals("confirm")){
                    List<Entity> entitys = ClearComfirm.get();
                    if (entitys == null){
                        sender.sendMessage("没有实体可以清理");
                        return false;
                    }
                    EntityType et;
                    try{
                        et = EntityType.valueOf(args[1]);
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
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null){
                        sender.sendMessage("找不到玩家");
                        return false;
                    }
                    double r;
                    String d = args[0];
                    if (d.equals("*")){
                        r = Double.MAX_VALUE;
                    } else r = Double.parseDouble(args[0]);
                    onClear(player,r,sender);
                }

            }
        }catch (NumberFormatException e){
            sender.sendMessage("无效参数");
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
        Map<EntityType, Integer> map = new EnumMap<>(EntityType.class);
        for (Entity entity : es) {
            if (entity instanceof Player){
                continue;
            }
            entities.add(entity);
            map.put(entity.getType(),map.getOrDefault(entity.getType(),0) + 1);
            iar++;
        }
        ClearComfirm = new SoftReference<>(entities);

        //对map进行排序
        LinkedHashMap<EntityType, Integer> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(oldValue,newValue) -> oldValue,
                        LinkedHashMap::new));

        for (Map.Entry<EntityType, Integer> m : result.entrySet()) {
            TextComponent component = new TextComponent(m.getKey() + (Bukkit.getPluginManager().isPluginEnabled("ChanLang") ? "(" + LangUtils.getEntityTypeName(m.getKey()) + ")" : "") + " * " + m.getValue());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clear confirm " + m.getKey()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("§b清除这类")));
            sender.spigot().sendMessage(component);
        }
        TextComponent component = new TextComponent("发现了" + iar + "个实体");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/ra clear confirm"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("清除全部")));
        sender.spigot().sendMessage(component);
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "清理范围内实体>";
    }
}
