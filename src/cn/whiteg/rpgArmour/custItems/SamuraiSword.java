package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.PlayerAttackMissEvent;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;


public class SamuraiSword extends CustItem_CustModle implements Listener {
    static private final SamuraiSword a = new SamuraiSword();
    final Scabbard scabbard = new Scabbard();
    private final String sound_unsheathed = "rpgarmour:samuraisword.unsheathed";
    private final String sound_unsheathed2 = "rpgarmour:samuraisword.unsheathed2";
    private final String sound_put = "rpgarmour:samuraisword.put";
    private final String sound_att = "minecraft:entity.player.attack.sweep";
    private final String sound_damagebad = "minecraft:entity.player.attack.crit";
    private float damage = 10f;
    private float skillDamage = 18.5f;
    private float spawnChance = 0.05f;
    private int id2 = 23;
    private float itemDropChance = 0.03f;

    private SamuraiSword() {
        super(Material.SHEARS,22,"§9武士刀");
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
            damage = (float) c.getDouble("damage",damage);
            skillDamage = (float) c.getDouble("skillDamage",skillDamage);
        }
//        NamespacedKey key = new NamespacedKey(RPGArmour.plugin,"samuraisword");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                "C A",
//                " A ",
//                "B C"
//        );
//        r.setIngredient('A',Material.IRON_INGOT);
//        r.setIngredient('B',Material.STICK);
//        r.setIngredient('C',Material.LEATHER);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static SamuraiSword get() {
        return a;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttMiss(PlayerAttackMissEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory pi = player.getInventory();
        ItemStack item = pi.getItemInMainHand();
        if (isAir(item)){
            item = pi.getItemInOffHand();
            if (is(item)){
                setCd(player,15);
            }
        } else if (is(item)){
            setCd(player,15);
        }
    }

    //
//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onInvClick(InventoryClickEvent event) {
//        ItemStack item = event.getCurrentItem();
//        if (item != null && item.getType() == getMaterial() && ItemSwitch(item,id2,getId())){
//            Location loc = event.getWhoClicked().getLocation();
//            loc.getWorld().playSound(loc,sound_put,1,1);
//        }
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 0.5 || !(event.getDamager() instanceof LivingEntity && (event.getEntity() instanceof Mob || event.getEntity() instanceof HumanEntity) && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK))
            return;
//        if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof Creature) || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
        final LivingEntity livingEntity = (LivingEntity) event.getDamager();
        if (event.isCancelled()) return;
        final EntityEquipment pi = livingEntity.getEquipment();
        ItemStack item = pi.getItemInMainHand();
        //使用技能.玩家必须空着主手 ， 其他实体直接覆盖主手w
        if (isAir(item) || !(event.getDamager() instanceof Player)){
            item = pi.getItemInOffHand();
            if (isItem(item) == getId()){
                if (hasCd(livingEntity)){
//                    event.setCancelled(true);
                    return;
                }
                Location loc = livingEntity.getLocation();
                event.setDamage(event.getDamage() + skillDamage);
                if (ItemToolUtil.damage(item,3)){
                    loc = livingEntity.getLocation();
                    loc.getWorld().playSound(loc,sound_damagebad,1,1);
                    pi.setItemInOffHand(null);
                    return;
                }
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(id2);
                item.setItemMeta(im);
                pi.setItemInMainHand(item);
                loc.getWorld().playSound(loc,sound_unsheathed2,1,1);
                pi.setItemInOffHand(scabbard.createItem());
                setCd(livingEntity,25);
                Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> {
                    final ItemStack i = pi.getItemInMainHand();
                    if (scabbard.is(pi.getItemInOffHand()) && isItem(i) == id2){
                        ItemMeta m = i.getItemMeta();
                        m.setCustomModelData(getId());
                        i.setItemMeta(m);
                        pi.setItemInOffHand(i);
                        pi.setItemInMainHand(null);
                        Location l = pi.getHolder().getLocation();
                        l.getWorld().playSound(l,sound_put,1,1);
                    }
                    if (is(i)){
                    }
                },15);
            }
            return;
        }
        if (isItem(item) == id2){
            if (hasCd(livingEntity)) return;
            Location loc = event.getEntity().getLocation();
            loc.getWorld().playSound(loc,sound_att,1,1);
            setCd(livingEntity,15);
            double damage = event.getDamage() + this.damage;
            if (livingEntity instanceof Mob) damage += 5;
            event.setDamage(damage);
            if (ItemToolUtil.damage(item,1)){
                loc = livingEntity.getLocation();
                loc.getWorld().playSound(loc,sound_damagebad,1,1);
                pi.setItemInMainHand(null);
            }
            return;
        }
    }

