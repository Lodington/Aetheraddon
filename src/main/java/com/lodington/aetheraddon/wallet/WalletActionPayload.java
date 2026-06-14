package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.AetherAddon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WalletActionPayload(int action) implements CustomPacketPayload {
    // Actions: 0=deposit, 1=convertUp, 2=convertDown, 3=withdrawSpud, 4=withdrawSpudding, 5=withdrawSpuddington

    public static final CustomPacketPayload.Type<WalletActionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "wallet_action"));

    public static final StreamCodec<FriendlyByteBuf, WalletActionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, WalletActionPayload::action, WalletActionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
