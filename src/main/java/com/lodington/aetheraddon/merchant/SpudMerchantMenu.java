package com.lodington.aetheraddon.merchant;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class SpudMerchantMenu extends AbstractContainerMenu {
    private final SpudMerchantBlockEntity merchant;
    private final BlockPos merchantPos;

    // Server constructor
    public SpudMerchantMenu(int containerId, Inventory inventory, SpudMerchantBlockEntity merchant) {
        super(ModMenuTypes.SPUD_MERCHANT_MENU.get(), containerId);
        this.merchant = merchant;
        this.merchantPos = merchant.getBlockPos();
        addPlayerSlots(inventory);
    }

    // Client constructor
    public SpudMerchantMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.SPUD_MERCHANT_MENU.get(), containerId);
        BlockPos pos = buf.readBlockPos();
        this.merchantPos = pos;
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        this.merchant = be instanceof SpudMerchantBlockEntity m ? m : null;
        addPlayerSlots(inventory);
    }

    private void addPlayerSlots(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 198));
        }
    }

    public List<TradeEntry> getTrades() {
        if (merchant == null) return List.of();
        return merchant.getTrades();
    }

    public SpudMerchantBlockEntity getMerchant() {
        return merchant;
    }

    public BlockPos getMerchantPos() {
        return merchantPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
