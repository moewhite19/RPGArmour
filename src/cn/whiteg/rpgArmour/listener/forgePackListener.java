package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class forgePackListener implements Listener {
    private final List<UUID> players = new LinkedList<>();

    @EventHandler
    public void resourcePackChan(PlayerResourcePackStatusEvent event) {
        final Player p = event.getPlayer();
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD){
//            kickPlayer(event.getPlayer(),"§b§l阁下的材质包加载错误\\n §r请尝试重新登录服务器或者联系管理员");
            p.sendMessage("§b§l阁下的材质包加载错误\\n §r请尝试重新登录服务器或者联系管理员");
        } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED){
            if (p.hasPermission("whiteg.test")){
                players.remove(p.getUniqueId());
                return;
            }
            kickPlayer(p,"    §b§l阁下因为没有接受资源包而被拒绝加入服务器\n\n    §f请在服务器列表编辑服务器,把服务器资源包设置为“允许”\n  或者服务器从列表移除重新添加IP地址然后进入服务器后接受服务器的资源包");
        } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED){
            players.remove(p.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUserCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        if (has(p)){
            event.setCancelled(true);
            String[] pack = ResourcePackManage.get();
            p.sendMessage("§b§l阁下因为没有接受资源包无法使用指令 ，尝试重新发送资源包");
            if (pack != null){
                ResourcePackManage.sendPack(p,pack[0],pack[1]);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerLoginEvent event) {
        players.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent event) {
        if (players.isEmpty()) return;
        players.remove(event.getPlayer().getUniqueId());
        ref();
    }

    public boolean has(Player player) {
        if (players.isEmpty()) return false;
        return players.contains(player.getUniqueId());
    }

    private void kickPlayer(Player player,String msg) {
        player.kickPlayer(msg);
        players.remove(player.getUniqueId());
    }

    private void ref() {
        if (!players.isEmpty()){
            players.removeIf(u -> Bukkit.getPlayer(u) == null);
        }
    }
}
