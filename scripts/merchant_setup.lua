-- Spud Merchant Setup Script
-- Place a computer next to a Spud Merchant block
-- This script configures trades and handles purchases

local shop = peripheral.find("spud_merchant")

if not shop then
  print("No Spud Merchant found!")
  return
end

-- Clear existing trades
shop.clearTrades()

-- Add trades: addTrade(name, priceSpud, priceSpudding, priceSpuddington)
shop.addTrade("Diamond", 50, 0, 0)           -- 50 spud
shop.addTrade("Netherite Ingot", 0, 20, 0)   -- 20 spudding
shop.addTrade("Elytra", 100, 50, 0)          -- 100 spud + 50 spudding
shop.addTrade("God Apple", 0, 0, 5)          -- 5 spuddington
shop.addTrade("VIP Access", 0, 0, 10)        -- 10 spuddington

print("Shop configured with " .. shop.getTradeCount() .. " trades!")
print("")
print("Listening for purchases...")

-- Handle purchases - this is where YOU decide what happens
while true do
  local event, playerName, tradeIdx, tradeName, costSpud, costSpudding, costSpuddington = os.pullEvent("spud_merchant_purchase")
  
  print("[SALE] " .. playerName .. " bought: " .. tradeName)
  print("  Paid: " .. costSpud .. " Spud, " .. costSpudding .. " Spudding, " .. costSpuddington .. " Spuddington")
  
  -- Example: dispense items, open doors, give permissions, etc.
  if tradeName == "Diamond" then
    -- Drop a diamond from a dropper via redstone
    redstone.setOutput("back", true)
    sleep(0.5)
    redstone.setOutput("back", false)
  elseif tradeName == "VIP Access" then
    -- Open an iron door
    redstone.setOutput("left", true)
    sleep(5)
    redstone.setOutput("left", false)
  end
end
