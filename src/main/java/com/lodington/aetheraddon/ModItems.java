package com.lodington.aetheraddon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AetherAddon.MOD_ID);

    public static final DeferredItem<Item> RAW_SPUD = ITEMS.register("raw_spud",
            () -> new RawSpudItem(new Item.Properties()));
    public static final DeferredItem<Item> SPUD = ITEMS.registerSimpleItem("spud", new Item.Properties()
            .food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.6f)
                    .effect(() -> new MobEffectInstance(ModEffects.GRIPPER_FEET, 200, 0), 1.0f)
                    .alwaysEdible()
                    .fast()
                    .build()));
    public static final DeferredItem<Item> SPUDDINGTON = ITEMS.register("spuddington",
            () -> new SpuddingtonItem(new Item.Properties()
            .food(new FoodProperties.Builder()
                    .nutrition(10)
                    .saturationModifier(1.2f)
                    .effect(() -> new MobEffectInstance(ModEffects.GRIPPER_FEET, 1800, 2), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 1), 1.0f)
                    .alwaysEdible()
                    .build())));
    public static final DeferredItem<Item> SPUDDING = ITEMS.registerSimpleItem("spudding", new Item.Properties());
    public static final DeferredItem<Item> TOE_JAM = ITEMS.registerSimpleItem("toe_jam", new Item.Properties());
    public static final DeferredItem<Item> CRUSHED_TOE_JAM = ITEMS.registerSimpleItem("crushed_toe_jam", new Item.Properties());
    public static final DeferredItem<Item> GRIP_PLATE = ITEMS.registerSimpleItem("grip_plate", new Item.Properties());
    public static final DeferredItem<Item> GRIPTIUM = ITEMS.registerSimpleItem("griptium", new Item.Properties());
    public static final DeferredItem<Item> GRIPTIUM_PICKAXE = ITEMS.register("griptium_pickaxe",
            () -> new GriptiumPickaxe(new Item.Properties().durability(256).stacksTo(1)));
    public static final DeferredItem<Item> SPUD_FAMILIAR = ITEMS.register("spud_familiar",
            () -> new SpudFamiliarItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SPUD_WALLET = ITEMS.register("spud_wallet",
            () -> new com.lodington.aetheraddon.wallet.SpudWalletItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SPUD_GPU = ITEMS.registerSimpleItem("spud_gpu", new Item.Properties().stacksTo(16));
}
