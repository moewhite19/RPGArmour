package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class ApiManager implements Listener {
    final RPGArmour plugin;

    public ApiManager(RPGArmour plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginLoad(PluginDisableEvent event) {

    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

    }
}
