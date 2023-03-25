package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.PlayerAttackMissEvent;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
    private final String sound_put = "rpgarmour:samuraisword.put"; //收刀音效
    private final String sound_att = "minecraft:entity.player.attack.sweep";
    private final String sound_damagebad = "minecraft:entity.player.attack.crit";
    private final int id2 = 23;
    private float damage = 10f;
    private float skillDamage = 18.5f;
    private float spawnChance = 0.05f;
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
        if (is(pi.getItemInMainHand()) || is(pi.getItemInOffHand())){
            setCd(player,15);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 0.5 || !(event.getDamager() instanceof final LivingEntity damager && (event.getEntity() instanceof Mob || event.getEntity() instanceof HumanEntity) && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            return;
        }
        final EntityEquipment equipment = damager.getEquipment();
//        if (pi == null) return;
        ItemStack main = equipment.getItemInMainHand();
        //如果主手是拔出状态的武士刀
        if (isItem(main) == id2){
            if (hasCd(damager)) return;
            //普通攻击
            Location loc = damager.getEyeLocation();
            loc.getWorld().playSound(loc,sound_att,1,1);
            loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK,loc.clone().add(VectorUtils.viewVector(loc).multiply(0.8F)),2);
            setCd(damager,15);
            double damage = event.getDamage() + this.damage;
            if (damager instanceof Mob) damage += 5;
            event.setDamage(damage);
            if (ItemToolUtil.damage(main,1)){
                //武器用坏了
                loc.getWorld().playSound(loc,sound_damagebad,1,1);
                equipment.setItemInMainHand(null);
            }
            return;
        }

        //如果副手是未拔出状态的武士刀
        ItemStack off = equipment.getItemInOffHand();
        if (isItem(off) == getId()){
            if (hasCd(damager)){
                return;
            }
            Location loc = damager.getEyeLocation();
            event.setDamage(event.getDamage() + skillDamage);
            loc.getWorld().playSound(loc,sound_unsheathed2,1,1);
            loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK,loc.clone().add(VectorUtils.viewVector(loc).multiply(0.8F)),3);
            //检查耐久度
            //使用技能时如果主手有物品则掉落
            if (!ItemToolUtil.itemIsAir(main)){
                Item dropItem = loc.getWorld().dropItem(loc,main);
                if (dropItem.isDead()) return;
                dropItem.setVelocity(VectorUtils.viewVector(loc));
                main.setData(null);
            }
            if (ItemToolUtil.damage(off,3)){
                loc.getWorld().playSound(loc,sound_damagebad,1,1);
                equipment.setItemInOffHand(null);
                equipment.setItemInMainHand(null);
                return;
            }
            ItemMeta im = off.getItemMeta();
            im.setCustomModelData(id2);
            off.setItemMeta(im);
            equipment.setItemInMainHand(off);
            equipment.setItemInOffHand(scabbard.createItem());
            setCd(damager,25);
            Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> {
                final ItemStack i = equipment.getItemInMainHand();
                if (scabbard.is(equipment.getItemInOffHand()) && isItem(i) == id2){
                    ItemMeta m = i.getItemMeta();
                    m.setCustomModelData(getId());
                    i.setItemMeta(m);
                    equipment.setItemInOffHand(i);
                    equipment.setItemInMainHand(null);
                    Location l = equipment.getHolder().getLocation();
                    l.getWorld().playSound(l,sound_put,1,1);
                }
            },15);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) return;
        ItemStack cursor = event.getWhoClicked().getItemOnCursor();
        ItemStack current = event.getCurrentItem();
        //收刀
        if (scabbard.is(current) && cursor.getAmount() == 1){
            if (isItem(cursor) != id2) return;
            if (event.getWhoClicked() instanceof Player p){
                ItemMeta im = cursor.getItemMeta();
                if (im == null) return;
                event.setCancelled(true);
                Location loc = p.getLocation();
                p.playSound(loc,sound_put,1F,1F);
                p.setItemOnCursor(null);
                im.setCustomModelData(getId());
                cursor.setItemMeta(im);
                event.setCurrentItem(cursor);

            }
        }
        //拔刀
        else if (ItemToolUtil.itemIsAir(cursor) && isItem(current) == getId()){
            if (event.getWhoClicked() instanceof Player p){
                ItemMeta im = current.getItemMeta();
                if (im == null) return;
                event.setCancelled(true);
                im.setCustomModelData(id2);
                current.setItemMeta(im);
                event.setCurrentItem(scabbard.createItem());
                Location loc = p.getLocation();
                p.playSound(loc,sound_unsheathed,1F,1F);
                p.setItemOnCursor(current);
                im.setCustomModelData(getId());
                current.setItemMeta(im);
            }

        }

    }


    @SuppressWarnings("ConstantConditions")
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
        } else if (isItem(off) == getId() && ItemToolUtil.itemIsAir(main)){ //拔刀
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
    }

    private boolean hasCd(Entity entity) {
        if (entity instanceof Player){
            return ((Player) entity).hasCooldown(getMaterial());
        }
        return false;
    }

    private void setCd(Entity entity,int cd) {
        if (entity instanceof Player player){
            if (player.getCooldown(getMaterial()) < cd){
                player.setCooldown(getMaterial(),cd);
            }
        }
    }

    @Override
    public boolean hasId(int i) {
        return i == getId() | i ==id2;
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

    private int getItemID(ItemStack item) {
        return item.getItemMeta().getCustomModelData();
    }

    private void setItemID(ItemStack item,int id) {
        ItemMeta im = item.getItemMeta();
        im.setCustomModelData(id);
        item.setItemMeta(im);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        //if (entity.fromMobSpawner() || entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)            return;
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
        } else if (entity instanceof Zombie && entity.getType() != EntityType.ZOMBIFIED_PIGLIN){
            if (random.nextDouble() < spawnChance){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.2F);
                ej.setItemInOffHand(item);
                ej.setItemInOffHandDropChance(itemDropChance);
                ej.setItemInMainHand(null);
            }
        }
    }

    static class Scabbard extends CustItem_CustModle {
        public Scabbard() {
            super(Material.BOWL,38,"§9刀鞘");
        }
    }
}
