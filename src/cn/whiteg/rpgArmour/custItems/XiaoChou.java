package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;

public class XiaoChou extends CustItem_CustModle {
    private static final XiaoChou a;

    static {
        a = new XiaoChou();
    }

    private XiaoChou() {
        super(Material.SHEARS,10,"§e小丑");
        //RPGArmour.plugin.regEven(this);
    }

    public static XiaoChou get() {
        return a;
    }
}


