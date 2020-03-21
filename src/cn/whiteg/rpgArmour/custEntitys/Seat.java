package cn.whiteg.rpgArmour.custEntitys;

import cn.whiteg.rpgArmour.api.CustEntityID;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Set;

public class Seat extends CustEntityID implements Listener {
    private static final Seat seat;

    static {
        seat = new Seat();
    }

    public Seat() {
        super("seat",ArmorStand.class);
    }

    public static Seat get() {
        return seat;
    }

    @EventHandler
    public void onChunkLoad(ChunkUnloadEvent event) {
        Entity[] entitiys = event.getChunk().getEntities();
        for (Entity entitiy : entitiys) {
            if (entitiy instanceof ArmorStand){
                if (((ArmorStand) entitiy).isMarker() || !((ArmorStand) entitiy).isVisible()){
                    Set<String> s = entitiy.getScoreboardTags();
                    if (s.contains("dontsave")){
                        entitiy.remove();
                    }
                }
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeave(EntityDismountEvent event) {
        Entity e = event.getDismounted();
        if (is(e)){
            e.remove();
        }
    }

    @Override
    public boolean init(Entity entity) {
        if (entity instanceof ArmorStand armorStand && super.init(entity)){
            armorStand.setMarker(true);
            armorStand.setVisible(false);
            armorStand.addScoreboardTag("dontsave");
            return true;
        }
        return false;
    }
}
