package cn.whiteg.rpgArmour.enitity;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;

import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumCreatureType;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyMode extends EntityArmorStand {
    public static EntityTypes<net.minecraft.server.v1_16_R3.Entity> md;

    static {
        if (Setting.DEBUG){
            RPGArmour.console.sendMessage("注册实体");
//            try{
//                Method ma = EntityTypes.class.getDeclaredMethod("a",String.class,EntityTypes.a.class);
//                ma.setAccessible(true);
//                md = (EntityTypes<net.minecraft.server.v1_16_R3.Entity>) ma.invoke(null,"armor_stand",EntityTypes.a.a(MyMode::new,EnumCreatureType.MISC).a(0.5F,1.975F));
//            }catch (NoSuchMethodException e){
//                e.printStackTrace();
//            }catch (IllegalAccessException e){
//                e.printStackTrace();
//            }catch (InvocationTargetException e){
//                e.printStackTrace();
//            }
        }
    }

    public MyMode(World world) {
        super(world,0,0,0);
        this.modspawn();
    }

    public MyMode(EntityTypes entityTypes,World world) {
        super(entityTypes,world);
//            super(entitytypes,world);
////            this.handItems = NonNullList.a(2,ItemStack.a);
////            this.armorItems = NonNullList.a(4,ItemStack.a);
//            this.headPose = new Vector3f(0,0,0);
//            this.bodyPose = new Vector3f(0,0,0);
//            this.leftArmPose = new Vector3f(0,0,0);
//            this.rightArmPose = new Vector3f(0,0,0);
//            this.leftLegPose = new Vector3f(0,0,0);
//            this.rightLegPose = new Vector3f(0,0,0);
//            this.K = 0.0F;
    }
/*
    public MyMode(Location loc) {
        this.locX = loc.getX();
        this.locY = loc.getY();
        this.locZ = loc.getZ();
    }
*/

    public void modspawn() {
        this.setArms(true);
        MMOCore.logger.info("生成自定义盔甲架");
    }
}
