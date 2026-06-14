package com.lodington.aetheraddon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GriptiumPickaxe extends Item {

    public GriptiumPickaxe(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        // Only take durability damage if mining chudington ore
        if (state.is(ModBlocks.CHUDINGTON_ORE.get())) {
            stack.hurtAndBreak(1, miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Only effective on chudington ore, useless on everything else
        if (state.is(ModBlocks.CHUDINGTON_ORE.get())) {
            return 20.0f;
        }
        return 0.1f; // slower than fist on anything else
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // Only counts as correct tool for chudington ore
        return state.is(ModBlocks.CHUDINGTON_ORE.get());
    }
}
