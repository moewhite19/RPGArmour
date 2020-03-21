package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RepairCrystal extends CustItem_CustModle implements Listener {
    public RepairCrystal() {
        super(Material.BOWL,1,"§b修复水晶");
        setLore(Arrays.asList("","§3将物品拖动到想修复的工具上右键即可"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) return;
        ItemStack ci = event.getWhoClicked().getItemOnCursor();
        if (!is(ci)) return;
        ItemStack cu = event.getCurrentItem();
        if (cu == null || !cu.hasItemMeta()) return;
        if (ItemToolUtil.fixTool(cu)){
            if (ci.getAmount() > 1){
                ci.setAmount(ci.getAmount() - 1);
            } else {
                event.getWhoClicked().setItemOnCursor(null);
            }
            event.setCancelled(true);
        }
    }
}


