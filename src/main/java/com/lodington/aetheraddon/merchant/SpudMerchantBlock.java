package com.lodington.aetheraddon.merchant;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModItems;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
import com.lodington.aetheraddon.wallet.SpudWalletItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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

import javax.annotation.Nullable;
import java.util.List;

public class SpudMerchantBlock extends BaseEntityBlock {
    public static final MapCodec<SpudMerchantBlock> CODEC = simpleCodec(SpudMerchantBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SpudMerchantBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpudMerchantBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudMerchantBlockEntity merchant) {
                List<TradeEntry> trades = merchant.getTrades();
                if (trades.isEmpty()) {
                    player.displayClientMessage(Component.literal("§cNo trades available! Set up via ComputerCraft."), true);
                } else {
                    // Show available trades
                    StringBuilder msg = new StringBuilder("§6═══ SPUD MERCHANT ═══\n");
                    for (int i = 0; i < trades.size(); i++) {
                        TradeEntry trade = trades.get(i);
                        msg.append("§f").append(i + 1).append(". §a")
                                .append(trade.getName())
                                .append(" §7- §e").append(trade.getPriceString())
                                .append("\n");
                    }
                    msg.append("§7Use wallet + sneak + right-click to buy (slot 1)");
                    player.displayClientMessage(Component.literal(msg.toString()), false);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && stack.getItem() instanceof SpudWalletItem) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudMerchantBlockEntity merchant) {
                // Shift+wallet = link shop owner's vault to this merchant
                if (player.isShiftKeyDown()) {
                    net.minecraft.core.component.DataComponents dc = null;
                    var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                    if (customData != null) {
                        var tag = customData.copyTag();
                        if (tag.contains("vaultX")) {
                            BlockPos vaultPos = new BlockPos(tag.getInt("vaultX"), tag.getInt("vaultY"), tag.getInt("vaultZ"));
                            merchant.linkToVault(vaultPos);
                            player.displayClientMessage(Component.literal("§aMerchant linked to your vault!"), true);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }
                    player.displayClientMessage(Component.literal("§cWallet has no vault linked!"), true);
                    return ItemInteractionResult.SUCCESS;
                }

                // Normal wallet right-click = open shop GUI
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new net.minecraft.world.MenuProvider() {
                        @Override
                        public net.minecraft.network.chat.Component getDisplayName() {
                            return Component.literal("Spud Merchant");
                        }

                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory, Player p) {
                            return new SpudMerchantMenu(containerId, inventory, merchant);
                        }
                    }, buf -> buf.writeBlockPos(pos));
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
