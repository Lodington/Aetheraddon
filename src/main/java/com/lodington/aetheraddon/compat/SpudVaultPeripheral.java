package com.lodington.aetheraddon.compat;

import com.lodington.aetheraddon.SpudVaultBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpudVaultPeripheral implements IPeripheral {
    private final SpudVaultBlockEntity vault;
    private final List<IComputerAccess> computers = new ArrayList<>();

    public SpudVaultPeripheral(SpudVaultBlockEntity vault) {
        this.vault = vault;
        vault.setPeripheral(this);
    }

    @Override
    public String getType() {
        return "spud_vault";
    }

    @LuaFunction
    public final int getSpud() {
        return vault.getSpud();
    }

    @LuaFunction
    public final int getSpudding() {
        return vault.getSpudding();
    }

    @LuaFunction
    public final int getSpuddington() {
        return vault.getSpuddington();
    }

    @LuaFunction
    public final int getTotal() {
        return vault.getSpud() + vault.getSpudding() + vault.getSpuddington();
    }

    @LuaFunction
    public final void addSpud(int amount) {
        vault.setSpud(vault.getSpud() + amount);
    }

    @LuaFunction
    public final void addSpudding(int amount) {
        vault.setSpudding(vault.getSpudding() + amount);
    }

    @LuaFunction
    public final void addSpuddington(int amount) {
        vault.setSpuddington(vault.getSpuddington() + amount);
    }

    @LuaFunction
    public final int depositFrom(IComputerAccess computer, String sourceName) throws dan200.computercraft.api.lua.LuaException {
        // Pull all spud items from a source inventory into the vault
        // Returns total items deposited
        var source = computer.getAvailablePeripheral(sourceName);
        if (source == null) throw new dan200.computercraft.api.lua.LuaException("Peripheral not found: " + sourceName);
        // We can't directly access the inventory from here in CC's API
        // Instead, the Lua script should handle counting and call addSpud/etc
        return 0;
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    public void fireChangedEvent(String type, int oldValue, int newValue) {
        for (IComputerAccess computer : computers) {
            computer.queueEvent("spud_vault_changed", type, oldValue, newValue);
        }
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof SpudVaultPeripheral p && p.vault == this.vault;
    }
}
