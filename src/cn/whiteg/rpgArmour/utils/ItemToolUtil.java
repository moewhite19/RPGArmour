package cn.whiteg.rpgArmour.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemToolUtil {
    public static boolean fixTool(ItemStack item) {
        if (item == null){
            return false;
        }
        final ItemMeta im = item.getItemMeta();
        if (im instanceof Damageable){
            if (im.isUnbreakable() || ((Damageable) im).getDamage() < 1){
                return false;
            } else {
                ((Damageable) im).setDamage(0);
                item.setItemMeta(im);
                return true;
            }
        }
        return false;
    }

    public static boolean hasLore(ItemStack che,String lor) {
        if (che != null){
            final ItemMeta im = che.getItemMeta();
            if (im == null) return false;
            final List<String> ls = im.getLore();
            if (ls == null || ls.isEmpty()) return false;
            for (String str : ls) {
                if (str.equals(lor)) return true;
            }
        }
        return false;
    }

    public static boolean damage(ItemStack item,int dam) {
        return damageRest(item,dam) <= 0;
    }

    public static int damageRest(ItemStack item,int dam) {
        Damageable im = (Damageable) item.getItemMeta();
        if (!((ItemMeta) im).isUnbreakable()){
            int nd = im.getDamage() + dam;
            im.setDamage(nd);
            item.setItemMeta((ItemMeta) im);
        }
        return item.getType().getMaxDurability() - im.getDamage();
    }

    public static void copyEnchat(@Nullable ItemStack item,ItemStack item2) {
        if (item != null){
            Map<Enchantment, Integer> ed = item.getEnchantments();
            if (!ed.isEmpty()){
                for (final Map.Entry<Enchantment, Integer> entry : ed.entrySet()) {
                    try{
                        item2.addEnchantment(entry.getKey(),entry.getValue());
                    }catch (Exception ignored){
                    }
                }
            }
        }
    }

//    public static ItemStack lootDamageItem(ItemStack item,float damage,int min) {
//        lootDamageItem(item,damage);
//        Damageable im = (Damageable) item.getItemMeta();
//        if (im.getDamage() < min)
//    }

    public static ItemStack lootDamageItem(ItemStack item,float damage) {
        Damageable im = (Damageable) item.getItemMeta();
        if (im == null) return item;
        final short max = item.getType().getMaxDurability();
        final float f = max * damage;
        double r = f * Math.random();
        im.setDamage((int) (max - r));
        item.setItemMeta((ItemMeta) im);
        return item;
    }
}