/*    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onItemr(PlayerrItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == getMaterial() && ItemSwitch(item,id2,getId())){
            Location loc = event.getPlayer().getLocation();
            loc.getWorld().playSound(loc,sound_put,1,1);
            event.setCancelled(true);
        }
    }*/

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onHat(ArmourChangeEvent event) {
//        if (event.getType() != ArmourChangeEvent.ArmourType.HELMET || !event.isWear()) return;
//        ItemStack item = event.getItem();
//        if (is(item)){
//            if (ItemSwitch(item,getId(),id2)){
//                Location loc = event.getPlayer().getLocation();
//                loc.getWorld().playSound(loc,sound_unsheathed,1,1);
//            }
//        }
//    }

    /*
        @EventHandler(priority = EventPriority.HIGH)
        public void onUse(PlayerInteractEvent event) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
            if (event.getHand() == EquipmentSlot.HAND){
                ItemStack item = event.getItem();
                if (this.is(item)){
                    ItemMeta im = item.getItemMeta();
                    int i = im.getCustomModelData();
                    if (i == getId()){
                        if (event.getPlayer().hasCooldown(getMaterial())) return;
                        im.setCustomModelData(id2);
                        item.setItemMeta(im);
                        Location loc = event.getPlayer().getLocation();
                        loc.getWorld().playSound(loc,sound_unsheathed,1,1);
                    } else {
                        im.setCustomModelData(getId());
                        item.setItemMeta(im);
                        Location loc = event.getPlayer().getLocation();
                        loc.getWorld().playSound(loc,sound_put,1,1);
                        setCd(event.getPlayer(),10);
                    }
                }
            } else if (event.getHand() == EquipmentSlot.OFF_HAND){
                ItemStack item = event.getItem();
                if (this.is(item)){
                    PlayerInventory pi = event.getPlayer().getInventory();
                    if (pi.getHolder().getGameMode() != GameMode.SURVIVAL) return;
                    ItemStack hand = pi.getItemInMainHand();
                    if (isAir(hand)){
                        if (event.getPlayer().hasCooldown(getMaterial())) return;
                        ItemMeta im = item.getItemMeta();
                        int i = im.getCustomModelData();
                        if (i == getId()){
                            im.setCustomModelData(id2);
                            item.setItemMeta(im);
                            Location loc = event.getPlayer().getLocation();
                            loc.getWorld().playSound(loc,sound_unsheathed,1,1);
                            pi.setItemInOffHand(null);
                            pi.setItemInMainHand(item);
                        }
                    }

                }
            }
        }
    */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) return;
        ItemStack cursor = event.getWhoClicked().getItemOnCursor();
        ItemStack current = event.getCurrentItem();
        //收刀
        if (scabbard.is(current) && cursor.getAmount() == 1){
            if (isItem(cursor) != id2) return;
            if (event.getWhoClicked() instanceof Player){
                ItemMeta im = cursor.getItemMeta();
                if (im == null) return;
                event.setCancelled(true);
                Player p = (Player) event.getWhoClicked();
                Location loc = p.getLocation();
                p.playSound(loc,sound_put,1F,1F);
                p.setItemOnCursor(null);
                im.setCustomModelData(getId());
                cursor.setItemMeta(im);
                event.setCurrentItem(cursor);

            }
        } else if (isAir(cursor) && isItem(current) == getId()){//拔刀
            if (event.getWhoClicked() instanceof Player){
                ItemMeta im = current.getItemMeta();
                if (im == null) return;
                event.setCancelled(true);
                im.setCustomModelData(id2);
                current.setItemMeta(im);
                event.setCurrentItem(scabbard.createItem());
                Player p = (Player) event.getWhoClicked();
                Location loc = p.getLocation();
                p.playSound(loc,sound_unsheathed,1F,1F);
                p.setItemOnCursor(current);
                im.setCustomModelData(getId());
                current.setItemMeta(im);
            }

        }

    }


    @EventHandler(ignoreCancelled = true)
    public void onSwapItem(PlayerSwapHandItemsEvent event) {
        ItemStack main = event.getOffHandItem();
        ItemStack off = event.getMainHandItem();
        if (isItem(main) == id2 && scabbard.is(off)){            //收刀
            if (off.getAmount() > 1) return;
            if (event.getPlayer().hasCooldown(getMaterial())){
                event.setCancelled(true);
                return;
            }
            final ItemMeta im = main.getItemMeta();
            im.setCustomModelData(getId());
            main.setItemMeta(im);
            Location loc = event.getPlayer().getLocation();
            loc.getWorld().playSound(loc,sound_put,1,1);
            event.setOffHandItem(main);
            event.setMainHandItem(new ItemStack(Material.AIR));
        } else if (isItem(off) == getId() && isAir(main)){ //拔刀
            if (event.getPlayer().hasCooldown(getMaterial())){
                event.setCancelled(true);
                return;
            }
            final ItemMeta im = off.getItemMeta();
            im.setCustomModelData(id2);
            off.setItemMeta(im);
            main.setItemMeta(im);
            Location loc = event.getPlayer().getLocation();
            loc.getWorld().playSound(loc,sound_unsheathed,1,1);
            event.setMainHandItem(off);
            event.setOffHandItem(scabbard.createItem());
            setCd(event.getPlayer(),10);
        }
//        if (isAir(off) && is(main)){
//            if (event.getPlayer().hasCooldown(getMaterial())){
//                event.setCancelled(true);
//                return;
//            }
//            if (ItemSwitch(main,getId(),id2)){
//                Location loc = event.getPlayer().getLocation();
//                loc.getWorld().playSound(loc,sound_unsheathed,1,1);
//            }
//        } else if (isAir(main) && is(off)){
//            if (event.getPlayer().hasCooldown(getMaterial())){
//                event.setCancelled(true);
//                return;
//            }
//            if (ItemSwitch(off,id2,getId())){
//                Location loc = event.getPlayer().getLocation();
//                loc.getWorld().playSound(loc,sound_put,1,1);
//                PlayerInventory pi = event.getPlayer().getInventory();
//                pi.setItemInMainHand(off);
//                setCd(event.getPlayer(),10);
//                event.setCancelled(true);
//            } else {
//                setCd(event.getPlayer(),15);
//            }
//        }
    }

