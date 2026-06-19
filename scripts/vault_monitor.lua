-- Spud Vault Monitor
-- Place a computer next to a Spud Vault
-- Optionally connect a monitor on another side
-- Tracks mining activity via spud_vault_changed events

local vault = peripheral.find("spud_vault")
local mon = peripheral.find("monitor")

if not vault then
  print("No Spud Vault found!")
  return
end

if mon then
  mon.setTextScale(1)
  mon.clear()
end

-- Mining stats
local minedTotal = 0
local minedSession = 0
local sessionStart = os.clock()
local lastMineTime = nil
local mineHistory = {} -- timestamps of recent mines for rate calc

local function getMiningRate()
  -- Calculate spud/min over the last 60 seconds
  local now = os.clock()
  local cutoff = now - 60
  -- Prune old entries
  local recent = {}
  for _, t in ipairs(mineHistory) do
    if t > cutoff then
      recent[#recent + 1] = t
    end
  end
  mineHistory = recent
  return #mineHistory -- spud mined in the last 60 seconds = spud/min
end

local function formatUptime()
  local elapsed = os.clock() - sessionStart
  local mins = math.floor(elapsed / 60)
  local secs = math.floor(elapsed % 60)
  return string.format("%dm %ds", mins, secs)
end

local function display()
  local spud = vault.getSpud()
  local spudding = vault.getSpudding()
  local spuddington = vault.getSpuddington()
  local total = vault.getTotal()
  local rate = getMiningRate()

  if mon then
    mon.clear()
    mon.setCursorPos(1, 1)
    mon.setTextColor(colors.yellow)
    mon.write("=== SPUD VAULT ===")
    mon.setCursorPos(1, 3)
    mon.setTextColor(colors.orange)
    mon.write("Spud: " .. spud)
    mon.setCursorPos(1, 4)
    mon.setTextColor(colors.lime)
    mon.write("Spudding: " .. spudding)
    mon.setCursorPos(1, 5)
    mon.setTextColor(colors.yellow)
    mon.write("Spuddington: " .. spuddington)
    mon.setCursorPos(1, 7)
    mon.setTextColor(colors.white)
    mon.write("Total: " .. total)
    mon.setCursorPos(1, 9)
    mon.setTextColor(colors.cyan)
    mon.write("=== MINING STATS ===")
    mon.setCursorPos(1, 10)
    mon.setTextColor(colors.lightGray)
    mon.write("Mined (session): " .. minedSession)
    mon.setCursorPos(1, 11)
    mon.write("Rate: " .. rate .. " spud/min")
    mon.setCursorPos(1, 12)
    mon.write("Uptime: " .. formatUptime())
  else
    term.clear()
    term.setCursorPos(1, 1)
    print("=== SPUD VAULT ===")
    print("")
    print("Spud: " .. spud)
    print("Spudding: " .. spudding)
    print("Spuddington: " .. spuddington)
    print("")
    print("Total: " .. total)
    print("")
    print("=== MINING STATS ===")
    print("Mined (session): " .. minedSession)
    print("Rate: " .. rate .. " spud/min")
    print("Uptime: " .. formatUptime())
  end
end

-- Initial display
display()

-- Listen for changes and track mining
while true do
  local event, type, oldVal, newVal = os.pullEvent("spud_vault_changed")

  -- Track mining: when spud increases, the miner deposited
  if type == "spud" and newVal > oldVal then
    local mined = newVal - oldVal
    minedSession = minedSession + mined
    minedTotal = minedTotal + mined
    lastMineTime = os.clock()
    for i = 1, mined do
      mineHistory[#mineHistory + 1] = os.clock()
    end
  end

  display()
end
