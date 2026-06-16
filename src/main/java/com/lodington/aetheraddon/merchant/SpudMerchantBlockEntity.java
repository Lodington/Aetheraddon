package com.lodington.aetheraddon.merchant;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
import com.lodington.aetheraddon.wallet.SpudWalletItem;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpudMerchantBlockEntity extends BlockEntity {
    private final List<TradeEntry> trades = new ArrayList<>();
    private final List<IComputerAccess> computers = new ArrayList<>();
    private static final int MAX_TRADES = 9;

    @Nullable
    private BlockPos linkedVaultPos = null;

    public SpudMerchantBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPUD_MERCHANT.get(), pos, state);
    }

    public List<TradeEntry> getTrades() {
        return trades;
    }

    public boolean addTrade(String name, int priceSpud, int priceSpudding, int priceSpuddington) {
        if (trades.size() >= MAX_TRADES) return false;
        trades.add(new TradeEntry(name, priceSpud, priceSpudding, priceSpuddington));
        setChanged();
        syncToClient();
        return true;
    }

    public boolean removeTrade(int index) {
        if (index < 0 || index >= trades.size()) return false;
        trades.remove(index);
        setChanged();
        syncToClient();
        return true;
    }

    public void clearTrades() {
        trades.clear();
        setChanged();
        syncToClient();
    }

    /**
     * Link this merchant to a shop owner's vault.
     */
    public void linkToVault(BlockPos vaultPos) {
        this.linkedVaultPos = vaultPos;
        setChanged();
    }

    @Nullable
    public BlockPos getLinkedVaultPos() {
        return linkedVaultPos;
    }

    /**
     * Attempt a purchase. Deducts from buyer's vault, deposits into owner's vault.
     */
    public boolean tryPurchase(int tradeIndex, Player player) {
        if (tradeIndex < 0 || tradeIndex >= trades.size()) return false;

        TradeEntry trade = trades.get(tradeIndex);

        // Find buyer's vault
        SpudVaultBlockEntity buyerVault = findVaultFromWallet(player);
        if (buyerVault == null) return false;

        // Check if buyer can afford
        if (buyerVault.getSpud() < trade.getPriceSpud()) return false;
        if (buyerVault.getSpudding() < trade.getPriceSpudding()) return false;
        if (buyerVault.getSpuddington() < trade.getPriceSpuddington()) return false;

        // Deduct from buyer
        buyerVault.setSpud(buyerVault.getSpud() - trade.getPriceSpud());
        buyerVault.setSpudding(buyerVault.getSpudding() - trade.getPriceSpudding());
        buyerVault.setSpuddington(buyerVault.getSpuddington() - trade.getPriceSpuddington());

        // Deposit into shop owner's vault (if found)
        SpudVaultBlockEntity ownerVault = findOwnerVault();
        if (ownerVault != null && ownerVault != buyerVault) {
            ownerVault.setSpud(ownerVault.getSpud() + trade.getPriceSpud());
            ownerVault.setSpudding(ownerVault.getSpudding() + trade.getPriceSpudding());
            ownerVault.setSpuddington(ownerVault.getSpuddington() + trade.getPriceSpuddington());
        }

        // Fire CC event
        for (IComputerAccess computer : computers) {
            computer.queueEvent("spud_merchant_purchase",
                    player.getName().getString(),
                    tradeIndex + 1,
                    trade.getName(),
                    trade.getPriceSpud(),
                    trade.getPriceSpudding(),
                    trade.getPriceSpuddington());
        }

        return true;
    }

    /**
     * Find the shop owner's vault - linked first, then adjacent.
     */
    @Nullable
    private SpudVaultBlockEntity findOwnerVault() {
        if (level == null) return null;

        // Check linked vault first
        if (linkedVaultPos != null && level.isLoaded(linkedVaultPos)) {
            BlockEntity be = level.getBlockEntity(linkedVaultPos);
            if (be instanceof SpudVaultBlockEntity vault) {
                return vault;
            }
        }

        // Fallback: adjacent vault
        for (Direction dir : Direction.values()) {
            BlockPos adjacent = getBlockPos().relative(dir);
            BlockEntity be = level.getBlockEntity(adjacent);
            if (be instanceof SpudVaultBlockEntity vault) {
                return vault;
            }
        }

        return null;
    }

    @Nullable
    private SpudVaultBlockEntity findVaultFromWallet(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof SpudWalletItem) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    CompoundTag tag = customData.copyTag();
                    if (tag.contains("vaultX")) {
                        BlockPos vaultPos = new BlockPos(tag.getInt("vaultX"), tag.getInt("vaultY"), tag.getInt("vaultZ"));
                        if (level != null && level.isLoaded(vaultPos)) {
                            BlockEntity be = level.getBlockEntity(vaultPos);
                            if (be instanceof SpudVaultBlockEntity vault) {
                                return vault;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void attachComputer(IComputerAccess computer) {
        computers.add(computer);
    }

    public void detachComputer(IComputerAccess computer) {
        computers.remove(computer);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag tradeList = new ListTag();
        for (TradeEntry trade : trades) {
            tradeList.add(trade.save(registries));
        }
        tag.put("trades", tradeList);
        if (linkedVaultPos != null) {
            tag.putInt("linkedX", linkedVaultPos.getX());
            tag.putInt("linkedY", linkedVaultPos.getY());
            tag.putInt("linkedZ", linkedVaultPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        trades.clear();
        ListTag tradeList = tag.getList("trades", 10);
        for (int i = 0; i < tradeList.size(); i++) {
            trades.add(TradeEntry.load(tradeList.getCompound(i), registries));
        }
        if (tag.contains("linkedX")) {
            linkedVaultPos = new BlockPos(tag.getInt("linkedX"), tag.getInt("linkedY"), tag.getInt("linkedZ"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}
