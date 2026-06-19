package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;

public class ClusterControllerBlockEntity extends BlockEntity {
    // Modest energy: 100,000 FE capacity, 5,000 FE/t max input
    private int energyStored = 0;
    private static final int MAX_ENERGY = 100000;
    private static final int MAX_RECEIVE = 5000;
    // Consumes 50 FE/t when active
    public static final int ENERGY_PER_TICK = 50;

    private boolean powered = false;

    private final EnergyStorage energy = new EnergyStorage(MAX_ENERGY, MAX_RECEIVE, 0) {
        @Override
        public int getEnergyStored() {
            return energyStored;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int accepted = Math.min(MAX_ENERGY - energyStored, Math.min(MAX_RECEIVE, maxReceive));
            if (!simulate) energyStored += accepted;
            return accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    };

    public ClusterControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLUSTER_CONTROLLER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public boolean isPowered() {
        return powered;
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClusterControllerBlockEntity controller) {
        if (controller.energyStored >= ENERGY_PER_TICK) {
            controller.energyStored -= ENERGY_PER_TICK;
            controller.powered = true;
        } else {
            controller.powered = false;
        }
        controller.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStored);
        tag.putBoolean("powered", powered);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStored = tag.getInt("energy");
        powered = tag.getBoolean("powered");
    }
}
