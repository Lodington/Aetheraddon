package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.AetherAddon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = AetherAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class WalletNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(AetherAddon.MOD_ID);
        registrar.playToServer(WalletActionPayload.TYPE, WalletActionPayload.STREAM_CODEC,
                WalletNetworking::handleAction);
    }

    private static void handleAction(WalletActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.containerMenu instanceof SpudWalletMenu menu) {
                switch (payload.action()) {
                    case 0 -> menu.depositAll(player);
                    case 1 -> menu.convertUp();
                    case 2 -> menu.convertDown();
                    case 3 -> {
                        ItemStack withdrawn = menu.withdrawSpud(64);
                        if (!withdrawn.isEmpty()) player.getInventory().placeItemBackInInventory(withdrawn);
                    }
                    case 4 -> {
                        ItemStack withdrawn = menu.withdrawSpudding(64);
                        if (!withdrawn.isEmpty()) player.getInventory().placeItemBackInInventory(withdrawn);
                    }
                    case 5 -> {
                        ItemStack withdrawn = menu.withdrawSpuddington(64);
                        if (!withdrawn.isEmpty()) player.getInventory().placeItemBackInInventory(withdrawn);
                    }
                }
            }
        });
    }
}