/*
    @EventHandler(ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {
        PlayerInventory pi = event.getPlayer().getInventory();
        ItemStack main = pi.getItemInMainHand();
        if (is(main)){
            if (ItemSwitch(main,id2,getId())){
                Location loc = event.getPlayer().getLocation();
                loc.getWorld().playSound(loc,sound_put,1,1);
            }
        }
    }
*/

    private boolean isAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private boolean hasCd(Entity entity) {
        if (entity instanceof Player){
            return ((Player) entity).hasCooldown(getMaterial());
        }
        return false;
    }

    private boolean setCd(Entity entity,int cd) {
        if (entity instanceof Player){
            Player player = (Player) entity;
            if (player.getCooldown(getMaterial()) < cd){
                player.setCooldown(getMaterial(),cd);
                return true;
            }
        }
        return false;
    }
//    private boolean hasCd(Entity player) {
//        if (cdMap.containsKey(player.getName())){
//            long now = System.currentTimeMillis();
//            return cdMap.get(player.getName()) < now;
//        }
//        return true;
//    }
//
//    @SuppressWarnings("all")
//    private void setCd(Entity player) {
//        cdMap.put(new String(player.getName()),System.currentTimeMillis() + 500);
//    }

//    private double attEntity(Entity atter,Entity atted,ItemStack item) {
//        if (!hasCd(atter)) return 0;
//        setCd(atter);
//        return 12;
//    }


    @SuppressWarnings("all")
    @Override
    public boolean is(ItemStack item) {
        if (item == null || item.getType() != getMaterial() || !item.hasItemMeta()) return false;
        ItemMeta im = item.getItemMeta();
        if (im.hasCustomModelData()){
            int i = im.getCustomModelData();
            if (i == getId() || i == id2) return true;
        }
        return false;
    }

    public int isItem(ItemStack item) {
        if (item == null || item.getType() != getMaterial() || !item.hasItemMeta()) return 0;
        ItemMeta im = item.getItemMeta();
        if (im.hasCustomModelData()){
            int i = im.getCustomModelData();
            if (i == getId() || i == id2) return i;
        }
        return 0;
    }

    @SuppressWarnings("all")
    private int getItemID(ItemStack item) {
        return item.getItemMeta().getCustomModelData();
    }

    @SuppressWarnings("all")
    private void setItemID(ItemStack item,int id) {
        ItemMeta im = item.getItemMeta();
        im.setCustomModelData(id);
        item.setItemMeta(im);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if(entity.fromMobSpawner() || entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        EntityEquipment ej = entity.getEquipment();
        Random random = RandomUtil.getRandom();
        if (ej == null) return;
        if (entity.getType() == EntityType.VINDICATOR){
            if (random.nextDouble() < 0.3){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.2F);
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(id2);
                item.setItemMeta(im);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(0.03F);
            }
        } else if (entity instanceof Zombie && entity.getType() != EntityType.PIG_ZOMBIE){
            if (random.nextDouble() < spawnChance){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.2F);
                ej.setItemInOffHand(item);
                ej.setItemInOffHandDropChance(itemDropChance);
                ej.setItemInMainHand(null);
            }
        }
    }

    class Scabbard extends CustItem_CustModle {
        public Scabbard() {
            super(Material.BOWL,38,"§9刀鞘");
        }
    }
}
