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


    public CustItem_Lore(Material mat,String displayname,List<String> lores,int loreinx) {
        super(mat,displayname,lores);
        this.loreinx = loreinx;
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
            im.setLore(getLores());
            item.setItemMeta(im);
        }
        return item;
    }

    @Override
    public boolean is(ItemStack item) {
        if (item == null || item.getType() != getMaterial()) return false;
        if (!item.hasItemMeta()) return false;
        return ItemToolUtil.hasLore(item,getLore());
    }

    public String getLore() {
        return getLores().get(loreinx);
    }
}
