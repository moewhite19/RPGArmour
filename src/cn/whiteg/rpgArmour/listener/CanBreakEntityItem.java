package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.event.BreakCustItemEntityEvent;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.PluginUtil;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

public class CanBreakEntityItem implements Listener {
    public static String TAG = "candestroy";
    public final List<CustItem> canPlaceItemFarm = new LinkedList<>();

    public static Item drop(ArmorStand armorStand,Location loc) {
        ItemStack itemStack = armorStand.getEquipment().getHelmet();
        final BreakCustItemEntityEvent breakCustItemEntityEvent = new BreakCustItemEntityEvent(armorStand,itemStack);
        if (breakCustItemEntityEvent.callEvent()){
            armorStand.remove();
            itemStack = breakCustItemEntityEvent.getDropStack();
            if (!ItemToolUtil.itemIsAir(itemStack)){
                return loc.getWorld().dropItem(loc,itemStack);
            }
        }
        return null;
    }

    public static Item drop(ItemFrame e,Location loc) {
        ItemStack itemStack = e.getItem();
        final BreakCustItemEntityEvent breakCustItemEntityEvent = new BreakCustItemEntityEvent(e,itemStack);
        if (breakCustItemEntityEvent.callEvent()){
            e.remove();
            itemStack = breakCustItemEntityEvent.getDropStack();
            if (!ItemToolUtil.itemIsAir(itemStack)){
                return loc.getWorld().dropItem(loc,itemStack);
            }
        }
        return null;
    }

    public boolean addCanPlaceItemFarm(CustItem custItem) {
        return canPlaceItemFarm.add(custItem);
    }

    public boolean removeCanPlaceItemFarm(CustItem custItem) {
        return canPlaceItemFarm.remove(custItem);
    }


    //盔甲架破坏
    //貌似只有火炮和扫帚依赖这个
    @EventHandler
    public void onLClickEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand as){
            Player player = null;
            Entity damage = event.getDamager();
            if (damage instanceof Projectile projectile){
                if (projectile.getShooter() instanceof Player) player = (Player) ((Projectile) damage).getShooter();
            } else if (damage instanceof Player) player = (Player) damage;
            if (player == null) return;
            if (as.isVisible()) return;
            if (!as.getScoreboardTags().contains(TAG)) return;
            Location loc = as.getLocation();
            //检查领地权限
            Residence res = Residence.getInstance();
            if (!res.isResAdminOn(player)){
                FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                if (!flag.playerHasHints(player,Flags.destroy,true)){
                    return;
                }
            }

            drop(as,loc);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageItemFarm(EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame && itemFrame.getScoreboardTags().contains(TAG)){
            event.setCancelled(true);
            drop(itemFrame,itemFrame.getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onItemFrameBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame && itemFrame.getScoreboardTags().contains(TAG)){
            event.setCancelled(true);
            drop(itemFrame,itemFrame.getLocation());
        }
    }

    //锁住的展示框不会触发Damage事件，所以只好用交互事件右键了
    @Deprecated(since = "现在已经不需要锁住展示框了，这里留着只是为了拆除现在已存在锁死展示框")
//    @EventHandler
    public void onRClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isSneaking() && event.getRightClicked() instanceof ItemFrame itemFrame){
            if (itemFrame.isFixed() && itemFrame.getScoreboardTags().contains(TAG)){
                Location loc = itemFrame.getLocation();
                //检查领地权限
                Residence res = Residence.getInstance();
                Player player = event.getPlayer();
                if (!res.isResAdminOn(player)){
                    FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                    if (!flag.playerHasHints(player,Flags.destroy,true)){
                        return;
                    }
                }
                drop(itemFrame,loc);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (Bukkit.getServer().isStopping()) return;
        final Plugin plugin = event.getPlugin();
        //如果插件被卸载，删除已注册的自定义物品
        canPlaceItemFarm.removeIf(ca -> plugin.equals(PluginUtil.getPluginFormClass(ca.getClass())));
    }

    //todo 还需要完善放置方向
    @EventHandler(ignoreCancelled = true)
    public void placeItemFarm(PlayerInteractEvent event) {
        if (canPlaceItemFarm.isEmpty() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getBlockFace() != BlockFace.UP)
            return;
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack item;
        PlayerInventory pi = player.getInventory();
        if (hand == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else if (hand == EquipmentSlot.OFF_HAND){
            item = pi.getItemInOffHand();
        } else return;

        for (CustItem custItem : canPlaceItemFarm) {
            if (custItem.is(item)){
                Block block = event.getClickedBlock();
                if (block == null || !block.isSolid()) return;
                event.setCancelled(true);
                Location loc = block.getLocation();
                loc.setY(loc.getY() + 1);
                if (loc.getBlock().isSolid()) return;

                //检查领地权限
                Residence res = Residence.getInstance();
                if (!res.isResAdminOn(player)){
                    FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,player);
                    if (!flag.playerHasHints(player,Flags.place,true)){
                        return;
                    }
                }

                //生成物品展示框
                ItemFrame itemFrame;
                try{
                    itemFrame = loc.getWorld().spawn(loc.toBlockLocation(),ItemFrame.class);
                }catch (IllegalArgumentException e){
                    return;
                }
                if (itemFrame.isDead()) return;

                if (item.getAmount() > 1){
                    item.setAmount(item.getAmount() - 1);
                } else {
                    pi.setItem(hand,null);
                }


                itemFrame.setFacingDirection(BlockFace.UP);
                float yaw = Math.abs(EntityUtils.getEntityRotYaw(player) % 360);
                itemFrame.setRotation(getRotation(yaw));
                //player.sendMessage("方向: " + getRotation(yaw) + " : " + yaw);
//                itemFrame.setFixed(true);
                itemFrame.setVisible(false);
                itemFrame.addScoreboardTag(TAG);
                itemFrame.addScoreboardTag("dontedit");
                item = item.clone();
                item.setAmount(1);
                itemFrame.setItem(item);
                return;
            }
        }
    }

    public Rotation getRotation(float yaw) {
        if (yaw > 337.5) return Rotation.FLIPPED;
        if (yaw > 292.5) return Rotation.CLOCKWISE_135;
        if (yaw > 247.5) return Rotation.CLOCKWISE;
        if (yaw > 202.5) return Rotation.CLOCKWISE_45;
        if (yaw > 157.5) return Rotation.NONE;
        if (yaw > 112.5) return Rotation.COUNTER_CLOCKWISE_45;
        if (yaw > 67.5) return Rotation.COUNTER_CLOCKWISE;
        if (yaw > 22.5) return Rotation.FLIPPED_45;
        return Rotation.FLIPPED;
    }
}
