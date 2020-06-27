package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_16_R1.EntityTypes;

public class Zombie extends LivingEntityWrapper {
    public Zombie() {
        super(EntityTypes.SKELETON);
    }
}
