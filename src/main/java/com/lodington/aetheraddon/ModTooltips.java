package com.lodington.aetheraddon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = AetherAddon.MOD_ID, value = Dist.CLIENT)
public class ModTooltips {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        String key;

        if (stack.getItem() instanceof BlockItem blockItem) {
            key = blockItem.getBlock().getDescriptionId() + ".tooltip";
        } else {
            key = stack.getDescriptionId() + ".tooltip";
        }

        MutableComponent tooltip = Component.translatable(key);
        // Only add if the translation exists (doesn't return the key itself)
        String translated = tooltip.getString();
        if (!translated.equals(key)) {
            event.getToolTip().add(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
