package cn.whiteg.rpgArmour.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class CustItem {

    private final Material mat;
    private final String displayname;
    private final List<String> lores;

    public CustItem(final Material mat,final String displayname,final List<String> lores) {
        this.mat = mat;
        this.displayname = displayname;
        this.lores = lores;
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


    public List<String> getLores() {
        return lores;
    }

    public String getDisplayName() {
        return displayname;
    }

    public Material getMaterial() {
        return mat;
    }

    public abstract ItemStack createItem();

    public abstract boolean is(ItemStack item);

}
