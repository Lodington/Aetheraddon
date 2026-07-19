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

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.merchant.SpudMerchantBlockEntity>> SPUD_MERCHANT = BLOCK_ENTITIES.register("spud_merchant",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.merchant.SpudMerchantBlockEntity::new, ModBlocks.SPUD_MERCHANT.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.autocraft.SpudAutocrafterBlockEntity>> SPUD_AUTOCRAFTER = BLOCK_ENTITIES.register("spud_autocrafter",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.autocraft.SpudAutocrafterBlockEntity::new, ModBlocks.SPUD_AUTOCRAFTER.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.miner.ClusterControllerBlockEntity>> CLUSTER_CONTROLLER = BLOCK_ENTITIES.register("cluster_controller",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.miner.ClusterControllerBlockEntity::new, ModBlocks.CLUSTER_CONTROLLER.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.miner.GpuCoolerBlockEntity>> GPU_COOLER = BLOCK_ENTITIES.register("gpu_cooler",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.miner.GpuCoolerBlockEntity::new, ModBlocks.GPU_COOLER.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.miner.CoolantCondenserBlockEntity>> COOLANT_CONDENSER = BLOCK_ENTITIES.register("coolant_condenser",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.miner.CoolantCondenserBlockEntity::new, ModBlocks.COOLANT_CONDENSER.get()).build(null));

    public static final Supplier<BlockEntityType<com.lodington.aetheraddon.miner.ToeJamFermenterBlockEntity>> TOE_JAM_FERMENTER = BLOCK_ENTITIES.register("toe_jam_fermenter",
            () -> BlockEntityType.Builder.of(com.lodington.aetheraddon.miner.ToeJamFermenterBlockEntity::new, ModBlocks.TOE_JAM_FERMENTER.get()).build(null));
}
