package com.lodington.aetheraddon.miner;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpudMinerPeripheral implements IPeripheral {
    private final SpudMinerBlockEntity miner;
    private final List<IComputerAccess> computers = new ArrayList<>();

    public SpudMinerPeripheral(SpudMinerBlockEntity miner) {
        this.miner = miner;
    }

    @Override
    public String getType() {
        return "spud_miner";
    }

    /**
     * Get the number of installed GPUs.
     */
    @LuaFunction
    public final int getGPUCount() {
        return miner.getInstalledGPUs();
    }

    /**
     * Get the total spud mined since placement.
     */
    @LuaFunction
    public final int getTotalMined() {
        return miner.getGeneratedSpud();
    }

    /**
     * Get the current energy stored in FE.
     */
    @LuaFunction
    public final int getEnergy() {
        return miner.getEnergyStorage().getEnergyStored();
    }

    /**
     * Get the max energy capacity in FE.
     */
    @LuaFunction
    public final int getMaxEnergy() {
        return miner.getEnergyStorage().getMaxEnergyStored();
    }

    /**
     * Get the current energy consumption per tick in FE.
     * Accounts for cooler discounts.
     */
    @LuaFunction
    public final int getEnergyUsage() {
        int gpus = miner.getInstalledGPUs();
        if (gpus == 0) return 0;
        int base = SpudMinerBlockEntity.getEnergyConsumption(gpus);
        int coolers = 0;
        if (miner.getLevel() != null) {
            coolers = GpuCoolerBlock.countCoolersAdjacent(miner.getLevel(), miner.getBlockPos());
        }
        return (int) (base * (1.0 - coolers * 0.2));
    }

    /**
     * Get the number of active coolers adjacent to this miner.
     */
    @LuaFunction
    public final int getCoolerCount() {
        if (miner.getLevel() == null) return 0;
        return GpuCoolerBlock.countCoolersAdjacent(miner.getLevel(), miner.getBlockPos());
    }

    /**
     * Get the hash rate in spuds per minute.
     */
    @LuaFunction
    public final double getHashRate() {
        int gpus = miner.getInstalledGPUs();
        if (gpus == 0) return 0.0;
        int ticksPerSpud = 600 / gpus;
        return 1200.0 / ticksPerSpud;
    }

    /**
     * Check if the miner is actively mining (has GPUs and enough energy).
     */
    @LuaFunction
    public final boolean isMining() {
        int gpus = miner.getInstalledGPUs();
        if (gpus == 0) return false;
        int energyNeeded = getEnergyUsage();
        return miner.getEnergyStorage().getEnergyStored() >= energyNeeded;
    }

    /**
     * Get a full status table with all miner info.
     */
    @LuaFunction
    public final Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("gpuCount", getGPUCount());
        status.put("totalMined", getTotalMined());
        status.put("energy", getEnergy());
        status.put("maxEnergy", getMaxEnergy());
        status.put("energyUsage", getEnergyUsage());
        status.put("coolerCount", getCoolerCount());
        status.put("hashRate", getHashRate());
        status.put("mining", isMining());
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
        return other instanceof SpudMinerPeripheral p && p.miner == this.miner;
    }
}
