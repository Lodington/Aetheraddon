package com.lodington.aetheraddon;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class SpudVaultItemHandler implements IItemHandler {
    private final SpudVaultBlockEntity vault;

    public SpudVaultItemHandler(SpudVaultBlockEntity vault) {
        this.vault = vault;
    }

    @Override
    public int getSlots() {
        // 3 virtual slots: spud, spudding, spuddington
        return 3;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> vault.getSpud() > 0 ? new ItemStack(ModItems.SPUD.get(), Math.min(vault.getSpud(), 64)) : ItemStack.EMPTY;
            case 1 -> vault.getSpudding() > 0 ? new ItemStack(ModItems.SPUDDING.get(), Math.min(vault.getSpudding(), 64)) : ItemStack.EMPTY;
            case 2 -> vault.getSpuddington() > 0 ? new ItemStack(ModItems.SPUDDINGTON.get(), Math.min(vault.getSpuddington(), 64)) : ItemStack.EMPTY;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        // Accept any spud item into any slot
        if (stack.is(ModItems.SPUD.get())) {
            if (!simulate) vault.setSpud(vault.getSpud() + stack.getCount());
            return ItemStack.EMPTY;
        } else if (stack.is(ModItems.SPUDDING.get())) {
            if (!simulate) vault.setSpudding(vault.getSpudding() + stack.getCount());
            return ItemStack.EMPTY;
        } else if (stack.is(ModItems.SPUDDINGTON.get())) {
            if (!simulate) vault.setSpuddington(vault.getSpuddington() + stack.getCount());
            return ItemStack.EMPTY;
        }

        // Reject non-spud items
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return switch (slot) {
            case 0 -> {
                int actual = Math.min(amount, vault.getSpud());
                if (actual <= 0) yield ItemStack.EMPTY;
                if (!simulate) vault.setSpud(vault.getSpud() - actual);
                yield new ItemStack(ModItems.SPUD.get(), actual);
            }
            case 1 -> {
                int actual = Math.min(amount, vault.getSpudding());
                if (actual <= 0) yield ItemStack.EMPTY;
                if (!simulate) vault.setSpudding(vault.getSpudding() - actual);
                yield new ItemStack(ModItems.SPUDDING.get(), actual);
            }
            case 2 -> {
                int actual = Math.min(amount, vault.getSpuddington());
                if (actual <= 0) yield ItemStack.EMPTY;
                if (!simulate) vault.setSpuddington(vault.getSpuddington() - actual);
                yield new ItemStack(ModItems.SPUDDINGTON.get(), actual);
            }
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.is(ModItems.SPUD.get()) ||
               stack.is(ModItems.SPUDDING.get()) ||
               stack.is(ModItems.SPUDDINGTON.get());
    }
}
