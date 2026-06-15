# Spud Mod

A NeoForge 1.21.1 addon for The Aether mod that adds a full spud-themed progression system, a tameable pet rat, custom fluids, a bitcoin-style mining system, and plenty of meme energy.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.230+
- The Aether (1.5.10+)
- Optional: Create (6.0+) for crushing/pressing/mixing recipes
- Optional: Create Power Grid (or any FE power source) for Spud Miner
- Optional: Ars Nouveau (5.11+) for enchanting apparatus recipes and Spud Familiar
- Optional: CC:Tweaked for Spud Vault peripheral

## Items

| Item | Description |
|------|-------------|
| **Raw Spud** | Dropped from Chudington Ore (2-3, Fortune compatible). Throwable like a snowball, deals 2 damage. |
| **Spud** | Smelted/blasted from Raw Spud. Edible: 3 hunger, Gripper Feet I for 10s, always edible, fast eat. Used to tame Spud Rats. |
| **Spudding** | Made via pressing Raw Spud, or 8 Spud shapeless craft. |
| **Spuddington** | 8 Spudding shapeless, or via Ars Enchanting Apparatus. Super food: 10 hunger, Gripper Feet III (90s), Resistance II (90s), Regen II (30s). Slow eat time. |
| **Toe Jam** | Dropped by wild Spud Rats (1-2). First step in the Griptium chain. |
| **Crushed Toe Jam** | Made by crushing Toe Jam in Create Crushing Wheels. |
| **Grip Plate** | Made by superheated compacting Molten Grip Fluid. |
| **Griptium** | Made via Ars Enchanting Apparatus (Grip Plate + 4 Spudding, 1000 source). |
| **Griptium Pickaxe** | 3 Griptium + 2 sticks. Only mines Chudington Ore (super fast, 50% bonus). 256 durability. |
| **Spud Familiar** | Endgame Ars item. Gives Mana Boost III + Mana Regen III while in inventory. |
| **Spud Wallet** | Remotely accesses a linked Spud Vault from anywhere. Shift+right-click vault to link. |
| **Spud GPU** | Install in Spud Miner to increase hash rate. Extremely expensive to craft. |
| **Molten Grip Bucket** | Bucket of Molten Grip Fluid. Burns on contact. |

## Blocks

| Block | Description |
|-------|-------------|
| **Chudington Ore** | Spawns in the Aether (replaces holystone). Emits light level 7. 10% chance to spawn a Spud Rat when mined. |
| **Spuddington Block** | 9 Spuddington. Drops itself. Wearable on feet slot. |
| **Griptium Block** | 9 Griptium storage block. Used to craft GPUs and the Spud Miner. |
| **Spud Vault** | Stores unlimited Spud/Spudding/Spuddington. Right-click to open. Supports hoppers, pipes, and CC:Tweaked peripherals. Directional. |
| **Spud Miner** | Bitcoin-style passive spud generator. Requires GPUs, FE power, and adjacent vault. Directional. |

## Spud Miner System

The Spud Miner is an endgame passive spud generator that mimics cryptocurrency mining.

**Setup:**
1. Place a Spud Vault
2. Place a Spud Miner adjacent to the vault
3. Connect FE power (Create Power Grid, Mekanism, Thermal, etc.)
4. Install Spud GPUs via the GUI (up to 10)

**Power consumption scales exponentially:**

| GPUs | FE/t | Mining Speed |
|------|------|-------------|
| 1 | 100 | 1 spud / 30s |
| 2 | 250 | 1 spud / 15s |
| 3 | 500 | 1 spud / 10s |
| 4 | 1,000 | 1 spud / 7.5s |
| 5 | 2,000 | 1 spud / 6s |
| 6 | 3,500 | 1 spud / 5s |
| 7 | 5,500 | 1 spud / 4.3s |
| 8 | 8,000 | 1 spud / 3.75s |
| 9 | 11,000 | 1 spud / 3.3s |
| 10 | 15,000 | 1 spud / 3s |

**Spud Miner specs:**
- 1,000,000 FE internal buffer
- 10,000 FE/t max input
- GUI shows GPU slots, energy bar, consumption, and total mined

**Spud GPU recipe:** 4 Diamond Blocks + 4 Griptium Blocks + 1 Netherite Ingot (per GPU)

## Spud Vault & Wallet

**Spud Vault:**
- Unlimited storage for Spud, Spudding, and Spuddington
- Right-click with spud items to deposit directly
- Shift-click in GUI to deposit
- Drops all contents when broken
- Works with hoppers and item pipes (IItemHandler)
- CC:Tweaked peripheral with Lua API

**Spud Wallet:**
- Shift+right-click on a Spud Vault to link
- Right-click in air to open linked vault remotely (chunk must be loaded)
- Conversion buttons: 8 Spudding ↔ 1 Spuddington

## Molten Grip Fluid

