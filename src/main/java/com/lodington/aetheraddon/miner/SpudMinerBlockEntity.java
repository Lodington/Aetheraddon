package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModBlocks;
import com.lodington.aetheraddon.ModItems;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;

public class SpudMinerBlockEntity extends BlockEntity {
    private NonNullList<ItemStack> gpuSlots = NonNullList.withSize(10, ItemStack.EMPTY);
    private int tickCounter = 0;
    private int generatedSpud = 0;

    // Energy storage: 1,000,000 FE capacity, 10,000 FE/t max input
    private final EnergyStorage energy = new EnergyStorage(1000000, 10000, 0);

    // Power scales exponentially per GPU: 100, 250, 500, 1000, 2000, 3500, 5500, 8000, 11000, 15000
    private static final int[] FE_PER_GPU = { 0, 100, 250, 500, 1000, 2000, 3500, 5500, 8000, 11000, 15000 };

    public static int getEnergyConsumption(int gpuCount) {
        if (gpuCount <= 0 || gpuCount > 10) return 0;
        return FE_PER_GPU[gpuCount];
    }

    public SpudMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPUD_MINER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SpudMinerBlockEntity miner) {
        int gpuCount = miner.getInstalledGPUs();
        if (gpuCount == 0) return;

        // Check if we have enough energy
        int energyNeeded = getEnergyConsumption(gpuCount);
        if (miner.energy.getEnergyStored() < energyNeeded) return;

        // Consume energy
        miner.energy.extractEnergy(energyNeeded, false);

        miner.tickCounter++;
        // Each GPU generates 1 raw spud every 600 ticks (30 seconds)
        // More GPUs = faster (10 GPUs = 1 per 60 ticks = 3 seconds)
        int ticksPerSpud = 600 / gpuCount;
        if (miner.tickCounter >= ticksPerSpud) {
            miner.tickCounter = 0;
            // Try to deposit into adjacent vault
            SpudVaultBlockEntity vault = miner.findAdjacentVault(level, pos);
            if (vault != null) {
                vault.setSpud(vault.getSpud() + 1);
                miner.generatedSpud++;
                miner.setChanged();
            }
        }
    }

    public int getInstalledGPUs() {
        int count = 0;
        for (ItemStack stack : gpuSlots) {
            if (!stack.isEmpty() && stack.is(ModItems.SPUD_GPU.get())) {
                count += stack.getCount();
            }
        }
        return Math.min(count, 10);
    }

    private SpudVaultBlockEntity findAdjacentVault(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = pos.relative(dir);
            if (level.getBlockState(adjacent).is(ModBlocks.SPUD_VAULT.get())) {
                BlockEntity be = level.getBlockEntity(adjacent);
                if (be instanceof SpudVaultBlockEntity vault) {
                    return vault;
                }
            }
        }
        return null;
    }

    public NonNullList<ItemStack> getGpuSlots() {
        return gpuSlots;
    }

    public int getGeneratedSpud() {
        return generatedSpud;
    }

    public ItemStack getGpuInSlot(int slot) {
        if (slot < 0 || slot >= 10) return ItemStack.EMPTY;
        return gpuSlots.get(slot);
    }

    public void setGpuInSlot(int slot, ItemStack stack) {
        if (slot < 0 || slot >= 10) return;
        gpuSlots.set(slot, stack);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, gpuSlots, registries);
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("generatedSpud", generatedSpud);
        tag.putInt("energy", energy.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        gpuSlots = NonNullList.withSize(10, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, gpuSlots, registries);
        tickCounter = tag.getInt("tickCounter");
        generatedSpud = tag.getInt("generatedSpud");
        energy.receiveEnergy(tag.getInt("energy"), false);
    }
}
