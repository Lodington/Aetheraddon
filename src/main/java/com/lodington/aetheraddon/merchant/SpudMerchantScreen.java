package com.lodington.aetheraddon.merchant;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SpudMerchantScreen extends AbstractContainerScreen<SpudMerchantMenu> {
    private static final ResourceLocation INVENTORY_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public SpudMerchantScreen(SpudMerchantMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;

        List<TradeEntry> trades = this.menu.getTrades();
        for (int i = 0; i < trades.size() && i < 9; i++) {
            final int tradeIndex = i;
            int btnY = y + 18 + (i * 12);
            this.addRenderableWidget(Button.builder(Component.literal("Buy"), btn -> {
                PacketDistributor.sendToServer(new MerchantBuyPayload(this.menu.getMerchantPos(), tradeIndex));
            }).bounds(x + 140, btnY, 30, 11).build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Player inventory
        guiGraphics.blit(INVENTORY_LOCATION, x, y + 126, 0, 126, 176, 96);

        // Shop area
        guiGraphics.fill(x, y, x + this.imageWidth, y + 126, 0xFFC6C6C6);
        guiGraphics.fill(x + 3, y + 14, x + this.imageWidth - 3, y + 123, 0xFF3A3A3A);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = this.leftPos;
        int y = this.topPos;

        List<TradeEntry> trades = this.menu.getTrades();
        for (int i = 0; i < trades.size() && i < 9; i++) {
            TradeEntry trade = trades.get(i);
            int textY = y + 19 + (i * 12);

            // Trade name
            guiGraphics.drawString(this.font, trade.getName(), x + 8, textY, 0x55FF55, true);
            // Price (small, below name offset to the right)
            guiGraphics.drawString(this.font, trade.getPriceString(), x + 8, textY + 4, 0xAAAAAA, false);
        }

        if (trades.isEmpty()) {
            guiGraphics.drawString(this.font, "No trades configured", x + 30, y + 60, 0xFF5555, true);
            guiGraphics.drawString(this.font, "Set up via ComputerCraft", x + 25, y + 75, 0xAAAAAA, true);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Spud Merchant", 6, 4, 0xFFFFFF, true);
    }
}
