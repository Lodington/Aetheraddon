package com.lodington.aetheraddon;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GripperFeetEffect extends MobEffect {

    public GripperFeetEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B4513); // Brown color
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
