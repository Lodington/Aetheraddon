package com.lodington.aetheraddon;

import com.lodington.aetheraddon.compat.SpudVaultPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SpudVaultBlockEntity extends BlockEntity {
    private int spud = 0;
    private int spudding = 0;
    private int spuddington = 0;

    @Nullable
    private SpudVaultPeripheral peripheral;

    public SpudVaultBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPUD_VAULT.get(), pos, state);
    }

    public void setPeripheral(@Nullable SpudVaultPeripheral peripheral) {
        this.peripheral = peripheral;
    }

    public int getSpud() { return spud; }
    public int getSpudding() { return spudding; }
    public int getSpuddington() { return spuddington; }

    public void setSpud(int val) {
        int old = spud;
        spud = val;
        setChanged();
        if (peripheral != null && old != val) {
            peripheral.fireChangedEvent("spud", old, val);
        }
    }

    public void setSpudding(int val) {
        int old = spudding;
        spudding = val;
        setChanged();
        if (peripheral != null && old != val) {
            peripheral.fireChangedEvent("spudding", old, val);
        }
    }

    public void setSpuddington(int val) {
        int old = spuddington;
        spuddington = val;
        setChanged();
        if (peripheral != null && old != val) {
            peripheral.fireChangedEvent("spuddington", old, val);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("spud", spud);
        tag.putInt("spudding", spudding);
        tag.putInt("spuddington", spuddington);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        spud = tag.getInt("spud");
        spudding = tag.getInt("spudding");
        spuddington = tag.getInt("spuddington");
    }
}
