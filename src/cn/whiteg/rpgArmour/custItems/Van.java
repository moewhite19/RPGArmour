package cn.whiteg.rpgArmour.custItems;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;

public class Van extends CustItem_CustModle implements Listener {

    public Van() {
        super(Material.SHEARS,27,"§5一起Van");
    }


    @EventHandler(ignoreCancelled = true)
    public void onRClickEntity(PlayerInteractEntityEvent event) {
        EquipmentSlot hand = event.getHand();
        Player p = event.getPlayer();
        PlayerInventory inv = p.getInventory();
        ItemStack item;
        if (hand == EquipmentSlot.HAND){
            item = inv.getItemInMainHand();
        } else {
            item = inv.getItemInOffHand();
        }
        if (!is(item)) return;
        event.setCancelled(true);
        Entity e = event.getRightClicked();
        if (p.isSneaking()){
            p.addPassenger(e);
        } else {
            e.addPassenger(p);
        }
//        RPGArmour.logger.info(p.getName());
    }

    //测试实体AI操作
    //@EventHandler(ignoreCancelled = true)
    public void onReClickEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof Player p)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        PlayerInventory pi = p.getInventory();
        if (!is(pi.getItemInMainHand())) return;
        Entity entity = event.getEntity();
        if (entity instanceof CraftCreature){
            LivingEntity e = (LivingEntity) entity;
            EntityCreature nmsEntity = ((CraftCreature) e).getHandle();
            //PathfinderGoalSelector
//            nmsEntity.bQ.a(1,new PathfinderGoalMeleeAttack(nmsEntity,1.0D,true));

            event.setCancelled(true);
            try{
                Field f = EntityInsentient.class.getDeclaredField("bO");//ControllerJump
                f.setAccessible(true);
                f.set(nmsEntity,((CraftPlayer) p).getHandle());
                event.setCancelled(true);
            }catch (NoSuchFieldException | IllegalAccessException ex){
                ex.printStackTrace();
            }
                /*
                //实体跳跃
                try{
                    Field f = EntityInsentient.class.getDeclaredField("bq");
                    f.setAccessible(true);
                    ControllerJump jump = (ControllerJump) f.get(fe);
                    jump.jump();
                    event.setCancelled(true);
                }catch (NoSuchFieldException | IllegalAccessException ex){
                    ex.printStackTrace();
                }*/

                /*
                //设置实体目标
                try{
                    Field f = EntityInsentient.class.getDeclaredField("goalTarget");
                    f.setAccessible(true);
                    f.set(fe,((CraftPlayer) p).getHandle());
                    event.setCancelled(true);
                }catch (NoSuchFieldException | IllegalAccessException ex){
                    ex.printStackTrace();
                }
                */
        }
    }

//    @EventHandler(ignoreCancelled = true)
//    public void onReClickEntity(EntityDamageByEntityEvent event) {
//        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof Player)) return;
//        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
//        final Player player = (Player) event.getDamager();
//        PlayerInventory pi = player.getInventory();
//        if (!is(pi.getItemInMainHand())) return;
//        LivingEntity livent = (LivingEntity) event.getEntity();
////            livent.setLeashHolder(player);
//        ActionBar.sendActionBar(player,"拴上实体");
//        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
//        final EntityFishingHook fishingHook = new EntityFishingHook(nmsPlayer,nmsPlayer.getWorld(),0,0);
//        final Location loc = player.getLocation();
////        fishingHook.locX = loc.getX();
////        fishingHook.locY = loc.getY();
////        fishingHook.locZ = loc.getZ();
//        event.setCancelled(true);
//
//    }
}


