package cn.whiteg.rpgArmour.api;

import cn.whiteg.rpgArmour.manager.CustEntityManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Set;

public abstract class CustEntityName implements CustEntity {
    public final Class<? extends Entity> entityClass;
    private final String name;
    private String rawName;

    public CustEntityName(String naem,Class<? extends Entity> c) {
        this.name = naem;
        this.entityClass = c;
        rawName = ChatColor.stripColor(naem);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean init(Entity entity) {
        Set<String> tags = entity.getScoreboardTags();
        tags.add(CustEntityManager.custtag);
        entity.setCustomName(name);
        return true;
    }

    @Override
    public boolean is(Entity entity) {
        String en = entity.getCustomName();
        if (en == null || en.isEmpty()) return false;
        return en.contains(name);
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
