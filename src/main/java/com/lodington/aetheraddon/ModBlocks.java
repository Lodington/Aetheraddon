package com.lodington.aetheraddon;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AetherAddon.MOD_ID);

    public static final DeferredBlock<Block> SPUDDINGTON_BLOCK = registerBlockWithCustomItem("spuddington_block",
            () -> new SpuddingtonBlock(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> CHUDINGTON_ORE = registerBlock("chudington_ore",
            () -> new ChudingtonOreBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)));

    public static final DeferredBlock<Block> SPUD_VAULT = registerBlock("spud_vault",
            () -> new SpudVaultBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 5)));

    public static final DeferredBlock<Block> GRIPTIUM_BLOCK = registerBlock("griptium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0f, 6.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 3)));

    public static final DeferredBlock<Block> SPUD_MINER = registerBlock("spud_miner",
            () -> new SpudMinerBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 2)));

    public static final DeferredBlock<Block> GPU_COOLER = registerBlock("gpu_cooler",
            () -> new com.lodington.aetheraddon.miner.GpuCoolerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.COPPER)));

    public static final DeferredBlock<Block> MINER_MONITOR = registerBlock("miner_monitor",
            () -> new com.lodington.aetheraddon.miner.MinerMonitorBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 4)));

    public static final DeferredBlock<Block> SPUD_NETWORK_CABLE = registerBlock("spud_network_cable",
            () -> new com.lodington.aetheraddon.miner.SpudNetworkCableBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.COPPER)));

    public static final DeferredBlock<Block> CLUSTER_CONTROLLER = registerBlock("cluster_controller",
            () -> new com.lodington.aetheraddon.miner.ClusterControllerBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 6)));

    public static final DeferredBlock<Block> SPUD_MERCHANT = registerBlock("spud_merchant",
            () -> new com.lodington.aetheraddon.merchant.SpudMerchantBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 3)));

    public static final DeferredBlock<Block> SPUD_AUTOCRAFTER = registerBlock("spud_autocrafter",
            () -> new com.lodington.aetheraddon.autocraft.SpudAutocrafterBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 4)));

    public static final DeferredBlock<Block> COOLANT_CONDENSER = registerBlock("coolant_condenser",
            () -> new com.lodington.aetheraddon.miner.CoolantCondenserBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> 2)));

    public static final DeferredBlock<Block> TOE_JAM_FERMENTER = registerBlock("toe_jam_fermenter",
            () -> new com.lodington.aetheraddon.miner.ToeJamFermenterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 1)));

    private static <T extends Block> DeferredBlock<T> registerBlockWithCustomItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new SpuddingtonBlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
