package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.ref.WeakReference;
import java.util.Map;

public class Craftting implements Listener {
    WeakReference<CustItem> LATE_CUST_ITEM = new WeakReference<>(null); //懒得写基于ID的MAP映射，先偷懒x

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onRename(PrepareAnvilEvent event) {
        final AnvilInventory inv = event.getInventory();
        final ItemStack item = inv.getFirstItem();
        final ItemStack result = event.getResult();
        if (result == null || item == null || !item.hasItemMeta()) return; //为空跳出
        final ItemMeta itemMeta = item.getItemMeta();
        final Component displayName = itemMeta.displayName();

        if (displayName == null && !itemMeta.hasCustomModelData()){
            return; //如果既没有自定义id也没自定义名字跳出
        }
        final String renameText = inv.getRenameText();
        //如果名字为空，设为原名称
        if (renameText == null || renameText.isBlank()){
            final CustItem late = LATE_CUST_ITEM.get();
            if (late != null && late.is(item)){
                final ItemMeta meta = result.getItemMeta();
                meta.setDisplayName(late.getDisplayName());
                result.setItemMeta(meta);
            } else {
                for (Map.Entry<String, CustItem> custItemEntry : RPGArmour.plugin.getItemManager().getItems().entrySet()) {
                    final CustItem custItem = custItemEntry.getValue();
                    if (custItem.is(item)){
                        final ItemMeta meta = result.getItemMeta();
                        meta.setDisplayName(custItem.getDisplayName());
                        result.setItemMeta(meta);
                        LATE_CUST_ITEM = new WeakReference<>(custItem);
                        return;
                    }
                }
            }
        } else {
            //重命名物品保留颜色
            Style style = null;
            //noinspection ConstantConditions
            for (Component child : displayName.children()) {
                if (child.hasStyling()){
                    style = child.style();
                }
            }

            if (style != null){
                final ItemMeta meta = result.getItemMeta();
                final Component newName = Component.text(renameText).style(style);
                meta.displayName(newName);
                result.setItemMeta(meta);
            }
        }
    }

    //    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
        final InventoryType ct = event.getInventory().getType();
        final int s = event.getRawSlot();
        switch (ct) {
            case ANVIL:
            case GRINDSTONE:
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

    //    @EventHandler
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
