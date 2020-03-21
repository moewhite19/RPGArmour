package cn.whiteg.rpgArmour.api;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class CustEntityName implements CustEntity {
    public final Class<? extends Entity> entityClass;
    private final String name;
    private final String rawName;

    public CustEntityName(String naem,Class<? extends Entity> c) {
        this.name = naem;
        this.entityClass = c;
        rawName = ChatColor.stripColor(naem);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return getRawName();
    }

    @Override
    public Entity summon(final Location location) {
        final Entity ent = location.getWorld().spawn(location,entityClass);
//        if (ent.isDead()) return null;
        this.init(ent);
        return ent;
    }

    public String getRawName() {
        return rawName;
    }
}
