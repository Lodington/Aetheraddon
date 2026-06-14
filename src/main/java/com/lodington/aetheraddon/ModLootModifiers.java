package com.lodington.aetheraddon;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AetherAddon.MOD_ID);

    public static final Supplier<MapCodec<SpudLootModifier>> SPUD_LOOT = LOOT_MODIFIERS.register("spud_loot",
            () -> SpudLootModifier.CODEC);
}
