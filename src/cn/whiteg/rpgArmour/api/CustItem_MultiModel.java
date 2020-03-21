package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustItem_MultiModel extends CustItem {
    private final int id;
    private int[] ids;


    public CustItem_MultiModel(Material mat,int id,String displayname,int... ids) {
        super(mat,displayname);
        this.id = id;
        this.ids = ids;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta im = item.getItemMeta();
        if (im != null){
            im.setDisplayName(getDisplayName());
            if (getLore() != null){
                im.setLore(getLore());
            }
            im.setCustomModelData(id);
            item.setItemMeta(im);
        }
        return item;
    }

    @Override
    public boolean is(ItemStack item) {
        if (item == null || item.getType() != getMaterial() || !item.hasItemMeta()) return false;
        ItemMeta im = item.getItemMeta();
        if (!im.hasCustomModelData()) return false;
        final int model = im.getCustomModelData();
        for (int i : ids) {
            if (model == i) return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public int[] getIds() {
        return ids;
    }
}
