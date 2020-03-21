package cn.whiteg.rpgArmour.listener;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Craftting implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
//        Inventory ci = event.getClickedInventory();
//        if (ci == null) return;
        final InventoryType ct = event.getInventory().getType();
        final int s = event.getRawSlot();
        switch (ct) {
            case ANVIL:
            case GRINDSTONE:
//                if (s == 1 || s == 0){
//                    if (has(event.getCursor())){
//                        event.setCancelled(true);
//                    }
//                } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY){
//                    if (has(event.getCurrentItem())){
//                        event.setCancelled(true);
//                    }
//                }
                if (s != 2) return;
                for (int i = 0; i < 2; i++) {
                    if (has(event.getInventory().getItem(i))){
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
            case ENCHANTING:
                if (s == 0){
                    if (has(event.getCursor())){
                        event.setCancelled(true);
                    }
                } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                    if (has(event.getCurrentItem())){
                        event.setCancelled(true);
                    }
                }
                break;
            case WORKBENCH:
//                if (s > 0 && s < 10 && has(event.getCursor())){
//                    event.setCancelled(true);
//                }
                if (s != 0) return;
                for (int i = 1; i < 10; i++) {
                    if (has(event.getInventory().getItem(i))){
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
            case CRAFTING:
//                if (s > 0 && s < 5 && has(event.getCursor())){
//                    event.setCancelled(true);
//                }
                if (s != 0) return;
                for (int i = 1; i < 5; i++) {
                    if (has(event.getInventory().getItem(i))){
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
        }
//        event.getWhoClicked().sendMessage(ct.toString() + " " + event.getAction() + " " + event.getSlot() + ":" + event.getRawSlot());
    }

    @EventHandler
    public void mend(PlayerItemMendEvent event) {
        if (has(event.getItem())){
            Map<Enchantment, Integer> m = event.getItem().getEnchantments();
            for (Map.Entry<Enchantment, Integer> entry : m.entrySet()) {
                event.getItem().removeEnchantment(entry.getKey());
            }
        }
    }

    public boolean has(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        final ItemMeta m = item.getItemMeta();
        return m.hasCustomModelData();
    }
}
