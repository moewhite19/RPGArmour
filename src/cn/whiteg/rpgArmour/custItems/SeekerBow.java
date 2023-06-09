package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.moeInfo.nms.ActionBar;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.entityWrapper.EntityWrapper;
import cn.whiteg.rpgArmour.event.ReadyThrowEvent;
import cn.whiteg.rpgArmour.utils.*;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.projectile.EntityProjectileThrowable;
import net.minecraft.world.entity.projectile.EntitySnowball;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SeekerBow extends CustItem_CustModle implements Listener {
    public static final String arrowTag = "seeker";
    static DecimalFormat decimalFormat = new DecimalFormat("#.#"); //数字格式化
    static boolean saveTarget = true;
    private static int duration = 60;
    private static float turningPower = 0.2F; //转向能力
    private static int delay = 4;
    private float spawnChance = 0.05f; //生成几率
    private float itemDropChance = 0.008F; //掉落几率

    public SeekerBow() {
        super(Material.BOW,4,"§e维维诺斯锚击弓");
        ConfigurationSection c = Setting.getCustItemConfig(this);
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
            turningPower = (float) c.getDouble("turningPower",turningPower);
            duration = c.getInt("duration",duration);
            delay = c.getInt("delay",delay);
            saveTarget = c.getBoolean("saveTarget",saveTarget);
        }
    }

    @EventHandler
    public void takeAim(ReadyThrowEvent event) {
        ItemStack item = event.getHandler().getItem();
        if (!is(item)) return;
        Player player = event.getPlayer();
        Selector.onStart(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShort(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof final Arrow arrow) || !is(event.getBow())) return;
        if (!(arrow.getShooter() instanceof final LivingEntity shooter)) return;
        if (event.getForce() < 0.5F) return;
        LivingEntity target = null;
        if (shooter instanceof Player){
            var selector = Selector.getSelector((Player) shooter);
            if (selector == null) return;
            target = selector.getAndCleanTarget();
            selector.cancel();
        } else if (shooter instanceof Mob){
            target = ((Mob) shooter).getTarget();
        }
        if (target == null || target.isDead()) return;
        new SeekerArrow(arrow,target,event.getForce()).start();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (is(event.getOffHandItem()) || is(event.getMainHandItem())){
            var selector = Selector.getSelector(player);
            if (selector != null && selector.isRun()){
                selector.switchLock();
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow){
            if (!arrow.getScoreboardTags().isEmpty()) arrow.getScoreboardTags().remove(arrowTag); //当箭矢命中目标后删除自身跟踪箭tag
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Selector.remove(event.getPlayer());
    }


    //骷髅生成
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        Entity entity = event.getEntity();
        Random random = RandomUtil.getRandom();
        if (EntityUtils.isSpawner(entity)) return;
        if (random.nextDouble() < spawnChance){
            EntityEquipment ej = ((Skeleton) entity).getEquipment();
            if (ej != null){
                ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.1F);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(itemDropChance);
            }
        }
    }

    public static class Selector implements Runnable {
        private final static Map<String, Selector> map = new HashMap<>();
        private final Player player;
        SelectorEntity selectorEntity = new SelectorEntity();
        private LivingEntity target = null;
        private boolean lastTarget = false;
        private BossBar bossBar = null;
        private boolean lock = false;
        private BukkitTask task = null;

        Selector(Player player) {
            this.player = player;
        }

        static void onStart(Player player) {
            var selector = map.get(player.getName());
            if (selector == null){
                selector = new Selector(player);
                map.put(player.getName(),selector);
            }
            selector.start();
        }

        static void remove(Player player) {
            if (map.isEmpty()) return;
            Selector sc = map.remove(player.getName());
            if (sc != null) sc.cancel();
        }

        static Selector getSelector(Player player) {
            return map.get(player.getName());
        }

        void start() {
            if (isRun()) return;
            task = Bukkit.getScheduler().runTaskTimer(RPGArmour.plugin,this,2L,2L);
        }

        boolean isRun() {
            return task != null;
        }

        public void run() {
            var itemUseTimeLeft = EntityUtils.getItemUseTimeLeft(player);
            if (player.isDead() || itemUseTimeLeft <= 0){
                cancel();
                return;
            }
            double viewDistance = 6 * 16;
            var entitys = player.getNearbyEntities(viewDistance,viewDistance,viewDistance);
            double f = 0F;
            var pLoc = player.getEyeLocation();
            if (!lock){
                target = null;
                for (Entity e : entitys) {
                    LivingEntity le;
                    if (e instanceof Mob || e instanceof Boss){
                        le = (LivingEntity) e;
                    } else if (e instanceof Player tPlayer){
                        //不追踪隐身以及生存模式以外的玩家
                        if (player.canSee(tPlayer) && (tPlayer.getGameMode() == GameMode.SURVIVAL || tPlayer.getGameMode() == GameMode.ADVENTURE)){
                            le = tPlayer;
                        } else continue;
                    } else {
                        continue;
                    }
                    if (le.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue; //不追踪隐身目标
                    var eLoc = e.getLocation();
                    eLoc.setY(eLoc.getY() + (e.getHeight() / 2));
                    var v = VectorUtils.checkViewCone(pLoc,eLoc,25F) - (pLoc.distance(eLoc) / viewDistance);
                    if (v > f){
                        target = le;
                        f = v;
                    }
                }
            }

            //发送BossBar
            if (Setting.DEBUG){
                if (target == null){
                    showBossBar("§b没有目标",0F);
                } else {
                    var d = target.getLocation().distance(pLoc);
                    if (lock){
                        showBossBar("§b锁定目标:§f" + LangUtils.getEntityName(target) + (" §r§b距离:§f") + decimalFormat.format(d),1F);
                    } else {
                        showBossBar("§b瞄准目标:§f" + LangUtils.getEntityName(target) + (" §r§b距离:§f") + decimalFormat.format(d),f);
                    }
                }
            }


            //发送瞄准实体
            if (target != null){
                Location loc = target.getLocation();
                loc.setY(loc.getY() + (target.getHeight() / 2));
                selectorEntity.setLocation(loc);
                if (lastTarget){
                    selectorEntity.sendPacket(selectorEntity.cratePacketMount(target),player);
//                    var p = selectorEntity.cratePacketEntityTeleport();
//                    selectorEntity.sendPacket(p,player);
                } else {
                    selectorEntity.spawn(player);
                    lastTarget = true;
                }
            } else if (lastTarget){
                selectorEntity.remove(player);
                lastTarget = false;
            }
        }

        public LivingEntity getAndCleanTarget() {
            var t = target;
            if (t != null && (target.isDead() || target.getWorld() != player.getWorld())){
                t = null;
            }
            //如果配置没开或者玩家没有指定权限则清理目标
            if (!saveTarget && !player.hasPermission("rpgarmour.seekerbow.savetarget")){
                target = null;
            }
            return t;
        }


        public void cancel() throws IllegalStateException {
            if (!isRun()) return;
            if (bossBar != null){
                bossBar.removeAll();
            }
            if (lastTarget){
                selectorEntity.remove(player);
                lastTarget = false;
            }
            Bukkit.getScheduler().cancelTask(task.getTaskId());
            task = null;
        }

        public void showBossBar(String str,double d) {
            if (bossBar == null){
                bossBar = Bukkit.createBossBar(str,BarColor.WHITE,BarStyle.SEGMENTED_20);
                bossBar.addPlayer(player);
            } else bossBar.setTitle(str);
            if (d > 1) d = 1;
            if (d < 0) d = 0;
            bossBar.setProgress(d);
        }

        public void switchLock() {
            this.lock = !lock;
            ActionBar.sendActionBar(player,lock ? "§b锁定目标" : "§3取消锁定");
        }

    }

    //选择指示
    public static class SelectorEntity extends EntityWrapper {
        private static final ItemStack item = new ItemStack(Material.SNOWBALL);
        static DataWatcherObject<net.minecraft.world.item.ItemStack> itemData;

        static {
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(10);
            item.setItemMeta(meta);
            try{
//                var itemField = EntityProjectileThrowable.class.getDeclaredField("b");
                var itemField = ReflectUtil.getFieldFormType(EntityProjectileThrowable.class,DataWatcherObject.class);
                itemField.setAccessible(true);
                //noinspection unchecked
                itemData = (DataWatcherObject<net.minecraft.world.item.ItemStack>) itemField.get(null);
            }catch (NoSuchFieldException | IllegalAccessException e){
                e.printStackTrace();
            }
        }


        public SelectorEntity() {
            super(NMSUtils.getEntityType(EntitySnowball.class));
            initDataWatcher();
            setNoGravity(true);
        }

        /**
         * Create a NMS data watcher object to send via a {@code PacketPlayOutEntityMetadata} packet.
         * Gravity will be disabled and the custom name will be displayed if available.
         */
        @Override
        public void initDataWatcher() {
            super.initDataWatcher();
            dataWatcher.a(itemData,CraftItemStack.asNMSCopy(item));
        }
    }

    //跟踪箭矢
    public static class SeekerArrow extends BukkitRunnable {
        final Projectile projectile;
        final LivingEntity target;
        private final float force;
        Vector lastLocation;
        int i = duration;
        Vector prejudge = new Vector(); //向量预判
        int distance;

        SeekerArrow(Projectile projectile,LivingEntity target,float force) {
            this.projectile = projectile;
            this.target = target;
            this.force = force;
            lastLocation = target.getLocation().toVector();
            distance = ((int) projectile.getLocation().distance(target.getLocation()));
            i += distance >> 2;
        }

        @Override
        public void run() {
            if (i <= 0 || (projectile instanceof AbstractArrow && ((AbstractArrow) projectile).isInBlock()) || projectile.isDead() || target.isDead() || !projectile.getScoreboardTags().contains(arrowTag) || target.getWorld() != projectile.getWorld()){
                cancel();
                return;
            }
            i--;
            var aLoc = projectile.getLocation(); //弓箭位置
            Location tLoc = target.getLocation(); //目标位置
            tLoc.setY(tLoc.getY() + (target.getHeight() * 0.7)); //将位置提升半个多升高
            var av = projectile.getVelocity(); //弓箭原始向量
            var avc = VectorUtils.checkViewCone(aLoc,av,tLoc,15); //弓箭向量与目标位置的差值
            aLoc.getWorld().spawnParticle(Particle.END_ROD,aLoc,0,0,0,0,1);
            prejudge = tLoc.clone().subtract(lastLocation).toVector(); //预判
            prejudge.multiply(Math.min(200D,tLoc.distance(aLoc) * (av.length()) / 2));
            lastLocation = tLoc.toVector();
            tLoc.add(prejudge);
            var vec = VectorUtils.viewVector(aLoc,tLoc);//转向向量
            av.setY(av.getY() + 0.045); //弓箭的重力补偿(烂办法x
            float m = turningPower * //基础转向能力
                    (0.5f + (((float) Math.min(i,duration) / duration) / 2)) * //根据发射出去后的时间削弱
                    (avc > 0F ? 1 + (avc * 4) : 1) * //当箭头指向目标时加速
                    force; //拉弓程度
            vec.multiply(m); //放大转向
            av.multiply(1 - (m / 1.5)).add(vec); //消减原始速度并入转向向量
            projectile.setVelocity(av);
            if (Setting.DEBUG){
                CommandSender sender;
                if (projectile.getShooter() instanceof Player){
                    sender = (Player) projectile.getShooter();
                } else if (target instanceof Player){
                    sender = target;
                } else {
                    return;
                }
                sender.sendMessage("向量差" + decimalFormat.format(avc) + " 加速倍率" + m);
                sender.sendMessage("预判" + decimalFormat.format(prejudge.getX()) + ", " + decimalFormat.format(prejudge.getY()) + ", " + decimalFormat.format(prejudge.getZ()));
                sender.sendMessage("-----------");
            }
        }

        public void start() {
            runTaskTimer(RPGArmour.plugin,delay + (distance >> 4),1L);
            projectile.addScoreboardTag(arrowTag); //为箭矢添加tag ，当命中后（包括命中盾牌，被插件阻止等）删除tag以此取消跟踪箭状态
        }
    }
}
