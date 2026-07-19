package com.lodington.aetheraddon.autocraft;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * External-facing item handler for the autocrafter.
 *
 * Only exposes the single output slot. The autocrafter drives connected
 * inventories directly for ingredients — no input slots needed.
 * Pipes/hoppers/CC can extract crafted results from slot 0.
 */
public class SpudAutocrafterItemHandler implements IItemHandler {
    private final SpudAutocrafterBlockEntity crafter;

    public SpudAutocrafterItemHandler(SpudAutocrafterBlockEntity crafter) {
        this.crafter = crafter;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) return crafter.getOutputSlot().getStackInSlot(0);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        // No inserting into the crafter — it drives storage directly
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0) return crafter.getOutputSlot().extractItem(0, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot == 0) return crafter.getOutputSlot().getSlotLimit(0);
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false; // Nothing can be inserted externally
    }
}
