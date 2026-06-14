package com.lodington.aetheraddon.compat;

import com.lodington.aetheraddon.AetherAddon;
import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = AetherAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CCCompat {

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!ModList.get().isLoaded("computercraft")) return;
        try {
            BlockCapability<IPeripheral, Direction> peripheralCap = (BlockCapability<IPeripheral, Direction>)
                    BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath("computercraft", "peripheral"), IPeripheral.class);
            event.registerBlockEntity(
                    peripheralCap,
                    ModBlockEntities.SPUD_VAULT.get(),
                    (blockEntity, direction) -> new SpudVaultPeripheral(blockEntity)
            );
        } catch (Exception ignored) {
            // CC:Tweaked not available or API mismatch
        }
    }
}
