package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ClusterControllerBlock extends BaseEntityBlock {
    public static final MapCodec<ClusterControllerBlock> CODEC = simpleCodec(ClusterControllerBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public ClusterControllerBlock(Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClusterControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.CLUSTER_CONTROLLER.get(), ClusterControllerBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            List<SpudMinerBlockEntity> miners = findConnectedMiners(level, pos);

            if (miners.isEmpty()) {
                player.displayClientMessage(Component.literal("§cNo miners connected! Use Spud Network Cables."), false);
                return InteractionResult.SUCCESS;
            }

            int totalGPUs = 0;
            int totalMined = 0;
            int totalConsumption = 0;
            int totalEnergy = 0;
            int totalMaxEnergy = 0;

            for (SpudMinerBlockEntity miner : miners) {
                int gpus = miner.getInstalledGPUs();
                totalGPUs += gpus;
                totalMined += miner.getGeneratedSpud();
                int baseConsumption = SpudMinerBlockEntity.getEnergyConsumption(gpus);
                int coolers = GpuCoolerBlock.countCoolersAdjacent(level, miner.getBlockPos());
                totalConsumption += (int)(baseConsumption * (1.0 - coolers * 0.2));
                totalEnergy += miner.getEnergyStorage().getEnergyStored();
                totalMaxEnergy += miner.getEnergyStorage().getMaxEnergyStored();
            }

            // Calculate combined spuds per minute
            double spudsPerMin = 0;
            for (SpudMinerBlockEntity miner : miners) {
                int gpus = miner.getInstalledGPUs();
                if (gpus > 0) {
                    int ticksPerSpud = 600 / gpus;
                    spudsPerMin += 1200.0 / ticksPerSpud;
                }
            }

            // Get controller's own energy status
            BlockEntity controllerBE = level.getBlockEntity(pos);
            String controllerStatus = "§cUnpowered";
            int controllerEnergy = 0;
            int controllerMaxEnergy = 0;
            if (controllerBE instanceof ClusterControllerBlockEntity controller) {
                controllerEnergy = controller.getEnergyStored();
                controllerMaxEnergy = controller.getMaxEnergy();
                controllerStatus = controller.isPowered() ? "§aPowered" : "§cNo Power";
            }

            player.displayClientMessage(Component.literal(
                    "§6══ CLUSTER CONTROLLER ══\n" +
                    "§7Status: " + controllerStatus + "\n" +
                    "§7Controller Energy: §e" + controllerEnergy / 1000 + "k/" + controllerMaxEnergy / 1000 + "k FE §7(" + ClusterControllerBlockEntity.ENERGY_PER_TICK + " FE/t)\n" +
                    "§7Miners Connected: §f" + miners.size() + "\n" +
                    "§7Total GPUs: §f" + totalGPUs + "\n" +
                    "§7Miner Energy: §e" + totalEnergy / 1000 + "k/" + totalMaxEnergy / 1000 + "k FE\n" +
                    "§7Miner Draw: §c" + totalConsumption + " FE/t\n" +
                    "§7Combined Rate: §a" + String.format("%.1f", spudsPerMin) + " spuds/min\n" +
                    "§7Total Mined (all): §f" + totalMined
            ), false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * BFS through cables to find all connected miners within 16 blocks
     */
    private List<SpudMinerBlockEntity> findConnectedMiners(Level level, BlockPos controllerPos) {
        List<SpudMinerBlockEntity> miners = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        visited.add(controllerPos);

        // Start from all adjacent positions
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = controllerPos.relative(dir);
            if (!visited.contains(adjacent)) {
                queue.add(adjacent);
                visited.add(adjacent);
            }
        }

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            // Check distance limit (16 blocks)
            if (current.distManhattan(controllerPos) > 16) continue;

            BlockState state = level.getBlockState(current);

            // If it's a miner, add it
            if (state.is(ModBlocks.SPUD_MINER.get())) {
                BlockEntity be = level.getBlockEntity(current);
                if (be instanceof SpudMinerBlockEntity miner) {
                    miners.add(miner);
                }
                continue; // Don't traverse through miners
            }

            // If it's a cable, continue traversing
            if (state.is(ModBlocks.SPUD_NETWORK_CABLE.get())) {
                for (Direction dir : Direction.values()) {
                    BlockPos next = current.relative(dir);
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        return miners;
    }
}
