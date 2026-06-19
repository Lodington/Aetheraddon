package com.lodington.aetheraddon.miner;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoolantCondenserPeripheral implements IPeripheral {
    private final CoolantCondenserBlockEntity condenser;
    private final List<IComputerAccess> computers = new ArrayList<>();

    public CoolantCondenserPeripheral(CoolantCondenserBlockEntity condenser) {
        this.condenser = condenser;
    }

    @Override
    public String getType() {
        return "coolant_condenser";
    }

    /**
     * Get the amount of heated coolant in the input tank (mB).
     */
    @LuaFunction
    public final int getInputAmount() {
        return condenser.getInputAmount();
    }

    /**
     * Get the amount of cooled gripper coolant in the output tank (mB).
     */
    @LuaFunction
    public final int getOutputAmount() {
        return condenser.getOutputAmount();
    }

    /**
     * Get the tank capacity for each tank (mB).
     */
    @LuaFunction
    public final int getTankCapacity() {
        return condenser.getTankCapacity();
    }

    /**
     * Check if the condenser is currently processing.
     */
    @LuaFunction
    public final boolean isProcessing() {
        return condenser.isProcessing();
    }

    /**
     * Get the current energy stored in FE.
     */
    @LuaFunction
    public final int getEnergy() {
        return condenser.getEnergyStored();
    }

    /**
     * Get the max energy capacity in FE.
     */
    @LuaFunction
    public final int getMaxEnergy() {
        return condenser.getMaxEnergy();
    }

    /**
     * Get energy consumption per tick while processing (FE/t).
     */
    @LuaFunction
    public final int getEnergyUsage() {
        return CoolantCondenserBlockEntity.ENERGY_PER_TICK;
    }

    /**
     * Get the amount of coolant processed per cycle (mB).
     */
    @LuaFunction
    public final int getProcessAmount() {
        return CoolantCondenserBlockEntity.PROCESS_AMOUNT;
    }

    /**
     * Get the number of ticks between each process cycle.
     */
    @LuaFunction
    public final int getProcessInterval() {
        return CoolantCondenserBlockEntity.PROCESS_INTERVAL;
    }

    /**
     * Get a full status table with all condenser info.
     */
    @LuaFunction
    public final Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("inputAmount", getInputAmount());
        status.put("outputAmount", getOutputAmount());
        status.put("tankCapacity", getTankCapacity());
        status.put("processing", isProcessing());
        status.put("energy", getEnergy());
        status.put("maxEnergy", getMaxEnergy());
        status.put("energyUsage", getEnergyUsage());
        status.put("processAmount", getProcessAmount());
        status.put("processInterval", getProcessInterval());
        return status;
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof CoolantCondenserPeripheral p && p.condenser == this.condenser;
    }
}
