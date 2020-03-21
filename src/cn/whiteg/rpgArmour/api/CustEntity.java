package cn.whiteg.rpgArmour.api;

import com.sun.istack.internal.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


public interface CustEntity {

    boolean init(Entity entity);

    boolean is(Entity entity);

    Entity summon(Location location);
}
