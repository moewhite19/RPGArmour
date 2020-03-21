package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class itemf extends CustItem_CustModle implements Listener {

    public itemf() {
        super(Material.SHEARS,30,"§e法杖");
    }

    @EventHandler
    public void onUse(PlayerInteractAtEntityEvent event) {
        final PlayerInventory pi = event.getPlayer().getInventory();
        ItemStack item;
        if (event.getHand() == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else {
            item = pi.getItemInOffHand();
        }
        if (is(item)){
            final Entity e = event.getRightClicked();
            if (e instanceof ItemFrame){
                ItemFrame frame = (ItemFrame) e;
//                frame.;
            } else {
                event.getPlayer().sendMessage("不支持对象");
            }
        }
    }
}

