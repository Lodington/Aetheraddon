package com.lodington.aetheraddon.wallet;

import com.lodington.aetheraddon.AetherAddon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpudWalletScreen extends AbstractContainerScreen<SpudWalletMenu> {

    public SpudWalletScreen(SpudWalletMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 232;
        this.inventoryLabelY = 140;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;

        // Deposit All (top)
        this.addRenderableWidget(Button.builder(Component.literal("Deposit All"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(0));
        }).bounds(x + 8, y + 16, 70, 18).build());

        // Take buttons (right of each label)
        this.addRenderableWidget(Button.builder(Component.literal("Take"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(3));
        }).bounds(x + 120, y + 38, 48, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("Take"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(4));
        }).bounds(x + 120, y + 58, 48, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("Take"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(5));
        }).bounds(x + 120, y + 78, 48, 16).build());

        // Convert buttons (big, below the content area)
        this.addRenderableWidget(Button.builder(Component.literal("8 Spudding \u2192 1 Spuddington"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(1));
        }).bounds(x + 8, y + 96, 160, 18).build());

        this.addRenderableWidget(Button.builder(Component.literal("1 Spuddington \u2192 8 Spudding"), btn -> {
            PacketDistributor.sendToServer(new WalletActionPayload(2));
        }).bounds(x + 8, y + 116, 160, 18).build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Use vanilla inventory texture for the player inventory section
        ResourceLocation INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
        // Draw the bottom half (player inventory) from the vanilla chest texture
        guiGraphics.blit(INVENTORY_LOCATION, x, y + 136, 0, 126, 176, 96);

        // Wallet header/content area (simple dark background above inventory)
        guiGraphics.fill(x, y, x + this.imageWidth, y + 136, 0xFFC6C6C6);
        guiGraphics.fill(x + 3, y + 3, x + this.imageWidth - 3, y + 134, 0xFF373737);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int x = this.leftPos;
        int y = this.topPos;

        // Draw item counts (inside dark area)
        guiGraphics.drawString(this.font, "Spud: " + this.menu.getSpud(), x + 10, y + 41, 0x55FF55, true);
        guiGraphics.drawString(this.font, "Spudding: " + this.menu.getSpudding(), x + 10, y + 61, 0x55FFFF, true);
        guiGraphics.drawString(this.font, "Spuddington: " + this.menu.getSpuddington(), x + 10, y + 81, 0xFFFF55, true);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Spud Wallet", 6, 3, 0xFFFFFF, true);
    }
}
