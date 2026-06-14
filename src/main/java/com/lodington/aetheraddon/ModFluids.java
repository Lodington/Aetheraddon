package com.lodington.aetheraddon;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, AetherAddon.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, AetherAddon.MOD_ID);

    // Fluid Type
    public static final Supplier<FluidType> MOLTEN_GRIP_FLUID_TYPE = FLUID_TYPES.register("molten_grip",
            () -> new FluidType(FluidType.Properties.create()
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1300)
                    .canConvertToSource(false)));

    // Source and Flowing
    public static final Supplier<FlowingFluid> MOLTEN_GRIP_SOURCE = FLUIDS.register("molten_grip",
            () -> new BaseFlowingFluid.Source(ModFluids.moltenGripProperties()));
    public static final Supplier<FlowingFluid> MOLTEN_GRIP_FLOWING = FLUIDS.register("molten_grip_flowing",
            () -> new BaseFlowingFluid.Flowing(ModFluids.moltenGripProperties()));

    // Fluid Block
    public static final DeferredBlock<LiquidBlock> MOLTEN_GRIP_BLOCK = ModBlocks.BLOCKS.register("molten_grip_block",
            () -> new LiquidBlock(MOLTEN_GRIP_SOURCE.get(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .replaceable()
                    .noCollission()
                    .strength(100.0f)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()));

    // Bucket Item
    public static final DeferredItem<Item> MOLTEN_GRIP_BUCKET = ModItems.ITEMS.register("molten_grip_bucket",
            () -> new BucketItem(MOLTEN_GRIP_SOURCE.get(),
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static BaseFlowingFluid.Properties moltenGripProperties() {
        return new BaseFlowingFluid.Properties(MOLTEN_GRIP_FLUID_TYPE, MOLTEN_GRIP_SOURCE, MOLTEN_GRIP_FLOWING)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2)
                .block(MOLTEN_GRIP_BLOCK)
                .bucket(MOLTEN_GRIP_BUCKET);
    }
}
