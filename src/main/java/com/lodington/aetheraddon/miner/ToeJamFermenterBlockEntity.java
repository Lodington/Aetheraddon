package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Toe Jam Fermenter block entity.
 *
 * Feed it Raw Spuds as fuel. Over time it ferments them into Toe Jam.
 * - Consumes 1 Raw Spud per cycle
 * - Takes 1200 ticks (60 seconds) per Toe Jam produced
 * - Holds up to 16 Raw Spuds as fuel
 * - Holds up to 32 Toe Jam in the output buffer
 * - Output can be extracted via hoppers/pipes
 */
public class ToeJamFermenterBlockEntity extends BlockEntity {

    public static final int FERMENT_TIME = 1200; // ticks per toe jam (60 seconds)
    public static final int MAX_FUEL = 16;
    public static final int MAX_OUTPUT = 32;

    private int fuelCount = 0;
    private int progress = 0;

    // Output slot holds produced toe jam
    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return MAX_OUTPUT;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.TOE_JAM.get());
        }
    };

    // Input slot for hoppers/pipes to feed raw spuds
    private final ItemStackHandler inputSlot = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return MAX_FUEL;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.RAW_SPUD.get());
        }
    };

    // Combined handler for external access: slot 0 = input (raw spud), slot 1 = output (toe jam)
    private final IItemHandler externalHandler = new IItemHandler() {
        @Override
        public int getSlots() { return 2; }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 0) return inputSlot.getStackInSlot(0);
            if (slot == 1) return outputSlot.getStackInSlot(0);
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Only allow inserting raw spuds into input slot
            if (slot == 0) return inputSlot.insertItem(0, stack, simulate);
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Only allow extracting from output slot
            if (slot == 1) return outputSlot.extractItem(0, amount, simulate);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) return MAX_FUEL;
            if (slot == 1) return MAX_OUTPUT;
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return stack.is(ModItems.RAW_SPUD.get());
            return false;
        }
    };

    public ToeJamFermenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOE_JAM_FERMENTER.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return externalHandler;
    }

    public boolean isFermenting() {
        return fuelCount > 0 || !inputSlot.getStackInSlot(0).isEmpty();
    }

    public int getFuelCount() {
        return fuelCount + inputSlot.getStackInSlot(0).getCount();
    }

    public int getProgress() {
        return progress;
    }

    public int getOutputCount() {
        return outputSlot.getStackInSlot(0).getCount();
    }

    public void addFuel(int amount) {
        fuelCount += amount;
        if (fuelCount > MAX_FUEL) fuelCount = MAX_FUEL;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ToeJamFermenterBlockEntity fermenter) {
        // Pull from input slot into internal fuel counter
        ItemStack inputStack = fermenter.inputSlot.getStackInSlot(0);
        if (!inputStack.isEmpty() && fermenter.fuelCount < MAX_FUEL) {
            int canTake = Math.min(inputStack.getCount(), MAX_FUEL - fermenter.fuelCount);
            fermenter.fuelCount += canTake;
            fermenter.inputSlot.extractItem(0, canTake, false);
            fermenter.setChanged();
        }

        // Need fuel to ferment
        if (fermenter.fuelCount <= 0) {
            fermenter.progress = 0;
            return;
        }

        // Check if output has space
        ItemStack output = fermenter.outputSlot.getStackInSlot(0);
        if (!output.isEmpty() && output.getCount() >= MAX_OUTPUT) {
            return; // Output full
        }

        // Ferment
        fermenter.progress++;
        if (fermenter.progress >= FERMENT_TIME) {
            fermenter.progress = 0;
            fermenter.fuelCount--;

            // Produce 1 toe jam
            if (output.isEmpty()) {
                fermenter.outputSlot.setStackInSlot(0, new ItemStack(ModItems.TOE_JAM.get(), 1));
            } else {
                output.grow(1);
            }
            fermenter.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("fuelCount", fuelCount);
        tag.putInt("progress", progress);
        tag.put("input", inputSlot.serializeNBT(registries));
        tag.put("output", outputSlot.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuelCount = tag.getInt("fuelCount");
        progress = tag.getInt("progress");
        if (tag.contains("input")) inputSlot.deserializeNBT(registries, tag.getCompound("input"));
        if (tag.contains("output")) outputSlot.deserializeNBT(registries, tag.getCompound("output"));
    }
}
