package com.lodington.aetheraddon;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, AetherAddon.MOD_ID);

    public static final Holder<MobEffect> GRIPPER_FEET = MOB_EFFECTS.register("gripper_feet",
            GripperFeetEffect::new);
}
