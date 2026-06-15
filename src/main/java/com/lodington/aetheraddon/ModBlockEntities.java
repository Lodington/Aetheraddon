package com.lodington.aetheraddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AetherAddon.MOD_ID);

    public static final Supplier<BlockEntityType<SpudVaultBlockEntity>> SPUD_VAULT = BLOCK_ENTITIES.register("spud_vault",
            () -> BlockEntityType.Builder.of(SpudVaultBlockEntity::new, ModBlocks.SPUD_VAULT.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.miner.SpudMinerBlockEntity>> SPUD_MINER = BLOCK_ENTITIES.register("spud_miner",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.miner.SpudMinerBlockEntity::new, ModBlocks.SPUD_MINER.get()).build(null));
}
