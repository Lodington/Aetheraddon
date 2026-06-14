package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.AetherAddon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class WalletDataAttachment {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AetherAddon.MOD_ID);

    public static final Supplier<AttachmentType<WalletPlayerData>> WALLET_DATA = ATTACHMENTS.register("wallet_data",
            () -> AttachmentType.builder(WalletPlayerData::new)
                    .serialize(WalletPlayerData.CODEC)
                    .copyOnDeath()
                    .build());

    public static class WalletPlayerData {
        public static final Codec<WalletPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("spud").forGetter(d -> d.spud),
                Codec.INT.fieldOf("spudding").forGetter(d -> d.spudding),
                Codec.INT.fieldOf("spuddington").forGetter(d -> d.spuddington)
        ).apply(instance, WalletPlayerData::new));

        public int spud;
        public int spudding;
        public int spuddington;

        public WalletPlayerData() {
            this(0, 0, 0);
        }

        public WalletPlayerData(int spud, int spudding, int spuddington) {
            this.spud = spud;
            this.spudding = spudding;
            this.spuddington = spuddington;
        }
    }
}
