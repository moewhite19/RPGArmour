package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustItem_RangeOfModel extends CustItem_CustModle {
    private final int idMax;
    private final int idMin;


    public CustItem_RangeOfModel(Material mat,int id,String displayname,int id1,int id2) {
        super(mat,id,displayname);
        this.idMax = Math.max(id1,id2);
        this.idMin = Math.min(id1,id2);
    }


    public int getIdMax() {
        return idMax;
    }

    public int getIdMin() {
        return idMin;
    }

    @Override
    public boolean hasId(int id) {
        return id >= idMin && id <= idMax;
    }
}
