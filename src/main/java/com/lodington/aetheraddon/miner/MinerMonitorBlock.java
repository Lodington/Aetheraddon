package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MinerMonitorBlock extends Block {

    public MinerMonitorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            // Find adjacent miner
            SpudMinerBlockEntity miner = findAdjacentMiner(level, pos);
            if (miner == null) {
                player.displayClientMessage(Component.literal("§cNo adjacent Spud Miner found!"), true);
                return InteractionResult.SUCCESS;
            }

            int gpus = miner.getInstalledGPUs();
            int energy = miner.getEnergyStorage().getEnergyStored();
            int maxEnergy = miner.getEnergyStorage().getMaxEnergyStored();
            int consumption = SpudMinerBlockEntity.getEnergyConsumption(gpus);
            int coolers = GpuCoolerBlock.countCoolersAdjacent(level, miner.getBlockPos());
            int actualConsumption = (int)(consumption * (1.0 - coolers * 0.2));
            int totalMined = miner.getGeneratedSpud();

            // Calculate spuds per minute
            double spudsPerMin = 0;
            if (gpus > 0) {
                int ticksPerSpud = 600 / gpus;
                spudsPerMin = 1200.0 / ticksPerSpud;
            }

            player.displayClientMessage(Component.literal(
                    "§6══ MINER MONITOR ══\n" +
                    "§7GPUs: §f" + gpus + "/10\n" +
                    "§7Coolers: §b" + coolers + "/3\n" +
                    "§7Energy: §e" + energy / 1000 + "k/" + maxEnergy / 1000 + "k FE\n" +
                    "§7Draw: §c" + actualConsumption + " FE/t" +
                    (coolers > 0 ? " §7(§b-" + (coolers * 20) + "%§7)" : "") + "\n" +
                    "§7Rate: §a" + String.format("%.1f", spudsPerMin) + " spuds/min\n" +
                    "§7Total Mined: §f" + totalMined
            ), false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private SpudMinerBlockEntity findAdjacentMiner(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.relative(dir);
            if (level.getBlockState(adjacent).is(ModBlocks.SPUD_MINER.get())) {
                BlockEntity be = level.getBlockEntity(adjacent);
                if (be instanceof SpudMinerBlockEntity miner) {
                    return miner;
                }
            }
        }
        return null;
    }
}
