package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements Listener {
    final static Map<UUID, GUIAbs> map = new HashMap<>();
    final RPGArmour plugin;

    public GUIManager(RPGArmour rpgArmour) {
        plugin = rpgArmour;
        plugin.regListener(this);
    }

    public void openGui(Player player,GUIAbs gui) {
        map.put(player.getUniqueId(),gui);
        InventoryView inv = player.getOpenInventory();
        if (inv.hashCode() != gui.getInv().hashCode()){
            player.openInventory(gui.getInv());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (map.isEmpty()) return;
        GUIAbs gui = map.get(event.getWhoClicked().getUniqueId());
        if (gui == null) return;
        event.setCancelled(true);
        if (gui.getInv().hashCode() == event.getInventory().hashCode()){
            gui.onClick(event);
        } else {
            map.remove(event.getWhoClicked().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClose(InventoryCloseEvent event) {
        if (map.isEmpty()) return;
        GUIAbs gui = map.remove(event.getPlayer().getUniqueId());
        if (gui != null) gui.onClose();
    }

    //    @EventHandler(ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent event) {
        if (map.isEmpty()) return;
        GUIAbs gui = map.get(event.getPlayer().getUniqueId());
        if (gui != null){
            gui.onClose();
            if (gui.hashCode() != event.getInventory().hashCode()){
                map.remove(event.getPlayer().getUniqueId());
                gui.onClose();
            }
        }
    }

    public Map<UUID, GUIAbs> getMap() {
        return map;
    }

    public abstract static class GUIAbs {
        InventoryView inv;

        public GUIAbs(InventoryView i) {
            inv = i;
        }

        public abstract void onClick(InventoryClickEvent event);

        public void onClose() {

        }

        public InventoryView getInv() {
            return inv;
        }
    }
}
