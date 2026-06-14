package com.lodington.aetheraddon;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class SpudLootModifier extends LootModifier {

    public static final MapCodec<SpudLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, SpudLootModifier::new));

    public SpudLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Only apply to chest-type loot (has at least some items already, which indicates a container)
        if (generatedLoot.isEmpty()) return generatedLoot;

        var random = context.getRandom();

        // Common tier: Raw Spud (15% chance, 1-3)
        if (random.nextFloat() < 0.15f) {
            generatedLoot.add(new ItemStack(ModItems.RAW_SPUD.get(), 1 + random.nextInt(3)));
        }

        // Rare tier: Spud (8% chance, 1-2)
        if (random.nextFloat() < 0.08f) {
            generatedLoot.add(new ItemStack(ModItems.SPUD.get(), 1 + random.nextInt(2)));
        }

        // Rare tier: Toe Jam (6% chance, 1-2)
        if (random.nextFloat() < 0.06f) {
            generatedLoot.add(new ItemStack(ModItems.TOE_JAM.get(), 1 + random.nextInt(2)));
        }

        // Very rare: Spudding (3% chance)
        if (random.nextFloat() < 0.03f) {
            generatedLoot.add(new ItemStack(ModItems.SPUDDING.get(), 1));
        }

        // Very rare: Griptium (3% chance)
        if (random.nextFloat() < 0.03f) {
            generatedLoot.add(new ItemStack(ModItems.GRIPTIUM.get(), 1 + random.nextInt(2)));
        }

        // Super rare: Spuddington (1% chance)
        if (random.nextFloat() < 0.01f) {
            generatedLoot.add(new ItemStack(ModItems.SPUDDINGTON.get(), 1));
        }

        // Super rare: Griptium Pickaxe (0.5% chance)
        if (random.nextFloat() < 0.005f) {
            generatedLoot.add(new ItemStack(ModItems.GRIPTIUM_PICKAXE.get(), 1));
        }

        // Ultra rare: Spud Familiar (0.2% chance)
        if (random.nextFloat() < 0.002f) {
            generatedLoot.add(new ItemStack(ModItems.SPUD_FAMILIAR.get(), 1));
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
