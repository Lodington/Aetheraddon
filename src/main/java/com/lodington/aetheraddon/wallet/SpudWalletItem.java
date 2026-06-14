package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.AetherAddon;
import com.lodington.aetheraddon.ModItems;
import com.lodington.aetheraddon.SpudVaultBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class SpudWalletItem extends Item {

    public SpudWalletItem(Properties properties) {
        super(properties);
    }

    // Shift+right-click on a vault to link
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!level.isClientSide() && player != null && player.isShiftKeyDown()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpudVaultBlockEntity) {
                // Link wallet to this vault
                ItemStack stack = context.getItemInHand();
                CompoundTag tag = new CompoundTag();
                tag.putInt("vaultX", pos.getX());
                tag.putInt("vaultY", pos.getY());
                tag.putInt("vaultZ", pos.getZ());
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                player.displayClientMessage(Component.literal("Wallet linked to Spud Vault at " +
                        pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    // Right-click in air to open linked vault remotely
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockPos vaultPos = getLinkedVault(stack);
            if (vaultPos == null) {
                player.displayClientMessage(Component.literal("No vault linked! Shift+right-click a Spud Vault to link."), true);
                return InteractionResultHolder.fail(stack);
            }

            // Check if vault still exists (chunk must be loaded)
            if (!level.isLoaded(vaultPos)) {
                player.displayClientMessage(Component.literal("Vault chunk is not loaded!"), true);
                return InteractionResultHolder.fail(stack);
            }

            BlockEntity be = level.getBlockEntity(vaultPos);
            if (!(be instanceof SpudVaultBlockEntity vault)) {
                player.displayClientMessage(Component.literal("Vault no longer exists at linked position!"), true);
                return InteractionResultHolder.fail(stack);
            }

            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Spud Vault (Remote)");
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
                buf.writeBlockPos(vaultPos);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Nullable
    private BlockPos getLinkedVault(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag tag = customData.copyTag();
        if (!tag.contains("vaultX")) return null;
        return new BlockPos(tag.getInt("vaultX"), tag.getInt("vaultY"), tag.getInt("vaultZ"));
    }

    public static boolean isSpudItem(ItemStack stack) {
        return stack.is(ModItems.SPUD.get()) ||
               stack.is(ModItems.SPUDDING.get()) ||
               stack.is(ModItems.SPUDDINGTON.get());
    }
}
