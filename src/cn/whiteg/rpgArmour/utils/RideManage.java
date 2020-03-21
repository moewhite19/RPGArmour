package cn.whiteg.rpgArmour.utils;

import cn.whiteg.moetp.utils.EntityTpUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RideManage {
    public static boolean Ride(Entity rider,Entity who) {
        if (rider == null) return false;
        if (who == null) return false;
        if (rider == who) return false;
        if (RideCheck(rider,who)){
            //连带着载具一起骑乘
//            if (rider.getVehicle() != null){
//                return Ride(rider.getVehicle(),who);
//            }
            Location loc1 = rider.getLocation();
            Location loc2 = who.getLocation();
            if (!loc1.getWorld().getName().equals(loc2.getWorld().getName()) || loc1.distance(loc2) > 32){
                if (rider instanceof Player){
                    if (!EntityTpUtils.PlayerOnceTp((Player) rider,who.getLocation())) return false;
                } else {
                    if (rider.teleport(who)){
                        return false;
                    }
                }
            }

//                if (rider.isEmpty()){
//                    rider.teleport(who);
//                    return who.addPassenger(rider);
//                }
//                List<Entity> team = new ArrayList<>();
//                team.add(rider);
//                getEntityTopList(rider,team);
//                for (Entity e : team) {
//                    e.teleport(who);
//                }
//                Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
//                    for (int i = team.size() - 1; i > 0; i--) {
//                        team.get(i - 1).addPassenger(team.get(i));
//                    }
//                });
//                return true;
            return who.addPassenger(rider);
        }
        return false;
    }

    public static boolean RideCheck(Entity e1,Entity e2) {
        if (e1 == e2) return false;
        if (e1.isEmpty() && e1.getVehicle() == null) return true;
        if (e2.isEmpty() && e2.getVehicle() == null) return true;
        List<Entity> team = getTeam(e1);
        team.addAll(getTeam(e2));
        return cheakIsRepeat(team);
    }

    public static List<Entity> getTeam(Entity entity) {
        Entity de = getEntityDownList(entity,new ArrayList<>());
        List<Entity> team = new ArrayList<>();
        getEntityTopList(de,team);
        return team;
    }

    public static Entity getEntityDownList(Entity entity,List<Entity> list) {
        if (entity.getVehicle() == null) return entity;
        list.add(entity.getVehicle());
        return getEntityDownList(entity.getVehicle(),list);
    }

    public static Entity getEntityDownList(Entity entity) {
        if (entity.getVehicle() == null) return entity;
        return getEntityDownList(entity.getVehicle());
    }

    public static Entity getEntityTopList(Entity entity,List<Entity> list) {
        if (entity.isEmpty()) return entity;
        list.addAll(entity.getPassengers());
        return getEntityTopList(list.get(list.size() - 1),list);
    }

    public static boolean cheakIsRepeat(List<Entity> array) {
        HashSet<Entity> hashSet = new HashSet<>();
        for (int i = 0; i < array.size(); i++) {
            hashSet.add(array.get(i));
        }
        if (hashSet.size() == array.size()){
            return true;
        } else {
            return false;
        }
    }
}
