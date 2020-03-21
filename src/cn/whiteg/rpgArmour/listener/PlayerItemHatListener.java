package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;

public class PlayerItemHatListener implements Listener {
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Material tp = event.getItem().getType();
        String sound;
        sound = Setting.getConfig().getString("EatSound." + tp.toString());
        if (sound == null) return;
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),sound,1,1);
        // event.getPlayer().chat("真香");

    }
}
