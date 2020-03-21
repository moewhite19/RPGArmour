package cn.whiteg.rpgArmour.custItems;


import cn.whiteg.moeInfo.nms.ActionBar;
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
import org.bukkit.boss.BossBar;
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
    public static List<World> worldList = new ArrayList<>();
    //   private short power = 0;
    //  private BukkitTask tick;
    // List<String> lores;
    public final Map<UUID, Staus> staMap = new HashMap<>();
    private final int flyid = 2;
    BukkitTask timer = null;
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
        if (!im.hasCustomModelData()) return false;
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
        if (sta == null) return;
        if (event.getPlayer().getAllowFlight()) return;
        if (!event.getPlayer().hasPermission("rpgarmour.use.bamboodragonfly")) return;
        final ItemStack he = event.getPlayer().getInventory().getChestplate();
        if (he != null && he.getType() == Material.ELYTRA){
            sta.remove();
            return;
        }
        final ItemStack item = sta.getItem();
        if (item == null) return;
        final ItemMeta im = item.getItemMeta();
        if (im instanceof Damageable){
            final Damageable dm = ((Damageable) im);
            if (dm.hasDamage() && dm.getDamage() >= getMaterial().getMaxDurability()){
                return;
            }
        }
        im.setCustomModelData(flyid);
        item.setItemMeta(im);
        sta.player.setAllowFlight(true);
        sta.player.setFlying(true);
        sta.player.setFlySpeed(flyspeed);
        //sta.upinv();
        Location l = sta.player.getLocation();
        l.getWorld().playSound(l,"block.beacon.activate",0.4f,1.5f);
        sta.activate = true;
        setTimer();
/*        tick = new BukkitRunnable() {
            @Override
            public void run() {
                power--;
                if (!player.isFlying()){
                    cancel();
                    return;
                }
                if (power < 2){
                    CustItem.setDurability((short) 27);
                    getPlayer().setFlying(false);
                    getPlayer().setAllowFlight(false);
                    ActionBar.sendActionBar(player,"竹蜻蜓电池耗尽");
                    upLore();
                    return;
                }
                ActionBar.sendActionBar(player,"竹蜻蜓: " + power);
            }
        }.runTaskTimerAsynchronously(RPGArmour.plugin,1,20)*/
        ;
    }

    @SuppressWarnings("all")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Staus sta = getUserSta(event.getPlayer());
        if (sta == null) return;
        if (sta.activate && !event.isFlying()){
            sta.stopfly();
            //     if (tick != null) tick.cancel();
            //   upLore();
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
                addPlayer(event.getPlayer());
                return;
            }
        } else {
            Staus sta = staMap.get(event.getPlayer().getUniqueId());
            if (sta == null) return;
            sta.remove();
        }
    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onInvClick(InventoryClickEvent event) {
//        if (event.getClickedInventory().getType() != InventoryType.PLAYER || event.getSlot() != 39) return;
//        Staus sta = staMap.get(event.getWhoClicked().getUniqueId());
//        if (sta == null) check((Player) event.getWhoClicked());
//        if (event.getHotbarButton() != -1){
//            if (event.getRawSlot() != 5) return;
//            sta.remove();
//            return;
//        } else if (event.getSlot() == 39 && event.getClick() != ClickType.MIDDLE){
//            sta.remove();
//        }
//        //setItem(event.getCurrentItem());
//    }

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
                        final Staus sta = it.next().getValue();
                        try{
                            final ItemStack hat = sta.getItem();
                            if (hat == null){
                                sta.stopfly();
                                it.remove();
                                return;
                            }
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
//        final BossBar bossBar;

        Staus(Player player) {
            this.player = player;
            defFlyspeed = player.getFlySpeed();
//            bossBar = Bukkit.createBossBar()
        }

        Staus(Player player,ItemStack ite) {
            this(player);
            item = ite;
        }

        public ItemStack getItem() {
            if (is(item)){
                return item;
            }
            item = player.getInventory().getHelmet();
            if (is(item)){
                return item;
            }
            return null;
        }

        public void setItem(ItemStack itemStack) {
            if (itemStack == null) return;
            this.item = itemStack;
//            nmsItem = CraftItemStack.asNMSCopy(itemStack);
//            modifiers = (nmsItem.hasTag()) ? nmsItem.getTag() : new TagCompound();
        }

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
            l.getWorld().playSound(l,"block.beacon.deactivate",0.4f,1.5f);
//            ActionBar.sendActionBar(player,"停止飞行");
            activate = false;
            flag = 10;
        }

        public void remove() {
            if (activate) stopfly();
            staMap.remove(player.getUniqueId());
        }


    }
}

