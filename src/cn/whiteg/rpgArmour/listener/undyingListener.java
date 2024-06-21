package cn.whiteg.rpgArmour.listener;

import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.reflection.ReflectionFactory;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UndyingListener implements Listener {
    static UsedTotemTrigger triggerUsedTotem;
//    static MethodInvoker<Void> worldServer_updateState;

    static {
        try{
            triggerUsedTotem = (UsedTotemTrigger) ReflectionFactory.createFieldAccessor(ReflectUtil.getFieldFormType(CriteriaTriggers.class,UsedTotemTrigger.class)).get(null);
        }catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }

        //这个暂时不用自适应
//        findMethod:
//        {
//            for (Method method : WorldServer.class.getMethods()) {
//                if (method.getReturnType().equals(void.class)){
//                    final Class<?>[] types = method.getParameterTypes();
//                    if (types.length == 2 && types[0].equals(Entity.class) && types[1].equals(byte.class)){
//                        worldServer_updateState = new MethodInvoker<>(method);
//                        break findMethod;
//                    }
//                }
//            }
//            throw new RuntimeException("Cant Find Method: worldServer_updateState");
//        }

    }

    @Deprecated
    public static void useOffHand(PlayerInventory inv,ItemStack item) {
        ItemStack ofin = inv.getItemInOffHand();
        inv.setItemInOffHand(item);
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            inv.setItemInOffHand(ofin);
        });
    }

    public static void EntityResurrect(LivingEntity livingEntity,ItemStack item) {
        net.minecraft.world.entity.LivingEntity entity = (net.minecraft.world.entity.LivingEntity) NMSUtils.getNmsEntity(livingEntity);
//        if (item != null){
//            if (entity instanceof ServerPlayer entityplayer){
//                entityplayer.b(StatisticList.c.b(Items.TOTEM_OF_UNDYING));
//                CriterionTriggers.B.a(entityplayer,nmsItem);
//            }
//        }
        //    抄自
//    EntityLiving.class;
        var nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null && entity instanceof ServerPlayer entityplayer){
            entityplayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
            CriteriaTriggers.USED_TOTEM.trigger(entityplayer,nmsItem);
            entity.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }

        entity.setHealth(1.0F);
        entity.removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION,900,1),org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,100,1),org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,800,0),org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
        entity.level().broadcastEntityEvent(entity,(byte) 35);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeat(PlayerDeathPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasCooldown(Material.TOTEM_OF_UNDYING)) return; //如果玩家图腾在CD则不触发保护 todo 在手里时咱未支持拦截
        PlayerInventory inv = player.getInventory();
        if (event.getDamageCause() == EntityDamageEvent.DamageCause.VOID) return; //虚空不能复活
        if (event.isCancelled()){
            ItemStack item = inv.getItemInMainHand();
            if (item.getType() == Material.TOTEM_OF_UNDYING){
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,5,1));
            }
            item = inv.getItemInOffHand();
            if (item.getType() == Material.TOTEM_OF_UNDYING){
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,5,1));
            }
            return;
        }
        ItemStack item = inv.getHelmet();
        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING){
            if (item.getAmount() > 1){
                item.setAmount(item.getAmount() - 1);
            } else {
                inv.setHelmet(null);
            }
            event.setCancelled(true);
            EntityResurrect(player,item);
            return;
        }
        item = inv.getChestplate();
        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING){
            if (item.getAmount() > 1){
                item.setAmount(item.getAmount() - 1);
            } else {
                inv.setChestplate(null);
            }
            event.setCancelled(true);
            EntityResurrect(player,item);
            return;
        }
        item = inv.getLeggings();
        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING){
            if (item.getAmount() > 1){
                item.setAmount(item.getAmount() - 1);
            } else {
                inv.setLeggings(null);
            }
            event.setCancelled(true);
            EntityResurrect(player,item);
            return;
        }
        item = inv.getBoots();
        if (item != null && item.getType() == Material.TOTEM_OF_UNDYING){
            if (item.getAmount() > 1){
                item.setAmount(item.getAmount() - 1);
            } else {
                inv.setBoots(null);
            }
            event.setCancelled(true);
            EntityResurrect(player,item);
        }
    }
}
