package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;

public class Catpaw extends CustItem_CustModle implements Listener {
    private final static Catpaw a = new Catpaw();

    private Catpaw() {
        super(Material.BOWL,36,"§f喵喵拳套");
        NamespacedKey key = new NamespacedKey(RPGArmour.plugin,"cat_paw");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "AAA",
                "ABA",
                " C "
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',Material.PINK_WOOL);
        r.setIngredient('C',Material.LEATHER);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static Catpaw get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        final Player p = (Player) event.getDamager();
        final PlayerInventory pi = p.getInventory();
        final ItemStack i = pi.getItemInMainHand();
        if (is(i)){
            event.setDamage(0.1D);
//            p.setNoDamageTicks(2);
        }
    }
}


