package com.lodington.aetheraddon.autocraft;

import com.lodington.aetheraddon.ModBlockEntities;
import com.lodington.aetheraddon.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;

/**
 * Block entity for the Spud Autocrafter.
 *
 * The autocrafter scans ALL physically adjacent inventories (chests, barrels, etc.)
 * and consumes ingredients from them server-side. This prevents free crafting.
 *
 * Place storage directly next to (touching) the autocrafter block.
 * The crafter checks every adjacent block for an item handler capability.
 */
public class SpudAutocrafterBlockEntity extends BlockEntity {

    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };

    public SpudAutocrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPUD_AUTOCRAFTER.get(), pos, state);
    }

    public ItemStackHandler getOutputSlot() { return outputSlot; }

    // ==================== Recipe Lookup ====================

    /** Get all recipe IDs across all recipe types. */
    public List<String> listRecipes() {
        if (!(level instanceof ServerLevel serverLevel)) return List.of();
        List<String> ids = new ArrayList<>();
        for (var holder : serverLevel.getRecipeManager().getRecipes()) {
            ids.add(holder.id().toString());
        }
        return ids;
    }

    /** Search recipes by partial name match across all recipe types. */
    public List<String> searchRecipes(String query) {
        String lower = query.toLowerCase();
        List<String> results = new ArrayList<>();
        if (!(level instanceof ServerLevel serverLevel)) return results;
        for (var holder : serverLevel.getRecipeManager().getRecipes()) {
            if (holder.id().toString().toLowerCase().contains(lower)) {
                results.add(holder.id().toString());
            }
        }
        return results;
    }

    /** Get recipe details including ingredients, result, and type. */
    public Map<String, Object> getRecipe(String recipeId) {
        if (!(level instanceof ServerLevel serverLevel)) return Map.of();
        var optional = serverLevel.getRecipeManager().byKey(ResourceLocation.parse(recipeId));
        if (optional.isEmpty()) return Map.of();

        var holder = optional.get();
        var recipe = holder.value();
        Map<String, Object> result = new HashMap<>();

        // Recipe type
        String type = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()).toString();
        result.put("type", type);

        ItemStack resultStack = recipe.getResultItem(serverLevel.registryAccess());
        Map<String, Object> resultInfo = new HashMap<>();
        resultInfo.put("item", BuiltInRegistries.ITEM.getKey(resultStack.getItem()).toString());
        resultInfo.put("count", resultStack.getCount());
        resultInfo.put("name", resultStack.getHoverName().getString());
        result.put("result", resultInfo);

        // Try to extract ingredients - multiple strategies for different recipe types
        Map<String, Integer> consolidated = new LinkedHashMap<>();

        // Strategy 1: Standard getIngredients()
        try {
            var ingList = recipe.getIngredients();
            if (ingList != null) {
                for (Ingredient ing : ingList) {
                    if (ing == null || ing.isEmpty()) continue;
                    try {
                        ItemStack[] stacks = ing.getItems();
                        if (stacks != null && stacks.length > 0) {
                            String itemId = BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).toString();
                            consolidated.merge(itemId, 1, Integer::sum);
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        // Strategy 2: If standard method returned nothing, try reflection on common fields
        if (consolidated.isEmpty()) {
            consolidated = extractIngredientsViaReflection(recipe);
        }

        List<Map<String, Object>> ingredients = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : consolidated.entrySet()) {
            Map<String, Object> ingInfo = new HashMap<>();
            ingInfo.put("item", entry.getKey());
            ingInfo.put("count", entry.getValue());
            ingredients.add(ingInfo);
        }
        result.put("ingredients", ingredients);

        return result;
    }

    /**
     * Try to extract ingredients from modded recipes via reflection.
     * Checks common field names and types used by mods like Ars Nouveau, Create, etc.
     */
    private Map<String, Integer> extractIngredientsViaReflection(Recipe<?> recipe) {
        Map<String, Integer> consolidated = new LinkedHashMap<>();
        try {
            for (var field : recipe.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(recipe);

                // Check for List<Ingredient>
                if (value instanceof List<?> list && !list.isEmpty()) {
                    for (Object item : list) {
                        if (item instanceof Ingredient ing && !ing.isEmpty()) {
                            try {
                                ItemStack[] stacks = ing.getItems();
                                if (stacks != null && stacks.length > 0) {
                                    String itemId = BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).toString();
                                    consolidated.merge(itemId, 1, Integer::sum);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }

                // Check for single Ingredient field (like "reagent")
                if (value instanceof Ingredient ing && !ing.isEmpty()) {
                    try {
                        ItemStack[] stacks = ing.getItems();
                        if (stacks != null && stacks.length > 0) {
                            String itemId = BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).toString();
                            consolidated.merge(itemId, 1, Integer::sum);
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return consolidated;
    }

    /**
     * Find the first recipe whose result matches the given item ID (any recipe type).
     */
    public String findRecipeForItem(String itemId) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        for (var holder : serverLevel.getRecipeManager().getRecipes()) {
            try {
                ItemStack result = holder.value().getResultItem(serverLevel.registryAccess());
                if (result.isEmpty()) continue;
                String resultId = BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
                if (resultId.equals(itemId)) return holder.id().toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    // ==================== Inventory Access ====================

    /**
     * Find all adjacent inventories (item handlers) the crafter can access.
     * Storage must be physically next to the autocrafter block.
     */
    private List<IItemHandler> findAdjacentInventories() {
        List<IItemHandler> handlers = new ArrayList<>();
        if (level == null) return handlers;

        for (Direction dir : Direction.values()) {
            BlockPos adjacent = worldPosition.relative(dir);
            BlockState adjState = level.getBlockState(adjacent);
            // Don't treat another autocrafter as storage
            if (adjState.is(ModBlocks.SPUD_AUTOCRAFTER.get())) continue;

            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, adjacent, dir.getOpposite());
            if (handler != null) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    /**
     * Count how many of a specific item exist across adjacent inventories.
     */
    public int countItemInStorage(String itemId) {
        int total = 0;
        for (IItemHandler handler : findAdjacentInventories()) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    if (id.equals(itemId)) {
                        total += stack.getCount();
                    }
                }
            }
        }
        return total;
    }

    // ==================== Crafting ====================

    /**
     * Craft a recipe by consuming ingredients from adjacent inventories.
     * Items are consumed server-side to prevent exploits.
     *
     * @param recipeId recipe to craft
     * @param count how many times to craft
     */
    public CraftResult craft(String recipeId, int count) {
        if (!(level instanceof ServerLevel serverLevel))
            return new CraftResult(false, "Not on server", null, 0);
        if (count < 1) count = 1;
        if (count > 64) count = 64;

        var optional = serverLevel.getRecipeManager().byKey(ResourceLocation.parse(recipeId));
        if (optional.isEmpty()) return new CraftResult(false, "Recipe not found", null, 0);

        var recipe = optional.get().value();

        ItemStack resultStack = recipe.getResultItem(serverLevel.registryAccess());
        if (resultStack.isEmpty()) return new CraftResult(false, "Recipe has no result", null, 0);

        // Check output slot capacity
        ItemStack existingOutput = outputSlot.getStackInSlot(0);
        int outputRoom;
        if (existingOutput.isEmpty()) {
            outputRoom = 64;
        } else if (ItemStack.isSameItemSameComponents(existingOutput, resultStack)) {
            outputRoom = 64 - existingOutput.getCount();
        } else {
            return new CraftResult(false, "Output slot occupied by a different item", null, 0);
        }
        int maxByOutput = resultStack.getCount() > 0 ? outputRoom / resultStack.getCount() : 0;
        count = Math.min(count, maxByOutput);
        if (count <= 0) return new CraftResult(false, "Output slot full", null, 0);

        // Extract ingredients — try standard method first, fallback to reflection
        Map<String, Integer> neededPerCraft = new LinkedHashMap<>();
        try {
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients != null) {
                for (Ingredient ing : ingredients) {
                    if (ing == null || ing.isEmpty()) continue;
                    try {
                        ItemStack[] stacks = ing.getItems();
                        if (stacks != null && stacks.length > 0) {
                            String itemId = BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).toString();
                            neededPerCraft.merge(itemId, 1, Integer::sum);
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        // Fallback: reflection for modded recipe types (Ars Nouveau, Create, etc.)
        if (neededPerCraft.isEmpty()) {
            neededPerCraft = extractIngredientsViaReflection(recipe);
        }

        if (neededPerCraft.isEmpty()) {
            return new CraftResult(false, "Could not determine ingredients for this recipe", null, 0);
        }

        // Check how many crafts we can do with available stock
        int maxCrafts = count;
        for (Map.Entry<String, Integer> entry : neededPerCraft.entrySet()) {
            int available = countItemInStorage(entry.getKey());
            int canDo = entry.getValue() > 0 ? available / entry.getValue() : 0;
            maxCrafts = Math.min(maxCrafts, canDo);
        }

        if (maxCrafts <= 0) {
            for (Map.Entry<String, Integer> entry : neededPerCraft.entrySet()) {
                int available = countItemInStorage(entry.getKey());
                if (available < entry.getValue()) {
                    return new CraftResult(false,
                            "Missing " + entry.getKey() + " (need " + entry.getValue() + ", have " + available + ")",
                            null, 0);
                }
            }
            return new CraftResult(false, "Missing ingredients", null, 0);
        }

        // Consume ingredients from adjacent inventories
        for (Map.Entry<String, Integer> entry : neededPerCraft.entrySet()) {
            int toConsume = entry.getValue() * maxCrafts;
            if (!consumeFromStorage(entry.getKey(), toConsume)) {
                return new CraftResult(false, "Failed to consume " + entry.getKey(), null, 0);
            }
        }

        // Produce result
        String itemId = BuiltInRegistries.ITEM.getKey(resultStack.getItem()).toString();
        ItemStack toInsert = resultStack.copyWithCount(resultStack.getCount() * maxCrafts);
        ItemStack leftover = outputSlot.insertItem(0, toInsert, false);

        if (!leftover.isEmpty() && level != null) {
            var entity = new net.minecraft.world.entity.item.ItemEntity(
                    level, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1, getBlockPos().getZ() + 0.5, leftover);
            level.addFreshEntity(entity);
        }

        setChanged();
        return new CraftResult(true,
                "Crafted " + maxCrafts + "x " + resultStack.getHoverName().getString(),
                itemId, resultStack.getCount() * maxCrafts);
    }

    /**
     * Remove items from adjacent inventories server-side.
     */
    private boolean consumeFromStorage(String itemId, int amount) {
        int remaining = amount;
        for (IItemHandler handler : findAdjacentInventories()) {
            if (remaining <= 0) break;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                if (remaining <= 0) break;
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.isEmpty()) continue;
                String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                if (!id.equals(itemId)) continue;

                int toExtract = Math.min(remaining, stack.getCount());
                ItemStack extracted = handler.extractItem(slot, toExtract, false);
                remaining -= extracted.getCount();
            }
        }
        return remaining <= 0;
    }

    public record CraftResult(boolean success, String message, String item, int count) {}

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("output", outputSlot.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("output")) outputSlot.deserializeNBT(registries, tag.getCompound("output"));
    }
}
