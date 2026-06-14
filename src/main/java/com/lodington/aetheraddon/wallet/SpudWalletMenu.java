package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.ModItems;
import com.lodington.aetheraddon.ModMenuTypes;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
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

public class SpudWalletMenu extends AbstractContainerMenu {
    private final SpudVaultBlockEntity vault;
    private final ContainerData data;

    // Server constructor (from block or wallet)
    public SpudWalletMenu(int containerId, Inventory inventory, SpudVaultBlockEntity vault) {
        super(ModMenuTypes.SPUD_WALLET_MENU.get(), containerId);
        this.vault = vault;

        this.data = new SimpleContainerData(3);
        this.data.set(0, vault.getSpud());
        this.data.set(1, vault.getSpudding());
        this.data.set(2, vault.getSpuddington());

        this.addDataSlots(this.data);
        addPlayerSlots(inventory);
    }

    // Client constructor
    public SpudWalletMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.SPUD_WALLET_MENU.get(), containerId);
        this.data = new SimpleContainerData(3);
        this.data.set(0, buf.readInt());
        this.data.set(1, buf.readInt());
        this.data.set(2, buf.readInt());

        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        this.vault = be instanceof SpudVaultBlockEntity v ? v : null;

        this.addDataSlots(this.data);
        addPlayerSlots(inventory);
    }

    private void addPlayerSlots(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 150 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 208));
        }
    }

    public int getSpud() { return data.get(0); }
    public int getSpudding() { return data.get(1); }
    public int getSpuddington() { return data.get(2); }

    public void depositAll(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.SPUD.get())) {
                data.set(0, data.get(0) + stack.getCount());
                player.getInventory().setItem(i, ItemStack.EMPTY);
            } else if (stack.is(ModItems.SPUDDING.get())) {
                data.set(1, data.get(1) + stack.getCount());
                player.getInventory().setItem(i, ItemStack.EMPTY);
            } else if (stack.is(ModItems.SPUDDINGTON.get())) {
                data.set(2, data.get(2) + stack.getCount());
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
        saveToVault();
    }

    public boolean convertUp() {
        if (data.get(1) >= 8) {
            data.set(1, data.get(1) - 8);
            data.set(2, data.get(2) + 1);
            saveToVault();
            return true;
        }
        return false;
    }

    public boolean convertDown() {
        if (data.get(2) >= 1) {
            data.set(2, data.get(2) - 1);
            data.set(1, data.get(1) + 8);
            saveToVault();
            return true;
        }
        return false;
    }

    public ItemStack withdrawSpud(int amount) {
        int actual = Math.min(amount, Math.min(data.get(0), 64));
        if (actual > 0) {
            data.set(0, data.get(0) - actual);
            saveToVault();
            return new ItemStack(ModItems.SPUD.get(), actual);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack withdrawSpudding(int amount) {
        int actual = Math.min(amount, Math.min(data.get(1), 64));
        if (actual > 0) {
            data.set(1, data.get(1) - actual);
            saveToVault();
            return new ItemStack(ModItems.SPUDDING.get(), actual);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack withdrawSpuddington(int amount) {
        int actual = Math.min(amount, Math.min(data.get(2), 64));
        if (actual > 0) {
            data.set(2, data.get(2) - actual);
            saveToVault();
            return new ItemStack(ModItems.SPUDDINGTON.get(), actual);
        }
        return ItemStack.EMPTY;
    }

    private void saveToVault() {
        if (vault != null) {
            vault.setSpud(data.get(0));
            vault.setSpudding(data.get(1));
            vault.setSpuddington(data.get(2));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if (SpudWalletItem.isSpudItem(stack)) {
                if (stack.is(ModItems.SPUD.get())) data.set(0, data.get(0) + stack.getCount());
                else if (stack.is(ModItems.SPUDDING.get())) data.set(1, data.get(1) + stack.getCount());
                else if (stack.is(ModItems.SPUDDINGTON.get())) data.set(2, data.get(2) + stack.getCount());
                slot.set(ItemStack.EMPTY);
                saveToVault();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
