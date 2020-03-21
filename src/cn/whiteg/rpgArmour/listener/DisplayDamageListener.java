package cn.whiteg.rpgArmour.listener;

import cn.whiteg.moeInfo.nms.ActionBar;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.entityWrapper.HoloText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class DisplayDamageListener implements Listener {
    final static Random random = new Random();
    static Map<EntityDamageEvent.DamageCause,Character> causeColorMap = new EnumMap<>(EntityDamageEvent.DamageCause.class);
    static char defColor = 'c';
    static {
        causeColorMap.put(EntityDamageEvent.DamageCause.FALL,'7');
        causeColorMap.put(EntityDamageEvent.DamageCause.FIRE,'4');
        causeColorMap.put(EntityDamageEvent.DamageCause.LAVA,'4');
        causeColorMap.put(EntityDamageEvent.DamageCause.FIRE_TICK,'4');
        causeColorMap.put(EntityDamageEvent.DamageCause.MELTING,'4');
        causeColorMap.put(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,'6');
        causeColorMap.put(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,'6');
        causeColorMap.put(EntityDamageEvent.DamageCause.MAGIC,'d');
        causeColorMap.put(EntityDamageEvent.DamageCause.POISON,'2');
        causeColorMap.put(EntityDamageEvent.DamageCause.STARVATION,'3');
        causeColorMap.put(EntityDamageEvent.DamageCause.WITHER,'0');
        causeColorMap.put(EntityDamageEvent.DamageCause.DRAGON_BREATH,'1');
        causeColorMap.put(EntityDamageEvent.DamageCause.DRYOUT,'8');
        causeColorMap.put(EntityDamageEvent.DamageCause.FREEZE,'b');
        causeColorMap.put(EntityDamageEvent.DamageCause.CUSTOM,'f');
        causeColorMap.put(EntityDamageEvent.DamageCause.SUICIDE,'f');
        causeColorMap.put(EntityDamageEvent.DamageCause.VOID,'f');
        causeColorMap.put(EntityDamageEvent.DamageCause.LIGHTNING,'f');
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getFinalDamage() <= 0){
            return;
        }
        final Entity e = event.getEntity();
        //如果不是玩家或者生物还是跳出叭
        if (!(e instanceof Mob) && !(e instanceof HumanEntity)) return;
        final LivingEntity le = (LivingEntity) e;
        final String damagestr = damageToString(event);
        if (event.getEntity() instanceof Player){
            ActionBar.sendActionBar((Player) event.getEntity(),damagestr);
        }

        final List<Player> playerList = new ArrayList<>();
        for (Entity entity : e.getNearbyEntities(16,16,16)) {
            if (entity instanceof Player){
                playerList.add((Player) entity);
            }
        }
        //如果玩家列表为空则跳出
        if (playerList.isEmpty()) return;
        final Location loc = le.getEyeLocation();
        loc.add(new Location(loc.getWorld(),random.nextDouble() - 0.5D,random.nextDouble() - 0.5D,random.nextDouble() - 0.5D));
        final HoloText aw = new HoloText(loc,damagestr);
        aw.playerShow(playerList);
//        Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> aw.setVector(new Vector(1D,2D,0D)),20);
        Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,(Runnable) aw::remove,40);
    }

    public String damageToString(EntityDamageEvent event) {
        StringBuilder sb = new StringBuilder("§f-§").append(causeColorMap.getOrDefault(event.getCause(), defColor)).append(String.format("%.2f",event.getFinalDamage()));
        if (event.getDamage() != event.getFinalDamage())
            sb.append(" §7(").append(String.format("%.2f",event.getDamage())).append(")");
        return sb.toString();
    }

}
