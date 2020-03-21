package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class QuickFiringCrossbow extends CustItem_CustModle implements Listener {
    private final String arrowTag = "QuickFiringCrossbow";
    private float itemDropChance = 0.05f;
    private float spawnChance = 0.075f;

    public QuickFiringCrossbow() {
        super(Material.CROSSBOW,1,"§9连弩");
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShor(final EntityShootBowEvent event) {
        if (!is(event.getBow())) return;
        final ItemStack bow = event.getBow();
        final ItemMeta im = bow.getItemMeta();
        if (im instanceof CrossbowMeta){
            CrossbowMeta cb = (CrossbowMeta) im;
            final List<ItemStack> items = cb.getChargedProjectiles();
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                if (!is(bow)) return;
                CrossbowMeta ncb = (CrossbowMeta) bow.getItemMeta();
                ncb.setChargedProjectiles(items);
                bow.setItemMeta(ncb);
            });
        }
        final Entity p = event.getProjectile();
        p.addScoreboardTag(arrowTag);
    }
/*    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR){
            final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (is(item)){
                final ItemMeta im = item.getItemMeta();
                if (im instanceof CrossbowMeta){
                    List<ItemStack> items = new ArrayList<>(3);
                    items.add(new ItemStack(Material.ARROW));
                    items.add(new ItemStack(Material.FIREWORK_ROCKET));
                    items.add(new ItemStack(Material.ARROW));
                    ((CrossbowMeta) im).setChargedProjectiles(items);
                    item.setItemMeta(im);
                    event.getPlayer().sendActionBar("填装完成");
                }

            }
        }
    }*/

    @EventHandler
    public void onPickUp(PlayerPickupArrowEvent event) {
        if (event.getArrow().getScoreboardTags().contains(arrowTag)){
            event.getArrow().remove();
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(final EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof Pillager)) return;
        final Pillager entity = (Pillager) event.getEntity();
        final Random random = RandomUtil.getRandom();
        if (random.nextDouble() < spawnChance){
            final EntityEquipment ej = entity.getEquipment();
            if (ej != null){
                final ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.15F);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(itemDropChance);
            }
        }
    }
}
