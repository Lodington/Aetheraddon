-- Spud Vault Monitor
-- Place a computer next to a Spud Vault
-- Optionally connect a monitor on another side

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

local function display()
  local spud = vault.getSpud()
  local spudding = vault.getSpudding()
  local spuddington = vault.getSpuddington()
  local total = vault.getTotal()

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
  end
end

-- Initial display
display()

-- Listen for changes and refresh
while true do
  local event = os.pullEvent("spud_vault_changed")
  display()
end
