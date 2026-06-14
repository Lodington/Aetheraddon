package com.lodington.aetheraddon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpuddingtonItem extends Item {

    public SpuddingtonItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        // Default is 32 ticks, double it to 64
        return 64;
    }
}
