package com.lodington.aetheraddon;

import com.lodington.aetheraddon.miner.SpudMinerMenu;
import com.lodington.aetheraddon.wallet.SpudWalletMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AetherAddon.MOD_ID);

    public static final Supplier<MenuType<SpudWalletMenu>> SPUD_WALLET_MENU = MENUS.register("spud_wallet",
            () -> IMenuTypeExtension.create(SpudWalletMenu::new));

    public static final Supplier<MenuType<SpudMinerMenu>> SPUD_MINER_MENU = MENUS.register("spud_miner",
            () -> IMenuTypeExtension.create(SpudMinerMenu::new));

    public static final Supplier<MenuType<com.lodington.aetheraddon.merchant.SpudMerchantMenu>> SPUD_MERCHANT_MENU = MENUS.register("spud_merchant",
            () -> IMenuTypeExtension.create(com.lodington.aetheraddon.merchant.SpudMerchantMenu::new));
}
