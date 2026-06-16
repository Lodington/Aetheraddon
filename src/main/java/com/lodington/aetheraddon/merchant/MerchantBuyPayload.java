package com.lodington.aetheraddon.merchant;

import com.lodington.aetheraddon.AetherAddon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MerchantBuyPayload(BlockPos pos, int tradeIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MerchantBuyPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "merchant_buy"));

    public static final StreamCodec<FriendlyByteBuf, MerchantBuyPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBlockPos(payload.pos);
                        buf.writeInt(payload.tradeIndex);
                    },
                    buf -> new MerchantBuyPayload(buf.readBlockPos(), buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
