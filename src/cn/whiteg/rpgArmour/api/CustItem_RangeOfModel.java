package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustItem_RangeOfModel extends CustItem {
    private final int id;
    private final int idMax;
    private final int idMin;


    public CustItem_RangeOfModel(Material mat,int id,String displayname,int id1,int id2) {
        super(mat,displayname);
        this.id = id;
        this.idMax = Math.max(id1,id2);
        this.idMin = Math.min(id1,id2);
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
        final int customModelData = im.getCustomModelData();
        return customModelData >= idMin && customModelData <= idMax;
    }

    public int getId() {
        return id;
    }

    public int getIdMax() {
        return idMax;
    }

    public int getIdMin() {
        return idMin;
    }
}
