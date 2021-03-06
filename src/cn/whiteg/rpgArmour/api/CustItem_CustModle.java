package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustItem_CustModle extends CustItem {
    private final int id;


    public CustItem_CustModle(Material mat,int id,String displayname) {
        super(mat,displayname,null);
        this.id = id;
    }

    public CustItem_CustModle(Material mat,int id,String displayname,List<String> lores) {
        super(mat,displayname,lores);
        this.id = id;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta im = item.getItemMeta();
        if (im != null){
            im.setDisplayName(getDisplayName());
            if (getLores() != null){
                im.setLore(getLores());
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
        return im.getCustomModelData() == id;

    }

    public int getId() {
        return id;
    }
}
