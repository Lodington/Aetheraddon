package com.lodington.aetheraddon.miner;

import com.lodington.aetheraddon.AetherAddon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SpudMinerScreen extends AbstractContainerScreen<SpudMinerMenu> {
    private static final ResourceLocation INVENTORY_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public SpudMinerScreen(SpudMinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 170;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Use vanilla inventory texture for player inv (starts at y+70)
        guiGraphics.blit(INVENTORY_LOCATION, x, y + 70, 0, 126, 176, 96);

        // Top section background
        guiGraphics.fill(x, y, x + this.imageWidth, y + 70, 0xFFC6C6C6);
        guiGraphics.fill(x + 3, y + 3, x + this.imageWidth - 3, y + 67, 0xFF3A3A3A);

        // GPU slot backgrounds (2 rows of 5)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int slotX = x + 43 + col * 18;
                int slotY = y + 19 + row * 18;
                guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF555555);
            }
        }

        // Energy bar background
        int barX = x + 8;
        int barY = y + 18;
        int barWidth = 28;
        int barHeight = 36;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF222222);

        // Energy bar fill
        int energy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int fillHeight = (int) ((float) energy / maxEnergy * barHeight);
            guiGraphics.fill(barX + 1, barY + barHeight - fillHeight, barX + barWidth - 1, barY + barHeight, 0xFFFF4444);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = this.leftPos;
        int y = this.topPos;

        // Energy text
        int energy = this.menu.getEnergy();
        int maxEnergy = this.menu.getMaxEnergy();
        String energyText = energy / 1000 + "k/" + maxEnergy / 1000 + "k";
        guiGraphics.drawString(this.font, energyText, x + 9, y + 58, 0xFF5555, true);

        // Consumption
        guiGraphics.drawString(this.font, this.menu.getConsumption() + " FE/t",
                x + 9, y + 68, 0xFFAA00, true);

        // Total mined
        guiGraphics.drawString(this.font, "Mined: " + this.menu.getTotalMined(),
                x + 134, y + 58, 0xFFFFFF, true);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Spud Miner", 44, 7, 0xFFFFFF, true);
        guiGraphics.drawString(this.font, "FE", 17, 8, 0xFF5555, true);
    }
}
