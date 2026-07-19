package com.lodington.aetheraddon.autocraft;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.*;

/**
 * CC:Tweaked peripheral for the Spud Autocrafter.
 *
 * The autocrafter consumes ingredients from adjacent inventories server-side.
 * Place chests/barrels directly next to the autocrafter block.
 *
 * The peripheral provides:
 *   - Recipe lookup (search, getRecipe, getIngredients, findRecipeForItem)
 *   - countItem() — check stock in adjacent storage
 *   - craft() — consumes ingredients and produces the result (secure, server-side)
 *
 * Usage from Lua:
 *   1. crafter.getIngredients(recipeId) — see what's needed
 *   2. crafter.countItem(itemId) — check if stock is available
 *   3. crafter.craft(recipeId, count) — consumes items from adjacent storage, produces result
 */
public class SpudAutocrafterPeripheral implements IPeripheral {
    private final SpudAutocrafterBlockEntity crafter;
    private IComputerAccess computer;

    public SpudAutocrafterPeripheral(SpudAutocrafterBlockEntity crafter) {
        this.crafter = crafter;
    }

    @Override
    public String getType() {
        return "spud_autocrafter";
    }

    @Override
    public void attach(IComputerAccess computer) {
        this.computer = computer;
    }

    @Override
    public void detach(IComputerAccess computer) {
        this.computer = null;
    }

    /** List all crafting recipe IDs available. */
    @LuaFunction
    public final List<String> listRecipes() {
        return crafter.listRecipes();
    }

    /** Search recipes by partial name match. */
    @LuaFunction
    public final List<String> searchRecipes(String query) {
        return crafter.searchRecipes(query);
    }

    /** Get full details of a recipe including ingredients and result. */
    @LuaFunction
    public final Map<String, Object> getRecipe(String recipeId) throws LuaException {
        try {
            var result = crafter.getRecipe(recipeId);
            if (result.isEmpty()) throw new LuaException("Recipe not found: " + recipeId);
            return result;
        } catch (LuaException e) {
            throw e;
        } catch (Exception e) {
            throw new LuaException("Error: " + e.getMessage());
        }
    }

    /**
     * Get consolidated ingredients for a recipe.
     * Uses reflection fallback for modded recipe types.
     */
    @LuaFunction
    @SuppressWarnings("unchecked")
    public final List<Map<String, Object>> getIngredients(String recipeId) throws LuaException {
        try {
            var recipeData = crafter.getRecipe(recipeId);
            if (recipeData.isEmpty()) throw new LuaException("Recipe not found: " + recipeId);
            Object ings = recipeData.get("ingredients");
            if (ings instanceof List<?> list) {
                return (List<Map<String, Object>>) (List<?>) list;
            }
            return List.of();
        } catch (LuaException e) {
            throw e;
        } catch (Exception e) {
            throw new LuaException("Error: " + e.getMessage());
        }
    }

    /**
     * Find the recipe ID that produces a given item.
     */
    @LuaFunction
    public final Object findRecipeForItem(String itemId) {
        return crafter.findRecipeForItem(itemId);
    }

    /**
     * Count how many of an item are in adjacent storage.
     */
    @LuaFunction
    public final int countItem(String itemId) {
        return crafter.countItemInStorage(itemId);
    }

    /**
     * Craft a recipe. Consumes ingredients from adjacent inventories server-side
     * and places the result in the output slot. Secure — no free crafting.
     *
     * @param recipeId recipe to craft
     * @param count how many times to craft (capped at 64)
     */
    @LuaFunction
    public final Map<String, Object> craft(String recipeId, int count) throws LuaException {
        if (!(crafter.getLevel() instanceof ServerLevel))
            throw new LuaException("Not on server");

        if (count < 1) count = 1;
        if (count > 64) count = 64;

        var result = crafter.craft(recipeId, count);

        Map<String, Object> info = new HashMap<>();
        info.put("success", result.success());
        info.put("message", result.message());
        if (result.item() != null) {
            info.put("item", result.item());
            info.put("count", result.count());
        }
        return info;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof SpudAutocrafterPeripheral p && p.crafter == this.crafter;
    }
}
