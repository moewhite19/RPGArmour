package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class duck extends CustItem_CustModle implements Listener {

    public duck() {
        super(Material.BOWL,6,"§e小黄鸭");
    }

    @EventHandler
    public void onani(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        final Player player = event.getPlayer();
        if (player.hasCooldown(getMaterial())) return;
        final PlayerInventory pi = player.getInventory();
        if (is(pi.getItemInMainHand())){
            player.getWorld().playSound(player.getLocation(),"rpgarmour:items.duck_0",1,1);
            player.setCooldown(getMaterial(),15);
        }
    }
}

