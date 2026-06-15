package com.lodington.aetheraddon;

import com.lodington.aetheraddon.wallet.SpudWalletMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

public class SpudVaultBlock extends BaseEntityBlock {

    public static final MapCodec<SpudVaultBlock> CODEC = simpleCodec(SpudVaultBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SpudVaultBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpudVaultBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudVaultBlockEntity vault) {
                // Drop the vault block itself
                Block.popResource(level, pos, new ItemStack(ModBlocks.SPUD_VAULT.get()));

                // Drop all stored currency
                int spud = vault.getSpud();
                while (spud > 0) {
                    int drop = Math.min(spud, 64);
                    Block.popResource(level, pos, new ItemStack(ModItems.SPUD.get(), drop));
                    spud -= drop;
                }

                int spudding = vault.getSpudding();
                while (spudding > 0) {
                    int drop = Math.min(spudding, 64);
                    Block.popResource(level, pos, new ItemStack(ModItems.SPUDDING.get(), drop));
                    spudding -= drop;
                }

                int spuddington = vault.getSpuddington();
                while (spuddington > 0) {
                    int drop = Math.min(spuddington, 64);
                    Block.popResource(level, pos, new ItemStack(ModItems.SPUDDINGTON.get(), drop));
                    spuddington -= drop;
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudVaultBlockEntity vault) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("Spud Vault");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player p) {
                        return new SpudWalletMenu(containerId, inventory, vault);
                    }
                }, buf -> {
                    buf.writeInt(vault.getSpud());
                    buf.writeInt(vault.getSpudding());
                    buf.writeInt(vault.getSpuddington());
                    buf.writeBlockPos(pos);
                });
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudVaultBlockEntity vault) {
                // Deposit held spud items directly
                if (stack.is(ModItems.SPUD.get())) {
                    vault.setSpud(vault.getSpud() + stack.getCount());
                    stack.setCount(0);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                } else if (stack.is(ModItems.SPUDDING.get())) {
                    vault.setSpudding(vault.getSpudding() + stack.getCount());
                    stack.setCount(0);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                } else if (stack.is(ModItems.SPUDDINGTON.get())) {
                    vault.setSpuddington(vault.getSpuddington() + stack.getCount());
                    stack.setCount(0);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                }
            }
        }
        // Non-spud item or client: pass through to open GUI via useWithoutItem
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
