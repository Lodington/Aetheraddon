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

public class CoolantCondenserBlockEntity extends BlockEntity {
    // Tank capacity: 8 buckets each
    public static final int TANK_CAPACITY = 8000;

    // Cooling takes time: processes 500 mB every 20 ticks (1 second per half bucket)
    // A full 8000 mB tank takes 16 seconds to fully condense
    public static final int PROCESS_AMOUNT = 500;
    public static final int PROCESS_INTERVAL = 20; // ticks between each conversion

    private int processTimer = 0;

    // Energy: 200,000 FE capacity, 5,000 FE/t max input, consumes 40 FE/t while condensing
    private int energyStored = 0;
    private static final int MAX_ENERGY = 200000;
    private static final int MAX_RECEIVE = 5000;
    public static final int ENERGY_PER_TICK = 40;

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

    // Input tank: accepts heated coolant
    private final FluidTank inputTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.HEATED_COOLANT_SOURCE.get();
        }
    };

    // Output tank: holds cooled gripper coolant
    private final FluidTank outputTank = new FluidTank(TANK_CAPACITY) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.GRIPPER_COOLANT_SOURCE.get();
        }
    };

    // Combined handler: fill puts heated coolant into input tank, drain extracts gripper coolant from output tank
    private final IFluidHandler combinedHandler = new IFluidHandler() {
        @Override
        public int getTanks() { return 2; }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? inputTank.getFluidInTank(0) : outputTank.getFluidInTank(0);
        }

        @Override
        public int getTankCapacity(int tank) { return TANK_CAPACITY; }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return tank == 0 && inputTank.isFluidValid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return inputTank.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return outputTank.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return outputTank.drain(maxDrain, action);
        }
    };

    public CoolantCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COOLANT_CONDENSER.get(), pos, state);
    }

    public IFluidHandler getFluidHandler() {
        return combinedHandler;
    }

    public EnergyStorage getEnergyStorage() {
        return energy;
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public int getInputAmount() {
        return inputTank.getFluidAmount();
    }

    public int getOutputAmount() {
        return outputTank.getFluidAmount();
    }

    public int getTankCapacity() {
        return TANK_CAPACITY;
    }

    public boolean isProcessing() {
        return energyStored >= ENERGY_PER_TICK
                && inputTank.getFluidAmount() >= PROCESS_AMOUNT
                && outputTank.getFluidAmount() + PROCESS_AMOUNT <= TANK_CAPACITY;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CoolantCondenserBlockEntity condenser) {
        if (condenser.inputTank.getFluidAmount() < PROCESS_AMOUNT) return;
        if (condenser.outputTank.getFluidAmount() + PROCESS_AMOUNT > TANK_CAPACITY) return;
        if (condenser.energyStored < ENERGY_PER_TICK) return;

        // Consume energy every tick while processing
        condenser.energyStored -= ENERGY_PER_TICK;

        condenser.processTimer++;
        if (condenser.processTimer >= PROCESS_INTERVAL) {
            condenser.processTimer = 0;
            // Convert heated coolant to gripper coolant
            condenser.inputTank.drain(PROCESS_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            condenser.outputTank.fill(
                    new FluidStack(ModFluids.GRIPPER_COOLANT_SOURCE.get(), PROCESS_AMOUNT),
                    IFluidHandler.FluidAction.EXECUTE
            );
        }
        condenser.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inputTank", inputTank.writeToNBT(registries, new CompoundTag()));
        tag.put("outputTank", outputTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("processTimer", processTimer);
        tag.putInt("energy", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inputTank")) {
            inputTank.readFromNBT(registries, tag.getCompound("inputTank"));
        }
        if (tag.contains("outputTank")) {
            outputTank.readFromNBT(registries, tag.getCompound("outputTank"));
        }
        processTimer = tag.getInt("processTimer");
        energyStored = tag.getInt("energy");
    }
}
