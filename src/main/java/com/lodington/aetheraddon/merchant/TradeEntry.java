package com.lodington.aetheraddon.merchant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

public class TradeEntry {
    private String name;
    private int priceSpud;
    private int priceSpudding;
    private int priceSpuddington;

    public TradeEntry(String name, int priceSpud, int priceSpudding, int priceSpuddington) {
        this.name = name;
        this.priceSpud = priceSpud;
        this.priceSpudding = priceSpudding;
        this.priceSpuddington = priceSpuddington;
    }

    public String getName() { return name; }
    public int getPriceSpud() { return priceSpud; }
    public int getPriceSpudding() { return priceSpudding; }
    public int getPriceSpuddington() { return priceSpuddington; }

    public String getPriceString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (priceSpud > 0) { sb.append(priceSpud).append(" Spud"); first = false; }
        if (priceSpudding > 0) { if (!first) sb.append(" + "); sb.append(priceSpudding).append(" Spudding"); first = false; }
        if (priceSpuddington > 0) { if (!first) sb.append(" + "); sb.append(priceSpuddington).append(" Spuddington"); }
        return sb.toString();
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putInt("priceSpud", priceSpud);
        tag.putInt("priceSpudding", priceSpudding);
        tag.putInt("priceSpuddington", priceSpuddington);
        return tag;
    }

    public static TradeEntry load(CompoundTag tag, HolderLookup.Provider registries) {
        return new TradeEntry(
                tag.getString("name"),
                tag.getInt("priceSpud"),
                tag.getInt("priceSpudding"),
                tag.getInt("priceSpuddington")
        );
    }
}
