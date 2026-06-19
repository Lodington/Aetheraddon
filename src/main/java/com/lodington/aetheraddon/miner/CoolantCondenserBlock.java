package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

public class CoolantCondenserBlock extends BaseEntityBlock {
    public static final MapCodec<CoolantCondenserBlock> CODEC = simpleCodec(CoolantCondenserBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public CoolantCondenserBlock(Properties properties) {
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
        return new CoolantCondenserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.COOLANT_CONDENSER.get(), CoolantCondenserBlockEntity::serverTick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CoolantCondenserBlockEntity condenser) {
                String status;
                if (condenser.getEnergyStored() < CoolantCondenserBlockEntity.ENERGY_PER_TICK) {
                    status = "§cNo Power!";
                } else if (condenser.isProcessing()) {
                    status = "§aCondensing";
                } else {
                    status = "§7Idle";
                }
                player.displayClientMessage(Component.literal(
                        "§b══ COOLANT CONDENSER ══\n" +
                        "§7Status: " + status + "\n" +
                        "§7Energy: §e" + condenser.getEnergyStored() / 1000 + "k/" + condenser.getMaxEnergy() / 1000 + "k FE §7(" + CoolantCondenserBlockEntity.ENERGY_PER_TICK + " FE/t)\n" +
                        "§7Hot In: §c" + condenser.getInputAmount() + "/" + condenser.getTankCapacity() + " mB\n" +
                        "§7Cold Out: §d" + condenser.getOutputAmount() + "/" + condenser.getTankCapacity() + " mB"
                ), false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
