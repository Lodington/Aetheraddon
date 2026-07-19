-- Autocraft Server
-- The autocrafter consumes items from adjacent storage SERVER-SIDE.
-- Place chests/barrels DIRECTLY NEXT TO the autocrafter block (touching it).
-- The CC network is used for the computer to talk to the crafter peripheral,
-- but items are consumed from physically adjacent inventories by the Java code.
--
-- SETUP:
--   - Computer with wireless modem (for pocket terminal) + wired modem
--   - Spud Autocrafter with wired modem (for CC peripheral access)
--   - Storage (chest, barrel, etc.) placed DIRECTLY NEXT TO the autocrafter
--   - Connect computer to autocrafter via networking cable

local PROTOCOL = "spud_autocraft"

-- Find the crafter peripheral
local crafter = peripheral.find("spud_autocrafter")
if not crafter then
  for _, name in ipairs(peripheral.getNames()) do
    if name:find("spud_autocrafter") then
      local p = peripheral.wrap(name)
      if p and p.craft then
        crafter = p
        break
      end
    end
  end
end
if not crafter then
  print("ERROR: no 'spud_autocrafter' peripheral found!")
  return
end

-- Open all modems
for _, side in ipairs({"top", "bottom", "left", "right", "front", "back"}) do
  if peripheral.getType(side) == "modem" then rednet.open(side) end
end

rednet.host(PROTOCOL, "autocraft_server")
print("")
print("=== Autocraft Server ===")
print("Storage must be adjacent to the autocrafter block.")
print("Waiting for requests...")
print("")

--- Check stock via the crafter (reads adjacent inventories server-side)
local function checkStock(itemId)
  local ok, count = pcall(crafter.countItem, itemId)
  if ok then return count end
  return 0
end

--- Craft a recipe (crafter handles ingredient consumption server-side)
local function handleCraft(recipeId, count)
  count = math.max(1, math.min(count or 1, 64))
  local ok, result = pcall(crafter.craft, recipeId, count)
  if not ok then
    return { success = false, message = "Craft error: " .. tostring(result) }
  end
  return result
end

--- Craft by item ID (resolves recipe automatically)
local function handleCraftItem(itemId, count)
  local recipeId = crafter.findRecipeForItem(itemId)
  if not recipeId then
    return { success = false, message = "No recipe produces " .. itemId }
  end
  return handleCraft(recipeId, count)
end

-- === Factory Gauges ===
local GAUGES_FILE = "autocraft_gauges.txt"
local GAUGE_CHECK_INTERVAL = 5

local gauges = {}

local function loadGauges()
  if not fs.exists(GAUGES_FILE) then return end
  local f = fs.open(GAUGES_FILE, "r")
  if not f then return end
  local data = f.readAll()
  f.close()
  local ok, decoded = pcall(textutils.unserialize, data)
  if ok and type(decoded) == "table" then
    gauges = decoded
  end
end

local function saveGauges()
  local f = fs.open(GAUGES_FILE, "w")
  if not f then return end
  f.write(textutils.serialize(gauges))
  f.close()
end

loadGauges()

local function setGauge(itemId, targetAmount)
  if not targetAmount or targetAmount <= 0 then
    gauges[itemId] = nil
  else
    gauges[itemId] = targetAmount
  end
  saveGauges()
end

local function checkGauges()
  for itemId, target in pairs(gauges) do
    local have = checkStock(itemId)
    if have < target then
      local recipeId = crafter.findRecipeForItem(itemId)
      if recipeId then
        local ok, recipe = pcall(crafter.getRecipe, recipeId)
        local yieldPerCraft = (ok and recipe and recipe.result and recipe.result.count) or 1
        local shortfall = target - have
        local batches = math.ceil(shortfall / math.max(yieldPerCraft, 1))
        batches = math.min(batches, 64)

        local result = handleCraft(recipeId, batches)
        if result.success then
          print("[GAUGE] " .. itemId .. ": " .. result.message)
        end
      end
    end
  end
end

local function gaugeLoop()
  while true do
    sleep(GAUGE_CHECK_INTERVAL)
    pcall(checkGauges)
  end
end

local function requestLoop()
  while true do
    local senderId, msg = rednet.receive(PROTOCOL)

    if type(msg) == "table" then
      if msg.action == "search" then
        local ok, results = pcall(crafter.searchRecipes, msg.query or "")
        rednet.send(senderId, { action = "search_result", results = ok and results or {} }, PROTOCOL)

      elseif msg.action == "get_recipe" then
        local ok, recipe = pcall(crafter.getRecipe, msg.id)
        rednet.send(senderId, { action = "recipe_result", recipe = ok and recipe or {} }, PROTOCOL)

      elseif msg.action == "check_stock" then
        local count = checkStock(msg.item or "")
        rednet.send(senderId, { action = "stock_result", item = msg.item, count = count }, PROTOCOL)

      elseif msg.action == "craft" then
        local craftResult = handleCraft(msg.id, msg.count or 1)
        rednet.send(senderId, { action = "craft_result", result = craftResult }, PROTOCOL)
        if craftResult.success then
          print("[CRAFT] " .. (craftResult.message or ""))
        else
          print("[FAIL]  " .. (craftResult.message or "unknown error"))
        end

      elseif msg.action == "craft_item" then
        local craftResult = handleCraftItem(msg.item, msg.count or 1)
        rednet.send(senderId, { action = "craft_result", result = craftResult }, PROTOCOL)
        if craftResult.success then
          print("[CRAFT] " .. (craftResult.message or ""))
        else
          print("[FAIL]  " .. (craftResult.message or "unknown error"))
        end

      elseif msg.action == "list" then
        local ok, recipes = pcall(crafter.listRecipes)
        rednet.send(senderId, { action = "list_result", count = ok and #recipes or 0 }, PROTOCOL)

      elseif msg.action == "set_gauge" then
        setGauge(msg.item, msg.target)
        rednet.send(senderId, { action = "gauge_result", success = true }, PROTOCOL)
        if msg.target and msg.target > 0 then
          print("[GAUGE] Set " .. msg.item .. " -> keep " .. msg.target .. " in stock")
        else
          print("[GAUGE] Removed gauge for " .. msg.item)
        end

      elseif msg.action == "list_gauges" then
        local list = {}
        for itemId, target in pairs(gauges) do
          local have = checkStock(itemId)
          table.insert(list, { item = itemId, target = target, have = have })
        end
        rednet.send(senderId, { action = "gauges_result", gauges = list }, PROTOCOL)
      end
    end
  end
end

print("Gauges loaded: " .. (function() local n=0 for _ in pairs(gauges) do n=n+1 end return n end)())
print("")

parallel.waitForAny(requestLoop, gaugeLoop)
