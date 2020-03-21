package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public abstract class CustItem {

    private final Material mat;
    private final String displayname;
    @SuppressWarnings("unchecked")
    private List<String> lore = Collections.EMPTY_LIST;

    public CustItem(final Material mat,final String displayname) {
        this.mat = mat;
        this.displayname = displayname;
    }

//    public final String naem_visible;
//        public CustItem(Material mat,int id,String naem_visible) {
//            this.mat = mat;
//            this.id = id;
//            this.naem_visible = naem_visible;
//            this.lores = null;
//            this.lore = null;
//        }
//
//        public CustItem(Material mat,int id,String naem_visible,List<String> lores) {
//            this.mat = mat;
//            this.id = id;

//            this.naem_visible = naem_visible;
//            this.lores = lores;
//            this.lore = null;
//        }
//
//        public CustItem(Material mat,String naem_visible,List<String> lores,int loreinx) {
//            this.mat = mat;
//            this.naem_visible = naem_visible;
//            this.lores = lores;
//            this.lore = lores.get(loreinx);
//        }

//        public ItemStack createItem() {
//            ItemStack item = new ItemStack(mat);
//            ItemMeta im = item.getItemMeta();
//            if (im != null){
//                if (naem_visible != null)
//                    im.setDisplayName(naem_visible);
//                if (lores != null){
//                    im.setLore(lores);
//                }
//                if (id != 0){
//                    im.setCustomModelData(id);
//                }
//                item.setItemMeta(im);
//            }
//            return item;
//        }

    /**
     * 消耗物品耐久度
     *
     * @param inv  玩家背包
     * @param slot 使用栏位
     * @param item 物品
     */
    public static void damageItem(PlayerInventory inv,EquipmentSlot slot,ItemStack item,ItemMeta meta) {
        if (meta == null) meta = item.getItemMeta();
        if (meta instanceof Damageable damageable){
            if (damageable.getDamage() < item.getType().getMaxDurability()){
                damageable.setDamage(damageable.getDamage() + 1);
                item.setItemMeta(meta);
            } else {
                useItem(inv,slot,item);
            }
        }
    }

    /**
     * 消耗物品
     *
     * @param inv  玩家背包
     * @param slot 使用栏位
     * @param item 物品
     */
    public static void useItem(PlayerInventory inv,EquipmentSlot slot,ItemStack item) {
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount() - 1);
        } else inv.setItem(slot,null);
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public String getDisplayName() {
        return displayname;
    }

    public Material getMaterial() {
        return mat;
    }

    public abstract ItemStack createItem();

    public ItemStack createItem(List<String> arg) {
        ItemStack item = createItem();
        if (arg != null && !arg.isEmpty()){
            try{
                item.setAmount(Integer.parseInt(arg.get(0)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return item;
    }

    public abstract boolean is(ItemStack item);
}
