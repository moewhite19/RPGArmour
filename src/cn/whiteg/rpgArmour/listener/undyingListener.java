package cn.whiteg.rpgArmour.listener;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.reflection.ReflectionFactory;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.advancements.critereon.CriterionTriggerUsedTotem;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
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
    static CriterionTriggerUsedTotem triggerUsedTotem;
//    static MethodInvoker<Void> worldServer_updateState;

    static {
        try{
            triggerUsedTotem = (CriterionTriggerUsedTotem) ReflectionFactory.createFieldAccessor(ReflectUtil.getFieldFormType(CriterionTriggers.class,CriterionTriggerUsedTotem.class)).get(null);
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
        EntityLiving entity = (EntityLiving) NMSUtils.getNmsEntity(livingEntity);
//        if (item != null){
//            if (entity instanceof EntityPlayer entityplayer){
//                entityplayer.b(StatisticList.c.b(Items.TOTEM_OF_UNDYING));
//                CriterionTriggers.B.a(entityplayer,nmsItem);
//            }
//        }
        //    抄自
//    EntityLiving.class;

        var nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null && entity instanceof EntityPlayer entityplayer){
            entityplayer.b(StatisticList.c.b(Items.uz));
            triggerUsedTotem.a(entityplayer,nmsItem);
        }

        livingEntity.setHealth(1.0f);
        entity.removeAllEffects(EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffect(MobEffects.j,900,1),EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffect(MobEffects.v,100,1),EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffect(MobEffects.l,800,0),EntityPotionEffectEvent.Cause.TOTEM);
//        worldServer_updateState.invoke(NMSUtils.getNmsWorld(livingEntity.getWorld()),entity,(byte) 35);
        NMSUtils.getNmsWorld(livingEntity.getWorld()).a(entity,(byte) 35); //这里不应该做成自适应，因为上面那个设置特效的还没完成自适应
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
