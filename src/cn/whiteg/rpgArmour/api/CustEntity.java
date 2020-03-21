package cn.whiteg.rpgArmour.api;


import cn.whiteg.rpgArmour.manager.CustEntityManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;


public interface CustEntity {


    default boolean init(Entity entity) {
        var con = CustEntityManager.getPersistentDataContainer(entity,true);
        con.set(CustEntityManager.TYPE_KEY,PersistentDataType.STRING,getId());
        CustEntityManager.setPersistentDataContainer(entity,con);
        return true;
    }

    default boolean is(Entity entity) {
        var con = CustEntityManager.getPersistentDataContainer(entity,false);
        if (con == null) return false;
        return getId().equals(con.get(CustEntityManager.TYPE_KEY,PersistentDataType.STRING));
    }

    Entity summon(Location location);

    default String getId() {
        return this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }
}
