package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustItem_MultiModel extends CustItem_CustModle {
    private int[] ids;


    public CustItem_MultiModel(Material mat,int id,String displayname,int... ids) {
        super(mat,id,displayname);
        this.ids = ids;
    }

    @Override
    public boolean hasId(int id) {
        for (int i : ids) {
            if (id == i) return true;
        }
        return false;
    }
}
