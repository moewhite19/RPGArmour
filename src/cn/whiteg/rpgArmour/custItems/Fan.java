package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.mmocore.sound.SingleSound;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Fan extends CustItem_CustModle implements Listener {
    private final float power = 5F;
    private final float range = 6F;
    private final float angle = 35F;
    SingleSound sound = new SingleSound("minecraft:entity.player.attack.knockback",1F,0.1F);
    private Entity damager = null;

    public Fan() {
        super(Material.SHEARS,42,"§a芭蕉扇");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getDamage() < 0.5 || !(damager instanceof LivingEntity) || this.damager == damager)
            return;
        final ItemStack item = ((LivingEntity) damager).getEquipment().getItemInMainHand();
        if (is(item)){
            event.setCancelled(true);
            onUse((LivingEntity) damager,item);
        }
    }

    @EventHandler()
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        final Player player = event.getPlayer();
        final ItemStack item = player.getEquipment().getItemInMainHand();
        if (is(item)){
            onUse(player,item);
        }
    }

    public void onUse(LivingEntity user,ItemStack item) {
        if (user instanceof Player player){
            if (player.hasCooldown(getMaterial())){
                player.setCooldown(getMaterial(),20);
                return;
            }
            player.setCooldown(getMaterial(),20);
        }
        damager = user;
        Location loc = user.getLocation();
        loc.setY(loc.getY() + (user.getHeight() / 2));
        sound.playTo(loc);

        //获取使用者载具
        Entity uv = user.getVehicle();
        EntityType vt = uv == null ? null : uv.getType();
        for (Entity e : user.getNearbyEntities(range,range,range)) {
            if (!e.isValid() || e.isInsideVehicle() || e.getType() == vt || e.getType() == EntityType.ITEM_FRAME)
                continue;
            final Location loc2 = e.getLocation();
            loc2.setY(loc2.getY() + (e.getHeight() / 2));
            float yaw = VectorUtils.getLocYaw(loc,loc2);
            float ag = VectorUtils.checkViewCone(loc,loc2,angle);
            if (ag > 0){

                if(e.isInvulnerable()) continue; //忽略无敌实体

                EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(user,e,EntityDamageEvent.DamageCause.CUSTOM,0);
                Bukkit.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) continue;
                float pitch = VectorUtils.getLocPitch(loc,loc2);
                Vector vector = VectorUtils.viewVector(loc);
                loc2.setPitch(pitch);
                loc2.setYaw(yaw);
                Vector vector2 = VectorUtils.viewVector(loc2);
                double distance = loc.distance(loc2); //距离
                if (distance < 0.1) continue;
                float mult = (float) (distance / range); //距离衰减
                float fpower = power - (mult * power / 2);
                vector.midpoint(vector2);
                vector.setY(vector.getY() / 2);
                e.setVelocity(e.getVelocity().multiply(0.5F).add(vector.multiply(fpower)));
            }
        }
        damager = null;
        if (!item.getItemMeta().isUnbreakable() && RandomUtil.getRandom().nextDouble() < 0.5 && ItemToolUtil.damage(item,1)){
            user.getEquipment().setItemInMainHand(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (is(p.getInventory().getItemInMainHand())){
            event.setCancelled(true);
        }
    }
}

