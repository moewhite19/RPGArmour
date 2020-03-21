package cn.whiteg.rpgArmour.api;

import cn.whiteg.rpgArmour.manager.CustEntityManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Set;

public abstract class CustEntityID implements CustEntity {
    private final String id;
    private final Class<? extends Entity> entityClass;

    public CustEntityID(String id,Class<? extends Entity> c) {
        this.id = id;
        this.entityClass = c;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean init(Entity entity) {
        final Set<String> tags = entity.getScoreboardTags();
        tags.add(CustEntityManager.custtag);
        tags.add(id);
        return true;
    }

    @Override
    public boolean is(Entity entity) {
//        if (entity.getClass() != entityClass){
//            return false;
//        }
        final Set<String> tags = entity.getScoreboardTags();
        return tags.contains(id);
    }

    @Override
    public Entity summon(final Location location) {
        final Entity ent = location.getWorld().spawn(location,entityClass);
//        if (ent.isDead()) return null;
        this.init(ent);
        return ent;
    }
}
