package com.lodington.aetheraddon.wallet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

public record WalletData(int spud, int spudding, int spuddington) {

    public static WalletData fromStack(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return new WalletData(0, 0, 0);
        CompoundTag tag = customData.copyTag();
        return new WalletData(
                tag.getInt("spud"),
                tag.getInt("spudding"),
                tag.getInt("spuddington")
        );
    }

    public void saveToStack(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("spud", spud);
        tag.putInt("spudding", spudding);
        tag.putInt("spuddington", spuddington);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public WalletData withSpud(int spud) {
        return new WalletData(spud, this.spudding, this.spuddington);
    }

    public WalletData withSpudding(int spudding) {
        return new WalletData(this.spud, spudding, this.spuddington);
    }

    public WalletData withSpuddington(int spuddington) {
        return new WalletData(this.spud, this.spudding, spuddington);
    }
}
