package cn.whiteg.rpgArmour.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class VectorUtils {


    /**
     * @param loc1 起始位置
     * @param loc2 结束位置
     * @return 从起点到终点的方位
     */
    public static float getLocYaw(Location loc1,Location loc2) {
        return (float) Math.toDegrees(Math.atan2(-(loc2.getX() - loc1.getX()),loc2.getZ() - loc1.getZ())); //转角度
    }

    /**
     * @param loc1 起始位置
     * @param loc2 结束位置
     * @return 从起点到终点的仰角
     */
    public static float getLocPitch(Location loc1,Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return (float) Math.toDegrees(Math.atan2(loc1.getY() - loc2.getY(),Math.sqrt(dx * dx + dz * dz)));
    }

    /**
     * 利用球面坐标系坐标获得视角单位向量
     *
     * @param loc 视角
     * @return 视角单位向量
     */
    public static Vector viewVector(Location loc,Location loc2) {
        return viewVector(getLocYaw(loc,loc2),getLocPitch(loc,loc2));
    }

    /**
     * 利用球面坐标系坐标获得视角单位向量
     *
     * @param loc 视角
     * @return 视角单位向量
     */
    public static Vector viewVector(Location loc) {
        return viewVector(loc.getYaw(),loc.getPitch());
    }

    /**
     * 利用球面坐标系坐标获得视角单位向量
     *
     * @return 视角单位向量
     */
    public static Vector viewVector(float yaw,float pitch) {
        return new Vector(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),-Math.sin(Math.toRadians(pitch)),Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    }

    /**
     * @param yaw1 角度1
     * @param yaw2 角度2
     * @return 两个Yaw轴的相差角度
     */
    public static float getIncludedAngle(float yaw1,float yaw2) {
        float t = Math.abs(yaw1 - yaw2);
        return Math.min(360 - t,t);
    }

    /**
     * @param yaw1 角度1
     * @param yaw2 角度2
     * @return 两个Yaw轴的差值
     */
    public static float getDifferenceAngle(float yaw1,float yaw2) {
        yaw1 += 180F;
        yaw2 += 180F;
        float t = yaw1 - yaw2;
        if (t > 180) t -= 360;
        else if (t < -180) t += 360;
        return t;
    }

    /**
     * 判断怪物是否在玩家视角的一个锥型球面范围内
     * [near,far]代表作用距离
     * 返回锥角为degree制
     * 返回一个[0,1]的浮点数代表与玩家位置near的接近程度。
     * 返回一个小于0的浮点数代表不在此范围内。
     *
     * @undersilence 2019.11.4
     */
    public static float checkViewCone(Location near,Location far,float ConeAngle) {
        Vector v = viewVector(near);
        Vector u = far.toVector().subtract(near.toVector());
        double a_cos = Math.acos(u.dot(v) / u.length() / v.length());
        return 1 - (float) (a_cos / Math.toRadians(ConeAngle));
    }

    public static float checkViewCone(Location near,Vector vector,Location far,float ConeAngle) {
        Vector u = far.toVector().subtract(near.toVector());
        double a_cos = Math.acos(u.dot(vector) / u.length() / vector.length());
        return 1 - (float) (a_cos / Math.toRadians(ConeAngle));
    }

    /**
     * 计算风阻
     *
     * @param v 速度值
     * @param c 风阻
     * @param p 空气密度
     * @param s 迎风面积
     * @return 阻值
     * 示例: resistance(300F,0.5, 1.2 , 0.01 * 0.01)
     */
    public static double resistance(double v,float c,float p,float s) {
        return c * p * s * v * v / 2;
    }

}
