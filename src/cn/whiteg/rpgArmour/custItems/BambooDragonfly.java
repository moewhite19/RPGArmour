package cn.whiteg.rpgArmour.custItems;


import cn.whiteg.chanlang.LangUtils;
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
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

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
    public boolean hasId(int id) {
        return id == getId() || id == flyid;
    }

    public Staus getUserSta(Player player) {
        return staMap.get(player.getUniqueId());
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        final PlayerInventory pi = event.getPlayer().getInventory();
        if (is(pi.getItemInMainHand()) || is(pi.getItemInOffHand())) event.setCancelled(true);
    }

    //限制使用竹蜻蜓时使用重锤
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player){
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand.getType() == Material.MACE && player.hasCooldown(Material.MACE)){
                event.setCancelled(true);
            }
        }
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
            sta.stopFly();
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
        sta.stopFly();
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event) {
        Staus sta = staMap.remove(event.getEntity().getUniqueId());
        if (sta == null) return;
        sta.stopFly();
    }

//    @EventHandler(ignoreCancelled = true)
//    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
//        Staus sta = getUserSta(event.getPlayer());
//        if (sta == null) return;
//        ItemStack he = event.getPlayer().getInventory().getChestplate();
//        if (he != null){
//            if (he.getType() == Material.ELYTRA){
//                sta.remove();
//            }
//        }
//    }

    //在进入载具后关闭竹蜻蜓
    @EventHandler(ignoreCancelled = true)
    public void onEnter(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player){
            final Staus sta = staMap.get(player.getUniqueId());
            if (sta != null && sta.activate){
                sta.stopFly();
            }
        }
    }

    //使用激流时关闭竹蜻蜓
    @EventHandler(ignoreCancelled = true)
    public void onPush(PlayerRiptideEvent event) {
        final Staus sta = staMap.get(event.getPlayer().getUniqueId());
        if (sta != null && sta.activate){
            sta.stopFly();
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTp(PlayerTeleportEvent event) {
        final Staus sta = staMap.get(event.getPlayer().getUniqueId());
        if (sta != null){
            sta.lastLoc = event.getTo();
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
        for (var entry : l) {
            Staus sta = entry.getValue();
            sta.stopFly();
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
                                sta.stopFly();
                                it.remove();
                                return;
                            }
                            ItemStack hat = sta.getItem();

                            if (sta.activate){
                                if (ItemToolUtil.damage(hat,1) || !canFlyin(sta.player)){
                                    sta.stopFly();
                                }
                                sta.hashSpeed();
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

    public static boolean canFlyin(Player player) {
        if (!player.hasPermission("rpgarmour.use.bamboodragonfly")) return false;

        if (player.hasPotionEffect(PotionEffectType.DARKNESS)) return false;

        final Biome biome = player.getWorld().getBiome(player.getLocation());
        return biome != Biome.DEEP_DARK;
    }


    public class Staus {
        final float defFlyspeed;
        boolean activate = false;
        Player player;
        short flag = 10;
        private final ItemStack item;
        Location lastLoc;
        long lastTime;
        short prewarningValue = 0;

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
                return hat != null && hat.hashCode() == item.hashCode();
            }
            return false;
        }

        public ItemStack getItem() {
            return item;
        }

        public void hashSpeed() {
            long nowTime = System.currentTimeMillis();
            final Location nowLoc = player.getLocation();
            //获取水平距离
            double distance = Math.sqrt(NumberConversions.square(nowLoc.getX() - lastLoc.getX()) + NumberConversions.square(nowLoc.getZ() - lastLoc.getZ()));
            distance /= ((double) (nowTime - lastTime) / 1000); //根据时间得秒速
            lastLoc = nowLoc;
            lastTime = nowTime;
            if (Setting.DEBUG) player.sendActionBar(String.format("速度: %.2f",distance));

            if (distance > 9){
                prewarningValue++;
                if (prewarningValue == 4){
                    player.sendMessage(" §c检测到阁下使用作弊工具，请停止您的作弊行为，否则可能会面临处罚。");
                }
                if (prewarningValue >= 7){
                    player.sendMessage(" §c因为使用§f修改飞行速度§c已没收道具§f" + LangUtils.getItemDisplayName(item));
                    item.setItemMeta(null);
                    item.setAmount(0);
                }
            } else if (prewarningValue > 0){
                prewarningValue--;
            }
        }

        //停止飞行
        public void stopFly() {
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
            final int maxHeight = l.getWorld().getMaxHeight();
            if (l.getY() > maxHeight){
                l.setY(maxHeight);
                player.teleport(l);
            }

//            PlayerInventory pi = player.getInventory();
//            ItemStack he = pi.getChestplate();
//            if (he != null && he.getType() == Material.ELYTRA){
//                pi.setChestplate(null);
//                player.getWorld().dropItem(l,he);
//            }
            stopSound.playTo(l);
            player.setCooldown(Material.MACE,200);
            activate = false;
            flag = 10;
        }

        public void onUse() {
            if (player.getAllowFlight()) return;
            if (!canFlyin(player)) return;

//限制这个没有用
//            ItemStack chestplate = player.getInventory().getChestplate();
//            if (chestplate != null && chestplate.getType() == Material.ELYTRA){
//                return;
//            }

            if (!hasItem()) return;
            ItemMeta im = item.getItemMeta();
            if (im instanceof final Damageable dm){
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
            //记录当前位置和时间
            lastLoc = l;
            lastTime = System.currentTimeMillis();
            setTimer();
        }

        public void remove() {
            if (activate) stopFly();
            staMap.remove(player.getUniqueId());
        }
    }
}

