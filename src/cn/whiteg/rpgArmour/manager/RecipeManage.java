package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeManage {
    final RPGArmour pligin;
    final Map<NamespacedKey, Recipe> map = new HashMap<>();
    boolean sync = false;

    public RecipeManage(RPGArmour pligin) {
        this.pligin = pligin;
    }

    public Map<NamespacedKey, Recipe> getMap() {
        return map;
    }

    public void addRecipe(NamespacedKey name,Recipe recipe) {
        try{
            map.put(name,recipe);
            if (sync) Bukkit.getServer().addRecipe(recipe);
        }catch (Exception e){
            if (Setting.DEBUG){
                RPGArmour.logger.info("合成表添加失败: " + name.getKey());
                e.printStackTrace();
            }
        }
    }

    public boolean giveRecipe(Player p,Recipe r) {
        if (r instanceof ShapedRecipe shapedRecipe){
            return p.discoverRecipe(shapedRecipe.getKey());
        }
        return false;
    }

    public GUIManager.GUIAbs getRecipeInv(Player p,String name) {
        Recipe r = map.get(name);
        if (r != null){
            if (r instanceof ShapedRecipe sr){
                InventoryView inv = p.openWorkbench(null,true);
                Map<Character, RecipeChoice> m = sr.getChoiceMap();
                String[] sheape = sr.getShape();
                ItemStack[][] base = new ItemStack[3][];
                for (int l = 0; l < sheape.length; l++) {
                    if (l > 3) break;
                    ItemStack[] il = new ItemStack[3];
                    char[] chars = sheape[l].toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        if (i > 3) break;
                        RecipeChoice c = m.get(chars[l]);
                        if (c != null){
                            il[i] = c.getItemStack();
                        } else {
                            il[i] = null;
                        }
                    }
                    base[l] = il;
                }
                int loc = 1;
                for (ItemStack[] litem : base) {
                    for (int loi = 0; loi < litem.length; loi++) {
                        ItemStack item = litem[loi];
                        if (item != null){
                            inv.setItem(loc + loi,item);
                        }
                    }
                    loc += 3;
                }
                inv.setItem(0,sr.getResult());
                GUIManager.GUIAbs guiAbs = new GUIManager.GUIAbs(inv) {
                    @Override
                    public void onClick(InventoryClickEvent event) {

                    }
                };
//                pligin.getGuiManager().openGui(p,guiAbs);
                return guiAbs;
            }
        }
        return null;
    }

    //同步更新配方
    public void onSync() {
        map.forEach((namespacedKey,recipe) -> {
            try{
                Bukkit.getServer().addRecipe(recipe);
            }catch (Exception ignored){
            }
        });
        sync = true;
    }

    public void unload() {
        if (map.isEmpty()) return;
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            if (map.containsValue(it.next())){
                it.remove();
            }
        }
    }
}
