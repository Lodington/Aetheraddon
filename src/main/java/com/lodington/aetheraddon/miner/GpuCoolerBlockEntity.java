package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class GpuCoolerBlockEntity extends BlockEntity {
    // Energy: 50,000 FE capacity, 2,000 FE/t max input, consumes 20 FE/t
    private int energyStored = 0;
    private static final int MAX_ENERGY = 50000;
    private static final int MAX_RECEIVE = 2000;
    public static final int ENERGY_PER_TICK = 20;

    // Cold tank: gripper coolant input (4 buckets)
    public static final int TANK_CAPACITY = 4000;
    // Transfer rate: 5 mB/t from cold to hot (4000 ticks / 5 = 800 ticks = 40 seconds per full tank)
    public static final int COOLANT_TRANSFER_PER_TICK = 5;

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

    // Cold tank: only accepts gripper coolant (input from pipes)
    private final FluidTank coldTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.GRIPPER_COOLANT_SOURCE.get();
        }
    };

    // Hot tank: holds heated coolant (output to pipes)
    private final FluidTank hotTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.HEATED_COOLANT_SOURCE.get();
        }
    };

    // Wrapper that only allows filling the cold tank (for input pipes)
    private final IFluidHandler inputHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return coldTank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int tank) { return TANK_CAPACITY; }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return coldTank.isFluidValid(stack); }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return coldTank.fill(resource, action); }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    // Wrapper that only allows draining the hot tank (for output pipes)
    private final IFluidHandler outputHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return hotTank.getFluidInTank(0); }

        @Override
        public int getTankCapacity(int tank) { return TANK_CAPACITY; }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return false; }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return hotTank.drain(resource, action); }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) { return hotTank.drain(maxDrain, action); }
    };

    public GpuCoolerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GPU_COOLER.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public IFluidHandler getInputHandler() {
        return inputHandler;
    }

    public IFluidHandler getOutputHandler() {
        return outputHandler;
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

    public int getColdCoolantAmount() {
        return coldTank.getFluidAmount();
    }

    public int getHotCoolantAmount() {
        return hotTank.getFluidAmount();
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GpuCoolerBlockEntity cooler) {
        // Needs energy, cold coolant available, and hot tank not full
        boolean hasEnergy = cooler.energyStored >= ENERGY_PER_TICK;
        boolean hasColdCoolant = cooler.coldTank.getFluidAmount() >= COOLANT_TRANSFER_PER_TICK;
        boolean hotTankHasSpace = cooler.hotTank.getFluidAmount() + COOLANT_TRANSFER_PER_TICK <= TANK_CAPACITY;

        if (hasEnergy && hasColdCoolant && hotTankHasSpace) {
            cooler.energyStored -= ENERGY_PER_TICK;
            // Transfer cold coolant to heated coolant
            cooler.coldTank.drain(COOLANT_TRANSFER_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            cooler.hotTank.fill(new FluidStack(ModFluids.HEATED_COOLANT_SOURCE.get(), COOLANT_TRANSFER_PER_TICK), IFluidHandler.FluidAction.EXECUTE);
            cooler.powered = true;
        } else {
            cooler.powered = false;
        }
        cooler.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energyStored);
        tag.putBoolean("powered", powered);
        tag.put("coldTank", coldTank.writeToNBT(registries, new CompoundTag()));
        tag.put("hotTank", hotTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStored = tag.getInt("energy");
        powered = tag.getBoolean("powered");
        if (tag.contains("coldTank")) {
            coldTank.readFromNBT(registries, tag.getCompound("coldTank"));
        }
        if (tag.contains("hotTank")) {
            hotTank.readFromNBT(registries, tag.getCompound("hotTank"));
        }
    }
}
