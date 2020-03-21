package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.custItems.*;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustItemManager {
    private Map<String, CustItem> items = new HashMap<>();

    public CustItemManager() {
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            regItem(new BambooDragonfly());
            regItem(new ResurrectArmor());
            regItem(new RepairCrystal());
            regItem(new RoastFish());
            regItem(SamuraiSword.get());
            regItem(new Muramasa());
            regItem(new EnderHat());
            regItem(new EnderBow());
            regItem(WingHat.get());
            regItem(new SeekerBow());
            regItem(new QiubtBow());
            regItem(new duck());
            regItem(XiaoChou.get());
            regItem(new Hook());
            regItem(new ZonZI());
            regItem(new Van());
            regItem(new ddBow());
            regItem(new PlayerHatCopyer());
            regItem(Catpaw.get());
            regItem(new Fan());
            regItem(new PulseGrenade());
            regItem(new BindingKiller());
            regItem(new BeeEgg());
//        addRecipe(new QuickFiringCrossbow());
        });
    }


    public CustItem unregItem(CustItem ca) {
        Iterator<Map.Entry<String, CustItem>> it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CustItem> e = it.next();
            if (ca == e.getValue()){
                it.remove();
                if (ca instanceof Listener){
                    RPGArmour.plugin.unregListener((Listener) ca);
                }
                return ca;
            }
        }
        return null;
    }

    public CustItem unregItem(Class<? extends CustItem> cs) {
        CustItem ca = items.remove(cs.getName());
        if (ca == null) return null;
        if (ca instanceof Listener){
            RPGArmour.plugin.unregListener((Listener) ca);
        }
        return ca;
    }

    public void regItem(String name,CustItem ca) {
        items.put(name,ca);
        if (ca instanceof Listener){
            RPGArmour.plugin.regListener((Listener) ca);
        }
    }

    public void regItem(CustItem ca) {
        regItem(ca.getClass().getName(),ca);
    }

    public ItemStack createItem(@NotNull String name) {
        CustItem ca = getCustItem(name);
        if (ca != null) return ca.createItem();
        return null;
    }


    public CustItem getCustItem(@NotNull String name) {
        CustItem custItem = items.get(name);
        if (custItem == null){
            for (Map.Entry<String, CustItem> entry : items.entrySet()) {
                if (ChatColor.stripColor(entry.getValue().getDisplayName()).equals(name)){
                    return entry.getValue();
                }
            }
        }
        return custItem;
    }

    public CustItem getCustItem(Class cl) {
        return items.get(cl.getName());
    }

    public Set<String> getCanCraftItems() {
        return items.keySet();
    }

    public List<String> getItemNames() {
        List<String> list = new ArrayList<>(items.size());
        for (Map.Entry<String, CustItem> entry : items.entrySet()) {
            list.add(ChatColor.stripColor(entry.getValue().getDisplayName()));
        }
        return list;
    }
}
