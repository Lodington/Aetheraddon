package com.lodington.aetheraddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AetherAddon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AETHER_ADDON_TAB =
            CREATIVE_MODE_TABS.register("aetheraddon_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aetheraddon"))
                    .icon(() -> new ItemStack(ModItems.SPUD.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RAW_SPUD.get());
                        output.accept(ModItems.SPUD.get());
                        output.accept(ModItems.SPUDDINGTON.get());
                        output.accept(ModItems.SPUDDING.get());
                        output.accept(ModItems.TOE_JAM.get());
                        output.accept(ModItems.CRUSHED_TOE_JAM.get());
                        output.accept(ModItems.GRIP_PLATE.get());
                        output.accept(ModItems.GRIPTIUM.get());
                        output.accept(ModFluids.MOLTEN_GRIP_BUCKET.get());
                        output.accept(ModItems.GRIPTIUM_PICKAXE.get());
                        output.accept(ModItems.SPUD_FAMILIAR.get());
                        output.accept(ModItems.SPUD_WALLET.get());
                        output.accept(ModBlocks.SPUDDINGTON_BLOCK.get());
                        output.accept(ModBlocks.CHUDINGTON_ORE.get());
                        output.accept(ModBlocks.SPUD_VAULT.get());
                        output.accept(ModBlocks.GRIPTIUM_BLOCK.get());
                        output.accept(ModBlocks.SPUD_MINER.get());
                        output.accept(ModBlocks.GPU_COOLER.get());
                        output.accept(ModBlocks.MINER_MONITOR.get());
                        output.accept(ModBlocks.SPUD_NETWORK_CABLE.get());
                        output.accept(ModBlocks.CLUSTER_CONTROLLER.get());
                        output.accept(ModBlocks.SPUD_MERCHANT.get());
                        output.accept(ModBlocks.COOLANT_CONDENSER.get());
                        output.accept(ModItems.SPUD_GPU.get());
                        output.accept(ModFluids.GRIPPER_COOLANT_BUCKET.get());
                        output.accept(ModFluids.HEATED_COOLANT_BUCKET.get());
                    })
                    .build());
}
