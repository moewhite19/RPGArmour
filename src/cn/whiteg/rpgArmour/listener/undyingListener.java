package cn.whiteg.rpgArmour.listener;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class undyingListener implements Listener {

    @Deprecated
    public static void useOffHand(PlayerInventory inv,ItemStack item) {
        ItemStack ofin = inv.getItemInOffHand();
        inv.setItemInOffHand(item);
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            inv.setItemInOffHand(ofin);
        });
    }

    public static void EntityResurrect(LivingEntity livingEntity,ItemStack item) {
        EntityLiving entity = ((CraftLivingEntity) livingEntity).getHandle();
        if (item != null){
            net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            if (entity instanceof EntityPlayer){
                EntityPlayer entityplayer = ((EntityPlayer) entity);
                entityplayer.b(StatisticList.ITEM_USED.b(nmsItem.getItem()));
                CriterionTriggers.B.a(entityplayer,nmsItem);
            }
        }
        entity.setHealth(1.0F);
        entity.removeAllEffects(EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffect(MobEffects.RESISTANCE,20,5),EntityPotionEffectEvent.Cause.TOTEM);
        entity.addEffect(new MobEffect(MobEffects.REGENERATION,40,3),EntityPotionEffectEvent.Cause.TOTEM);
        entity.world.broadcastEntityEffect(entity,(byte) 35);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeat(PlayerDeathPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        if (player.getLocation().getY() < 0) return;
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
            return;
        }
    }
}
