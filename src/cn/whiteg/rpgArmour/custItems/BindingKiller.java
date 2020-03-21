package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BindingKiller extends CustItem_CustModle implements Listener {
    public BindingKiller() {
        super(Material.BOWL,45,"§b绑定杀手");
        setLore(Arrays.asList("","§3将物品拖动到想去掉的的绑定准备上右键即可"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) return;
        ItemStack ci = event.getWhoClicked().getItemOnCursor();
        if (!is(ci)) return;
        ItemMeta cim = ci.getItemMeta();
        if (cim.getCustomModelData() != getId()) return;
        //
        ItemStack cu = event.getCurrentItem();
        if (cu == null || cu.getType() == Material.AIR) return;
        if (ci.getAmount() > 1){
            ci.setAmount(ci.getAmount() - 1);
        } else {
            event.getWhoClicked().setItemOnCursor(null);
        }
        event.setCurrentItem(null);
        event.setCancelled(true);
    }
}


