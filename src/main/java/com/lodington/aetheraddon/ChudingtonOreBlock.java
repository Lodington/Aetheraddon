package com.lodington.aetheraddon;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public class ChudingtonOreBlock extends Block {

    public ChudingtonOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float baseSpeed = super.getDestroyProgress(state, player, level, pos);
        ItemStack tool = player.getMainHandItem();
        // If holding a pickaxe, mine significantly faster
        if (tool.getItem() instanceof PickaxeItem) {
            return baseSpeed * 4.0f;
        }
        return baseSpeed;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        // Default: drop 1-2 raw spud
        int count = 1 + builder.getLevel().getRandom().nextInt(2);
        drops.add(new ItemStack(ModItems.RAW_SPUD.get(), count));
        return drops;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @javax.annotation.Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                              ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!level.isClientSide() && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            // Drop experience (2-5 XP)
            int xp = 2 + level.getRandom().nextInt(4);
            this.popExperience(serverLevel, pos, xp);

            // 10% chance to spawn a spud rat when mined
            if (level.getRandom().nextFloat() < 0.1f) {
                SpudRatEntity rat = ModEntities.SPUD_RAT.get().create(serverLevel);
                if (rat != null) {
                    rat.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.getRandom().nextFloat() * 360f, 0f);
                    rat.setTarget(player);
                    serverLevel.addFreshEntity(rat);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative()) {
            ItemStack tool = player.getMainHandItem();

            // Grant "You've Been Chudded" advancement if mining with bare fist
            if (tool.isEmpty() && player instanceof ServerPlayer serverPlayer) {
                AdvancementHolder advancement = serverPlayer.server.getAdvancements()
                        .get(ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "youve_been_chudded"));
                if (advancement != null) {
                    AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                    if (!progress.isDone()) {
                        for (String criterion : progress.getRemainingCriteria()) {
                            serverPlayer.getAdvancements().award(advancement, criterion);
                        }
                    }
                }
            }

            // Check for silk touch
            if (EnchantmentHelper.getItemEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(Enchantments.SILK_TOUCH), tool) > 0) {
                Block.popResource(level, pos, new ItemStack(ModBlocks.CHUDINGTON_ORE.get()));
            } else if (tool.getItem() instanceof GriptiumPickaxe) {
                // Griptium Pickaxe gives 50% more raw spud
                int count = 1 + level.getRandom().nextInt(2);
                // 50% chance for an extra drop
                if (level.getRandom().nextFloat() < 0.5f) {
                    count += 1;
                }
                Block.popResource(level, pos, new ItemStack(ModItems.RAW_SPUD.get(), count));
            } else {
                // Drop 1-2 raw spud
                int count = 1 + level.getRandom().nextInt(2);

                // Check fortune level and add bonus
                int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(
                        level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.FORTUNE), tool);
                if (fortuneLevel > 0) {
                    int bonus = level.getRandom().nextInt(fortuneLevel + 1);
                    count += bonus;
                }

                Block.popResource(level, pos, new ItemStack(ModItems.RAW_SPUD.get(), count));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
