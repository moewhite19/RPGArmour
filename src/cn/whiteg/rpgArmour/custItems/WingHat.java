package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class WingHat extends CustItem_CustModle {
    private static final WingHat a = new WingHat();

    private WingHat() {
        super(Material.SHEARS,21,"§f翅膀头饰");
        NamespacedKey key = new NamespacedKey(RPGArmour.plugin,"wing_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "ABA",
                "ABA",
                "B B"
        );
        r.setIngredient('A',Material.FEATHER);
        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static WingHat get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,new AttributeModifier(UUID.randomUUID(),getDisplayName(),0.01,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        item.setItemMeta(im);
        return item;
    }
}
