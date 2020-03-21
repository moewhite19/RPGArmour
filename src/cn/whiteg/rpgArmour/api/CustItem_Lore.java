package cn.whiteg.rpgArmour.api;

import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustItem_Lore extends CustItem {
    private final int loreinx;
//    private final Material mat;
//    private final String naem_visible;
//    private final List<String> lores;


    public CustItem_Lore(Material mat,String displayname,List<String> lore,int loreinx) {
        super(mat,displayname);
        this.loreinx = loreinx;
        setLore(lore);
//        this.mat = mat;
//        this.naem_visible = naem_visible;r
//        this.lores = lores;
//        this.loreinx = lores.get(loreinx);

    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta im = item.getItemMeta();
        if (im != null){
            im.setDisplayName(getDisplayName());
            im.setLore(this.getLore());
            item.setItemMeta(im);
        }
        return item;
    }

    @Override
    public boolean is(ItemStack item) {
        if (item == null || item.getType() != getMaterial()) return false;
        if (!item.hasItemMeta()) return false;
        return ItemToolUtil.hasLore(item,getLoreStr());
    }

    public String getLoreStr() {
        return this.getLore().get(loreinx);
    }
}
