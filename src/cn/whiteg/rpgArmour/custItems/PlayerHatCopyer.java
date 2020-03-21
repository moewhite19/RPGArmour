package cn.whiteg.rpgArmour.custItems;


import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.ArmourChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHatCopyer extends CustItem_CustModle implements Listener {
    public PlayerHatCopyer() {
        super(Material.BOWL,37,"§b戴立得头套");
    }


    @EventHandler(ignoreCancelled = true)
    public void onWear(ArmourChangeEvent event) {
        if (event.getType() == ArmourChangeEvent.ArmourType.HELMET && is(event.getItem())){
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                PlayerInventory pi = event.getPlayer().getInventory();
                if (!is(pi.getHelmet())) return;
                final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                final SkullMeta im = (SkullMeta) head.getItemMeta();
                im.setOwningPlayer(event.getPlayer());
                head.setItemMeta(im);
                int am = pi.getHelmet().getAmount();
                head.setAmount(am);
                pi.setHelmet(head);
            });
        }
    }
}

