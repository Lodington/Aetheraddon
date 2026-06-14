package com.lodington.aetheraddon;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SpudFamiliarItem extends Item {

    public SpudFamiliarItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) return;

        // Only apply every 200 ticks (10 seconds) to avoid spam
        if (player.tickCount % 200 != 0) return;

        // Apply Ars Nouveau mana boost (level 2) and mana regen (level 2)
        applyEffect(player, "ars_nouveau", "mana_regen", 2, 220);
        applyEffect(player, "ars_nouveau", "mana_boost", 2, 220);
    }

    private void applyEffect(Player player, String namespace, String path, int amplifier, int duration) {
        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT
                .getHolder(ResourceLocation.fromNamespaceAndPath(namespace, path));
        effect.ifPresent(holder -> {
            if (!player.hasEffect(holder)) {
                player.addEffect(new MobEffectInstance(holder, duration, amplifier, true, true));
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Enchanted shimmer to make it look special
        return true;
    }
}
