package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ToeJamFermenterBlock extends BaseEntityBlock {
    public static final MapCodec<ToeJamFermenterBlock> CODEC = simpleCodec(ToeJamFermenterBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public ToeJamFermenterBlock(Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToeJamFermenterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.TOE_JAM_FERMENTER.get(), ToeJamFermenterBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ToeJamFermenterBlockEntity fermenter) {
                // Try to insert a raw spud from hand
                ItemStack held = player.getMainHandItem();
                if (!held.isEmpty() && held.is(ModItems.RAW_SPUD.get())) {
                    int fuelSlots = fermenter.getFuelCount();
                    if (fuelSlots < ToeJamFermenterBlockEntity.MAX_FUEL) {
                        fermenter.addFuel(1);
                        held.shrink(1);
                        player.displayClientMessage(Component.literal("§aFed 1 Raw Spud to the fermenter"), true);
                        return InteractionResult.SUCCESS;
                    }
                }

                // Show status
                String status = fermenter.isFermenting() ? "§aFermenting" : "§7Idle";
                int progress = fermenter.getProgress();
                int maxProgress = ToeJamFermenterBlockEntity.FERMENT_TIME;
                int percent = maxProgress > 0 ? (progress * 100) / maxProgress : 0;

                player.displayClientMessage(Component.literal(
                        "§6══ TOE JAM FERMENTER ══\n" +
                        "§7Status: " + status + "\n" +
                        "§7Fuel: §f" + fermenter.getFuelCount() + "/" + ToeJamFermenterBlockEntity.MAX_FUEL + " Raw Spuds\n" +
                        "§7Progress: §e" + percent + "%\n" +
                        "§7Output: §f" + fermenter.getOutputCount() + "/" + ToeJamFermenterBlockEntity.MAX_OUTPUT + " Toe Jam"
                ), false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
