package cn.whiteg.rpgArmour.listener;

import cn.whiteg.mmocore.sound.Sound;
import cn.whiteg.rpgArmour.Setting;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemEatListener implements Listener {
    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Material mat = event.getItem().getType();
        Sound sound = Setting.getEatSoundMap().get(mat);
        if (sound != null) sound.playTo(event.getPlayer().getLocation());
    }
}
