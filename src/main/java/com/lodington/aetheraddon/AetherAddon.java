package com.lodington.aetheraddon;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(AetherAddon.MOD_ID)
public class AetherAddon {
    public static final String MOD_ID = "aetheraddon";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AetherAddon(IEventBus modEventBus) {
        LOGGER.info("Spud Mod initializing...");

        // Force ModFluids to load first so block/bucket register into BLOCKS/ITEMS
        var ignored = ModFluids.MOLTEN_GRIP_FLUID_TYPE;

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
