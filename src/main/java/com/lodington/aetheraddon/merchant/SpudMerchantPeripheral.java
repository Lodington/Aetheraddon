package com.lodington.aetheraddon.merchant;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SpudMerchantPeripheral implements IPeripheral {
    private final SpudMerchantBlockEntity merchant;

    public SpudMerchantPeripheral(SpudMerchantBlockEntity merchant) {
        this.merchant = merchant;
    }

    @Override
    public String getType() {
        return "spud_merchant";
    }

    @LuaFunction
    public final int getTradeCount() {
        return merchant.getTrades().size();
    }

    @LuaFunction
    public final Map<String, Object> getTrade(int index) throws LuaException {
        int i = index - 1;
        if (i < 0 || i >= merchant.getTrades().size()) throw new LuaException("Invalid trade index");
        TradeEntry trade = merchant.getTrades().get(i);
        Map<String, Object> result = new HashMap<>();
        result.put("name", trade.getName());
        result.put("priceSpud", trade.getPriceSpud());
        result.put("priceSpudding", trade.getPriceSpudding());
        result.put("priceSpuddington", trade.getPriceSpuddington());
        return result;
    }

    @LuaFunction
    public final boolean addTrade(String name, int priceSpud, int priceSpudding, int priceSpuddington) {
        return merchant.addTrade(name, priceSpud, priceSpudding, priceSpuddington);
    }

    @LuaFunction
    public final boolean removeTrade(int index) {
        return merchant.removeTrade(index - 1);
    }

    @LuaFunction
    public final void clearTrades() {
        merchant.clearTrades();
    }

    @Override
    public void attach(IComputerAccess computer) {
        merchant.attachComputer(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        merchant.detachComputer(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof SpudMerchantPeripheral p && p.merchant == this.merchant;
    }
}
