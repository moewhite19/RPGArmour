package cn.whiteg.rpgArmour.custItems;


import cn.whiteg.mmocore.sound.SingleSound;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.ArmourChangeEvent;
import cn.whiteg.rpgArmour.event.PlayerJumpEvent;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class
BambooDragonfly extends CustItem_CustModle implements Listener {
    public static List<World> disableWorld = new ArrayList<>();
    public final Map<UUID, Staus> staMap = new HashMap<>();
    private final int flyid = 2;
    BukkitTask timer = null;
    SingleSound stopSound = new SingleSound("block.beacon.deactivate",0.4f,1.5f);
    SingleSound startSound = new SingleSound("block.beacon.activate",0.4f,1.5f);

    private float flyspeed = 0.04f;

    public BambooDragonfly() {
        super(Material.SHEARS,1,"§b竹蜻蜓");
        try{
            for (Player player : Bukkit.getOnlinePlayers()) {
                check(player);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            flyspeed = (float) c.getDouble("flySpeed",flyspeed);
        }
    }

    @Override
    public boolean is(ItemStack item) {
        if (item == null || item.getType() != getMaterial() || !item.hasItemMeta()) return false;
        final ItemMeta im = item.getItemMeta();
        if (im == null || !im.hasCustomModelData()) return false;
        return im.getCustomModelData() == getId() || im.getCustomModelData() == flyid;
    }

    public Staus getUserSta(Player player) {
        return staMap.get(player.getUniqueId());
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        final PlayerInventory pi = event.getPlayer().getInventory();
        if (is(pi.getItemInMainHand()) || is(pi.getItemInOffHand())) event.setCancelled(true);
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        if (event.isCancelled() || event.getPlayer().isSneaking()) return;
        Staus sta = getUserSta(event.getPlayer());
        if (sta == null){
            ItemStack hat = event.getPlayer().getInventory().getHelmet();
            if (is(hat)){
                sta = new Staus(event.getPlayer(),hat);
                staMap.put(event.getPlayer().getUniqueId(),sta);
            } else {
                return;
            }
        }
        sta.onUse();
    }

    @SuppressWarnings("all")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Staus sta = getUserSta(event.getPlayer());
        if (sta == null) return;
        if (sta.activate && !event.isFlying()){
            sta.stopfly();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        check(event.getPlayer());
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        Staus sta = staMap.remove(event.getPlayer().getUniqueId());
        if (sta == null) return;
        sta.stopfly();
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event) {
        Staus sta = staMap.remove(event.getEntity().getUniqueId());
        if (sta == null) return;
        sta.stopfly();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Staus sta = getUserSta(event.getPlayer());
        if (sta == null) return;
        ItemStack he = event.getPlayer().getInventory().getChestplate();
        if (he != null){
            if (he.getType() == Material.ELYTRA){
                sta.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWear(ArmourChangeEvent event) {
        if (event.getType() != ArmourChangeEvent.ArmourType.HELMET) return;
        ItemStack item = event.getItem();
        if (event.isWear()){
            if (is(item)){
                addPlayer(event.getPlayer(),item);
            }
        } else {
            Staus sta = staMap.get(event.getPlayer().getUniqueId());
            if (sta == null) return;
            sta.remove();
        }
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta im = item.getItemMeta();
        Objects.requireNonNull(im).setDisplayName("§b竹蜻蜓");
        im.setCustomModelData(getId());
        item.setItemMeta(im);
        return item;
    }

    public void check(Player player) {
        ItemStack item = player.getInventory().getHelmet();
        if (is(item)) addPlayer(player);
    }

    public void addPlayer(Player player,ItemStack itemStack) {
        staMap.put(player.getUniqueId(),new Staus(player,itemStack));
    }

    public void addPlayer(Player player) {
        staMap.put(player.getUniqueId(),new Staus(player));
    }

    public void unreg() {
        Set<Map.Entry<UUID, Staus>> l = staMap.entrySet();
        for (Map.Entry entry : l) {
            Staus sta = (Staus) entry.getValue();
            sta.stopfly();
        }
        staMap.clear();
    }

    public void setTimer() {
        if (timer == null){
            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (staMap.isEmpty()){
                        cancel();
                        timer = null;
                        return;
                    }
                    final Iterator<Map.Entry<UUID, Staus>> it = staMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Staus sta = it.next().getValue();
                        try{
                            if (!sta.hasItem()){
                                sta.stopfly();
                                it.remove();
                                return;
                            }
                            ItemStack hat = sta.getItem();

                            if (sta.activate){
                                if (ItemToolUtil.damage(hat,1)){
                                    sta.stopfly();
                                }

                            } else {
                                if (sta.flag > 1){
                                    sta.flag--;
                                } else {
                                    ItemMeta im = hat.getItemMeta();
                                    Damageable d = (Damageable) im;
                                    if (d.getDamage() > 0){
                                        d.setDamage(d.getDamage() - 1);
                                        hat.setItemMeta(im);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            it.remove();
                        }

                    }
                }

            }.runTaskTimer(RPGArmour.plugin,20,20);
        }
    }


    public class Staus {
        final float defFlyspeed;
        boolean activate = false;
        Player player;
        short flag = 10;
        private ItemStack item;

        Staus(Player player) {
            this.player = player;
            defFlyspeed = player.getFlySpeed();
            item = player.getInventory().getHelmet();
        }

        Staus(Player player,ItemStack item) {
            this.player = player;
            defFlyspeed = player.getFlySpeed();
            this.item = item;
        }

        public boolean hasItem() {
            if (is(item)){
                ItemStack hat = player.getInventory().getHelmet();
                return hat.hashCode() == item.hashCode();
            }
            return false;
        }

        public ItemStack getItem() {
            return item;
        }

        //停止飞行
        public void stopfly() {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFlySpeed(defFlyspeed);
            if (item != null && item.getType() == getMaterial()){
                ItemMeta im = item.getItemMeta();
                if (im != null){
                    im.setCustomModelData(getId());
                    item.setItemMeta(im);
                }
            }
            Location l = player.getLocation();
            if (l.getY() > 256){
                l.setY(255);
                player.teleport(l);
            }
            PlayerInventory pi = player.getInventory();
            ItemStack he = pi.getChestplate();
            if (he != null && he.getType() == Material.ELYTRA){
                pi.setChestplate(null);
                player.getWorld().dropItem(l,he);
            }
            stopSound.playTo(l);
            activate = false;
            flag = 10;
        }

        public void onUse() {
            if (player.getAllowFlight()) return;
            if (!player.hasPermission("rpgarmour.use.bamboodragonfly")) return;

            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA){
                return;
            }

            if (!hasItem()) return;
            ItemMeta im = item.getItemMeta();
            if (im instanceof Damageable){
                final Damageable dm = ((Damageable) im);
                if (dm.hasDamage() && dm.getDamage() >= getMaterial().getMaxDurability()){
                    return;
                }
            }

            im.setCustomModelData(flyid);
            item.setItemMeta(im);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(flyspeed);
            Location l = player.getLocation();
            startSound.playTo(l);
            activate = true;
            setTimer();
        }

        public void remove() {
            if (activate) stopfly();
            staMap.remove(player.getUniqueId());
        }


    }
}

