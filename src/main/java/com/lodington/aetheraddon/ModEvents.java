package com.lodington.aetheraddon;

import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = AetherAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SPUD_RAT.get(), SpudRatEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPUD_VAULT.get(),
                (blockEntity, direction) -> new SpudVaultItemHandler(blockEntity)
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SPUD_MINER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CLUSTER_CONTROLLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.GPU_COOLER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );
        // GPU Cooler fluid: back side outputs heated coolant, all other sides input cold coolant
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.GPU_COOLER.get(),
                (blockEntity, direction) -> {
                    if (direction == null) return blockEntity.getInputHandler();
                    Direction facing = blockEntity.getBlockState().getValue(
                            com.lodington.aetheraddon.miner.GpuCoolerBlock.FACING);
                    // Back = the direction the block is facing (front is opposite of placement)
                    return direction == facing.getOpposite()
                            ? blockEntity.getOutputHandler()
                            : blockEntity.getInputHandler();
                }
        );
        // Coolant Condenser fluid: all sides accept heated coolant input and allow cold coolant extraction
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.COOLANT_CONDENSER.get(),
                (blockEntity, direction) -> blockEntity.getFluidHandler()
        );
        // Coolant Condenser energy
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.COOLANT_CONDENSER.get(),
                (blockEntity, direction) -> blockEntity.getEnergyStorage()
        );
    }
}
