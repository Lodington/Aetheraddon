package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpudNetworkCableBlock extends Block {

    public SpudNetworkCableBlock(Properties properties) {
        super(properties);
    }

    public static boolean isCableOrMinerOrController(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(ModBlocks.SPUD_NETWORK_CABLE.get()) ||
               state.is(ModBlocks.SPUD_MINER.get()) ||
               state.is(ModBlocks.CLUSTER_CONTROLLER.get());
    }
}
