package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModItems;
import com.lodington.aetheraddon.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SpudMinerMenu extends AbstractContainerMenu {
    private final SpudMinerBlockEntity miner;
    private final ContainerData data;
    private final GpuItemHandler gpuHandler;

    // Server constructor
    public SpudMinerMenu(int containerId, Inventory inventory, SpudMinerBlockEntity miner) {
        super(ModMenuTypes.SPUD_MINER_MENU.get(), containerId);
        this.miner = miner;
        this.gpuHandler = new GpuItemHandler(miner);

        this.data = new SimpleContainerData(4);
        this.data.set(0, miner.getEnergyStorage().getEnergyStored());
        this.data.set(1, miner.getEnergyStorage().getMaxEnergyStored());
        this.data.set(2, miner.getGeneratedSpud());
        this.data.set(3, SpudMinerBlockEntity.getEnergyConsumption(miner.getInstalledGPUs()));
        this.addDataSlots(this.data);

        addGpuSlots();
        addPlayerSlots(inventory);
    }

    // Client constructor
    public SpudMinerMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.SPUD_MINER_MENU.get(), containerId);
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        this.miner = be instanceof SpudMinerBlockEntity m ? m : null;
        this.gpuHandler = new GpuItemHandler(miner);

        this.data = new SimpleContainerData(4);
        this.addDataSlots(this.data);

        addGpuSlots();
        addPlayerSlots(inventory);
    }

    private void addGpuSlots() {
        // 10 GPU slots in 2 rows of 5
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int index = row * 5 + col;
                this.addSlot(new SlotItemHandler(gpuHandler, index, 44 + col * 18, 20 + row * 18));
            }
        }
    }

    private void addPlayerSlots(Inventory inventory) {
        // Vanilla inventory texture blit at y+70, slots start 17px into the texture
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 88 + row * 18));
            }
        }
        // Hotbar is 58px below first inv row
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 146));
        }
    }

    public int getEnergy() { return data.get(0); }
    public int getMaxEnergy() { return data.get(1); }
    public int getTotalMined() { return data.get(2); }
    public int getConsumption() { return data.get(3); }

    @Override
    public void broadcastChanges() {
        if (miner != null) {
            data.set(0, miner.getEnergyStorage().getEnergyStored());
            data.set(1, miner.getEnergyStorage().getMaxEnergyStored());
            data.set(2, miner.getGeneratedSpud());
            data.set(3, SpudMinerBlockEntity.getEnergyConsumption(miner.getInstalledGPUs()));
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        // GPU slots (0-9)
        if (index < 10) {
            if (!this.moveItemStackTo(stack, 10, 46, true)) return ItemStack.EMPTY;
        } else {
            // Player inventory - try to move GPU into GPU slots
            if (stack.is(ModItems.SPUD_GPU.get())) {
                if (!this.moveItemStackTo(stack, 0, 10, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // Custom item handler that wraps the block entity's GPU slots
    private static class GpuItemHandler extends ItemStackHandler {
        private final SpudMinerBlockEntity miner;

        public GpuItemHandler(SpudMinerBlockEntity miner) {
            super(10);
            this.miner = miner;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (miner == null) return ItemStack.EMPTY;
            return miner.getGpuInSlot(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (miner != null) miner.setGpuInSlot(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) return stack;
            if (miner == null) return stack;
            if (!miner.getGpuInSlot(slot).isEmpty()) return stack;
            if (!simulate) miner.setGpuInSlot(slot, stack.copyWithCount(1));
            ItemStack remainder = stack.copy();
            remainder.shrink(1);
            return remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (miner == null) return ItemStack.EMPTY;
            ItemStack current = miner.getGpuInSlot(slot);
            if (current.isEmpty()) return ItemStack.EMPTY;
            if (!simulate) miner.setGpuInSlot(slot, ItemStack.EMPTY);
            return current;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModItems.SPUD_GPU.get());
        }
    }
}
