-- Spud Vault + Create Vault Monitor Display
-- 
-- SETUP: Point the hopper physically INTO the spud vault block.
-- Our mod's IItemHandler makes vanilla hoppers deposit spuds automatically.
-- This script just displays the contents on the monitor.
--
-- Peripherals (all on wired modem network):
--   create:item_vault_1 (Create vault - display contents)
--   monitor_1 (display)
--   spud_vault_0 (our spud vault - display spud currency)

local createVault = peripheral.wrap("create:item_vault_1")
local mon = peripheral.wrap("monitor_1")
local spudVault = peripheral.wrap("spud_vault_0")

if not createVault then print("ERROR: create:item_vault_1 not found!") return end
if not mon then print("ERROR: monitor_1 not found!") return end
if not spudVault then print("ERROR: spud_vault_0 not found!") return end

mon.setTextScale(1.5)

-- Display vault contents on monitor
local function displayVault()
  mon.clear()
  mon.setCursorPos(1, 1)
  mon.setTextColor(colors.yellow)
  mon.write("=== SPUD VAULT ===")

  local maxW, maxH = mon.getSize()
  local line = 3

  -- Spud currency display
  mon.setCursorPos(1, line)
  mon.setTextColor(colors.orange)
  mon.write("Spud: " .. spudVault.getSpud())
  line = line + 1

  mon.setCursorPos(1, line)
  mon.setTextColor(colors.cyan)
  mon.write("Spudding: " .. spudVault.getSpudding())
  line = line + 1

  mon.setCursorPos(1, line)
  mon.setTextColor(colors.yellow)
  mon.write("Spuddington: " .. spudVault.getSpuddington())
  line = line + 1

  mon.setCursorPos(1, line)
  mon.setTextColor(colors.white)
  mon.write("Total: " .. spudVault.getTotal())
  line = line + 2

  -- Create vault items
  mon.setCursorPos(1, line)
  mon.setTextColor(colors.yellow)
  mon.write("=== ITEM VAULT ===")
  line = line + 1

  local items = createVault.list()

  -- Aggregate items by name
  local totals = {}
  for slot, item in pairs(items) do
    local name = item.displayName or item.name
    -- Strip namespace prefix (everything before and including the colon)
    name = name:match(":(.+)$") or name
    -- Replace underscores with spaces and capitalise first letter
    name = name:gsub("_", " ")
    name = name:sub(1,1):upper() .. name:sub(2)
    if totals[name] then
      totals[name] = totals[name] + item.count
    else
      totals[name] = item.count
    end
  end

  -- Sort by count descending
  local sorted = {}
  for name, count in pairs(totals) do
    table.insert(sorted, {name = name, count = count})
  end
  table.sort(sorted, function(a, b) return a.count > b.count end)

  -- Display items
  for _, entry in ipairs(sorted) do
    if line > maxH then break end
    mon.setCursorPos(1, line)
    mon.setTextColor(colors.white)
    local displayName = entry.name
    if #displayName > maxW - 8 then
      displayName = displayName:sub(1, maxW - 11) .. "..."
    end
    mon.write(displayName)
    mon.setCursorPos(maxW - #tostring(entry.count), line)
    mon.setTextColor(colors.lime)
    mon.write(tostring(entry.count))
    line = line + 1
  end
end

-- Main loop
print("Monitoring spud vault + item vault...")
print("Moving spud items from hopper to create vault...")
print("Press Ctrl+T to stop")

-- Move spud items from hopper to create vault
local function transferItems()
  local hopper = peripheral.wrap("minecraft:hopper_0")
  if not hopper then return end
  local items = hopper.list()
  for slot, item in pairs(items) do
    local name = item.name
    if name == "aetheraddon:spud" or name == "aetheraddon:spudding" or name == "aetheraddon:spuddington" then
      hopper.pushItems("create:item_vault_1", slot)
    end
  end
end

while true do
  transferItems()
  displayVault()
  sleep(2)
end
