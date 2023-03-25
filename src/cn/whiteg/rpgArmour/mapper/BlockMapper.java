package cn.whiteg.rpgArmour.mapper;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;

import java.lang.reflect.Method;

public class BlockMapper {
    static Method getBlockMethod;

    static {
        for (Method method : BlockBase.BlockData.class.getMethods()) {
            if (method.getReturnType().equals(Block.class)){
                getBlockMethod = method;
                break;
            }
        }
    }

    public static IBlockData getNmsBlock(org.bukkit.block.Block block) {
        return ((CraftBlock) block).getNMS();
    }

    public Block getBlock(IBlockData blockData) {
        try{
            return (Block) getBlockMethod.invoke(blockData);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
