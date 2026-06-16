package com.lodington.aetheraddon;

import com.lodington.aetheraddon.miner.SpudMinerScreen;
import com.lodington.aetheraddon.wallet.SpudWalletScreen;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AetherAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWN_SPUD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SPUD_RAT.get(), SpudRatRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.SPUD_WALLET_MENU.get(), SpudWalletScreen::new);
        event.register(ModMenuTypes.SPUD_MINER_MENU.get(), SpudMinerScreen::new);
        event.register(ModMenuTypes.SPUD_MERCHANT_MENU.get(), com.lodington.aetheraddon.merchant.SpudMerchantScreen::new);
    }

    @SubscribeEvent
    public static void registerFluidRendering(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            @Override
            public net.minecraft.resources.ResourceLocation getStillTexture() {
                return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "fluid/texturestill");
            }

            @Override
            public net.minecraft.resources.ResourceLocation getFlowingTexture() {
                return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AetherAddon.MOD_ID, "fluid/textureflow");
            }

            @Override
            public int getTintColor() {
                return 0xFFFFFFFF; // No tint, use texture as-is
            }
        }, ModFluids.MOLTEN_GRIP_FLUID_TYPE.get());
    }
}
