package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class GpuCoolerBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GpuCoolerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            boolean foundMiner = false;
            for (Direction dir : Direction.values()) {
                BlockPos adjacent = pos.relative(dir);
                if (level.getBlockState(adjacent).is(ModBlocks.SPUD_MINER.get())) {
                    foundMiner = true;
                    break;
                }
            }
            if (foundMiner) {
                player.displayClientMessage(Component.literal("§bGPU Cooler: §aActive §7(20% power reduction)"), true);
            } else {
                player.displayClientMessage(Component.literal("§bGPU Cooler: §cNo adjacent Spud Miner found!"), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static int countCoolersAdjacent(Level level, BlockPos minerPos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(minerPos.relative(dir)).is(ModBlocks.GPU_COOLER.get())) {
                count++;
            }
        }
        return Math.min(count, 3);
    }
}
