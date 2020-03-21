package cn.whiteg.rpgArmour.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class CustEntityID implements CustEntity {
    private final String id;
    private final Class<? extends Entity> entityClass;

    public CustEntityID(String id,Class<? extends Entity> c) {
        this.id = id;
        this.entityClass = c;
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public Entity summon(final Location location) {
        final Entity ent = location.getWorld().spawn(location,entityClass);
//        if (ent.isDead()) return null;
        this.init(ent);
        return ent;
    }
}
