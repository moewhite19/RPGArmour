package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZonZI extends CustItem_CustModle implements Listener {

    public ZonZI() {
        super(Material.PUMPKIN_PIE,4,"§2粽子");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvClick(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20 * 15,1));
    }
}