A hot pink fluid produced in Create basins.
- Burns entities on contact (lava-tagged)
- Can be bucketed, piped, and stored in tanks
- Produced by superheated mixing: 2 Crushed Toe Jam + 500mb Lava

## Custom Effect: Gripper Feet

A custom potion effect that grants:
- **Speed** (level scales with amplifier)
- **Jump Boost** (level scales with amplifier)
- **Wall Climbing** — walk into any wall and climb up like a spider

Obtained by eating Spud (level I, 10s) or Spuddington (level III, 90s).

## Mobs

### Spud Rat

A small mob that spawns from Chudington Ore (10% chance on mine). Rendered as a billboard sprite.

**Wild:** Hostile, 8 HP, 2 damage, drops 1-2 Toe Jam.

**Taming:** Feed Spud (1 in 3 chance). Tamed rats:
- Follow you, fight for you, sit on command
- Shift+right-click to ride on your head (invulnerable)
- Sniffs out Chudington Ore within 8 blocks (green particles)
- Feed Spud to heal (4 HP)

## Recipes

### Processing Flow
```
Chudington Ore
├── Mining → 2-3 Raw Spud
└── Crushing Wheels → 3-4 Raw Spud

Raw Spud
├── Smelting/Blasting → Spud
├── Pressing → Spudding
└── Ars Enchanting (4x Raw Spud + Source Gem, 5000 source) → Spuddington

Spud → 8:1 → Spudding → 8:1 → Spuddington → 9:1 → Spuddington Block
```

### Griptium Production Chain
```
Toe Jam (rat drop)
  → [Create Crushing Wheels] → Crushed Toe Jam
    → [Create Superheated Mixer + Lava] → Molten Grip Fluid
      → [Create Compacting, Superheated] → Grip Plate
        → [Ars Enchanting Apparatus + 4 Spudding] → Griptium
```

### Key Recipes
- Griptium Block: 9 Griptium (shaped 3x3)
- Griptium Pickaxe: 3 Griptium + 2 Sticks
- Spud GPU: 4 Diamond Blocks + 4 Griptium Blocks + 1 Netherite Ingot
- Spud Miner: 4 Griptium Blocks + 4 Iron Blocks + 1 Redstone Block
- Spud Wallet: 7 Griptium + 1 Spuddington + 1 Ender Chest
- Spud Vault: 8 Griptium + 1 Spuddington Block
- Spud Familiar: Ars Enchanting (2 Spudding + 2 Griptium + 2 Toe Jam + Spuddington reagent, 2000 source)

## Loot Table Injection

All items can appear randomly in chests (including Lootr):

| Item | Chance | Amount |
|------|--------|--------|
| Raw Spud | 15% | 1-3 |
| Spud | 8% | 1-2 |
| Toe Jam | 6% | 1-2 |
| Spudding | 3% | 1 |
| Griptium | 3% | 1-2 |
| Spuddington | 1% | 1 |
| Griptium Pickaxe | 0.5% | 1 |
| Spud Familiar | 0.2% | 1 |

## CC:Tweaked Integration

Place a computer next to a Spud Vault:
```lua
local vault = peripheral.wrap("left")
print(vault.getSpud())
print(vault.getSpudding())
print(vault.getSpuddington())
print(vault.getTotal())

-- Listen for changes
while true do
  local event, type, old, new = os.pullEvent("spud_vault_changed")
  print(type .. ": " .. old .. " -> " .. new)
end
```

## Advancements

20+ advancements including hidden easter eggs:
- It's Chudding Time, Baked Potato?, Getting Spuddy, Full Spud, Spud Lord
- I'm Spudding It, I Got Spud On My Grippers, One Small Step for Spud
- Hot Potato, Couch Potato, Mr. Potato Head, Mashed Potato
- Spud Gun, Spud-nik, You've Been Chudded, The Spud Rises
- Best Friends Forever, Ratatouille, That's Nasty, Grip Check
- No Grip, No Trip, Arcane Rodent, Spud Crypto

## Death Messages

- "Player was spudded to death" — killed by thrown Raw Spud
- "Player was nibbled to death by a Spud Rat"

## World Generation

Chudington Ore generates in Aether biomes:
- Replaces holystone, vein size 8, 7 veins/chunk, Y 0-128

## Building

```bash
./gradlew build
```

Output jar: `build/libs/aetheraddon-1.0.0.jar`

## Textures To Replace

- `textures/item/toe_jam.png`, `crushed_toe_jam.png`, `grip_plate.png`
- `textures/item/griptium.png`, `griptium_pickaxe.png`, `spud_gpu.png`
- `textures/item/spud_familiar.png`, `spud_wallet.png`, `molten_grip_bucket.png`
- `textures/block/griptium_block.png`, `spud_miner.png`
- `textures/entity/spud_rat.png`
- `textures/mob_effect/gripper_feet.png` (18x18)
