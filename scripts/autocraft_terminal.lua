-- Autocraft Terminal (Pocket Computer Client)
-- Connects wirelessly to the autocraft server.
-- Run this on a pocket computer with a wireless modem.

local PROTOCOL = "spud_autocraft"

-- Open wireless modem
for _, side in ipairs({"top", "bottom", "left", "right", "front", "back"}) do
  if peripheral.getType(side) == "modem" then
    rednet.open(side)
  end
end

-- Find server
local serverId = rednet.lookup(PROTOCOL, "autocraft_server")
if not serverId then
  print("Searching for autocraft server...")
  sleep(2)
  serverId = rednet.lookup(PROTOCOL, "autocraft_server")
end
if not serverId then
  print("ERROR: No autocraft server found!")
  print("Make sure the server is running.")
  return
end
print("Connected to server #" .. serverId)
sleep(0.5)

-- Helper: send and receive with timeout
local function request(msg, timeout)
  rednet.send(serverId, msg, PROTOCOL)
  local _, reply = rednet.receive(PROTOCOL, timeout or 5)
  return reply
end

-- UI functions
local function clearScreen()
  term.clear()
  term.setCursorPos(1, 1)
end

local function header(text)
  term.setTextColor(colors.yellow)
  print(text)
  term.setTextColor(colors.white)
  print(string.rep("-", #text))
end

-- Craft a specific recipe
local function craftItem(recipeId)
  clearScreen()
  header("CRAFT")
  print("")

  -- Get recipe details
  local reply = request({ action = "get_recipe", id = recipeId })
  if not reply or not reply.recipe or not reply.recipe.result then
    term.setTextColor(colors.red)
    print("Recipe not found!")
    sleep(2)
    return
  end

  local recipe = reply.recipe
  term.setTextColor(colors.yellow)
  print("Item: " .. recipe.result.name)
  print("Yields: " .. recipe.result.count .. " per craft")
  print("")

  -- Show ingredients with stock
  term.setTextColor(colors.white)
  print("Ingredients:")
  if recipe.ingredients then
    for _, ing in ipairs(recipe.ingredients) do
      local name = ing.item:match(":(.+)$") or ing.item
      name = name:gsub("_", " ")

      -- Check stock
      local stockReply = request({ action = "check_stock", item = ing.item }, 3)
      local available = (stockReply and stockReply.count) or "?"

      local color = colors.lime
      if type(available) == "number" and available < ing.count then
        color = colors.red
      end
      term.setTextColor(color)
      print("  " .. ing.count .. "x " .. name .. " [" .. tostring(available) .. "]")
    end
  end

  print("")
  term.setTextColor(colors.white)
  write("Amount (1): ")
  local countStr = read()
  local count = tonumber(countStr) or 1
  if count < 1 then return end

  term.setTextColor(colors.gray)
  print("Crafting " .. count .. "x ...")

  local craftReply = request({ action = "craft", id = recipeId, count = count }, 10)
  print("")
  if craftReply and craftReply.result and craftReply.result.success then
    term.setTextColor(colors.lime)
    print(craftReply.result.message)
  else
    term.setTextColor(colors.red)
    local msg = (craftReply and craftReply.result and craftReply.result.message) or "No response"
    print("FAILED: " .. msg)
  end

  print("")
  term.setTextColor(colors.gray)
  print("[Enter] to continue")
  read()
end

-- Search and browse
local function searchMenu()
  clearScreen()
  header("SEARCH RECIPES")
  print("")
  write("Search: ")
  local query = read()
  if query == "" then return end

  term.setTextColor(colors.gray)
  print("Searching...")

  local reply = request({ action = "search", query = query })
  if not reply or not reply.results then
    print("No response from server!")
    sleep(2)
    return
  end

  local results = reply.results
  clearScreen()
  header("'" .. query .. "' (" .. #results .. " found)")
  print("")

  local maxShow = math.min(#results, 12)
  for i = 1, maxShow do
    local id = results[i]
    local short = id:match(":(.+)$") or id
    short = short:gsub("_", " ")
    term.setTextColor(colors.lime)
    write(i .. ". ")
    term.setTextColor(colors.white)
    print(short)
  end

  if #results > 12 then
    term.setTextColor(colors.gray)
    print("... +" .. (#results - 12) .. " more")
  end

  print("")
  term.setTextColor(colors.cyan)
  print("# to craft, Enter to back")
  write("> ")
  local pick = read()

  local num = tonumber(pick)
  if num and num >= 1 and num <= maxShow then
    craftItem(results[num])
  end
end

-- System info
local function storageInfo()
  clearScreen()
  header("SYSTEM INFO")
  print("")
  local reply = request({ action = "storage_info" }, 3)
  if reply then
    print("Storage: " .. (reply.storage or "unknown"))
    print("Output:  " .. (reply.output or "unknown"))
  else
    print("No response from server")
  end
  print("")
  term.setTextColor(colors.gray)
  print("[Enter] to continue")
  read()
end

-- Factory Gauges - mimics Create's Factory Gauge: set a target stock level
-- for an item and the server keeps it topped off automatically.
local function gaugeMenu()
  clearScreen()
  header("FACTORY GAUGES")
  print("")

  local reply = request({ action = "list_gauges" }, 3)
  local list = (reply and reply.gauges) or {}

  if #list == 0 then
    term.setTextColor(colors.gray)
    print("No gauges set.")
  else
    for i, g in ipairs(list) do
      local short = g.item:match(":(.+)$") or g.item
      short = short:gsub("_", " ")
      local color = g.have < g.target and colors.red or colors.lime
      term.setTextColor(colors.white)
      write(i .. ". " .. short .. " ")
      term.setTextColor(color)
      print(g.have .. "/" .. g.target)
    end
  end

  print("")
  term.setTextColor(colors.cyan)
  print("A: add/update gauge  R: remove gauge  Enter: back")
  write("> ")
  local choice = read()

  if choice == "a" or choice == "A" then
    clearScreen()
    header("ADD GAUGE")
    print("")
    write("Item ID (e.g. minecraft:oak_planks): ")
    local itemId = read()
    if itemId == "" then return end

    write("Target stock amount: ")
    local targetStr = read()
    local target = tonumber(targetStr)
    if not target or target <= 0 then
      term.setTextColor(colors.red)
      print("Invalid amount.")
      sleep(2)
      return
    end

    local setReply = request({ action = "set_gauge", item = itemId, target = target }, 3)
    if setReply and setReply.success then
      term.setTextColor(colors.lime)
      print("Gauge set: keep " .. target .. "x " .. itemId .. " in stock.")
    else
      term.setTextColor(colors.red)
      print("Failed to set gauge (no response from server).")
    end
    sleep(2)

  elseif choice == "r" or choice == "R" then
    clearScreen()
    header("REMOVE GAUGE")
    print("")
    write("Item ID to remove: ")
    local itemId = read()
    if itemId == "" then return end

    request({ action = "set_gauge", item = itemId, target = 0 }, 3)
    term.setTextColor(colors.lime)
    print("Gauge removed for " .. itemId .. ".")
    sleep(1.5)
  end
end

-- Main menu
local function mainMenu()
  while true do
    clearScreen()
    header("AUTOCRAFT TERMINAL")
    print("")
    print("1. Search recipes")
    print("2. Craft by ID")
    print("3. System info")
    print("4. Factory gauges")
    print("5. Exit")
    print("")
    write("> ")
    local choice = read()

    if choice == "1" then
      searchMenu()
    elseif choice == "2" then
      clearScreen()
      header("CRAFT BY ID")
      print("")
      write("Recipe ID: ")
      local id = read()
      if id ~= "" then craftItem(id) end
    elseif choice == "3" then
      storageInfo()
    elseif choice == "4" then
      gaugeMenu()
    elseif choice == "5" then
      return
    end
  end
end

mainMenu()
