# Aether Addon - The Spud Mod

A NeoForge 1.21.1 addon for The Aether mod that adds a full spud-themed progression system, a tameable pet rat, custom tools, a unique potion effect, and plenty of meme energy.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.230+
- The Aether (1.5.10+)
- Optional: Create (6.0+) for crushing/pressing/mixing recipes
- Optional: Ars Nouveau (5.11+) for enchanting apparatus recipes and Spud Familiar

## Items

| Item | Description |
|------|-------------|
| **Raw Spud** | Dropped from Chudington Ore (1-2, Fortune compatible). Throwable like a snowball, deals 2 damage. |
| **Spud** | Smelted/blasted from Raw Spud. Edible: 3 hunger, Gripper Feet I for 10s, always edible, fast eat. Used to tame Spud Rats. |
| **Spudding** | Crafted via Ars Nouveau Enchanting Apparatus (4 Raw Spud on pedestals + 1 reagent, 500 source). |
| **Spuddington** | 8 Spudding (shapeless). Super food: 10 hunger, Gripper Feet III (90s), Resistance II (90s), Regen II (30s). Slow eat time. |
| **Toe Jam** | Dropped by wild Spud Rats (1-2). First step in the Griptium chain. |
| **Crushed Toe Jam** | Made by crushing Toe Jam in Create Crushing Wheels. |
| **Molten Grip Compound** | Made by superheated mixing 2 Crushed Toe Jam + 500mb Lava. |
| **Grip Plate** | Made by pressing Molten Grip Compound in a Create Mechanical Press. |
| **Griptium** | Made via Ars Enchanting Apparatus (Grip Plate reagent + 4 Spudding, 1000 source). |
| **Griptium Pickaxe** | 3 Griptium + 2 sticks. Only mines Chudington Ore (super fast, 50% bonus drops). Useless on other blocks. 256 durability. |
| **Spud Familiar** | Crafted via Enchanting Apparatus (Spuddington reagent + 2 Spudding, 2 Griptium, 2 Toe Jam, 2000 source). Passively gives Mana Boost III and Mana Regen III while in inventory. Has enchanted shimmer. |

## Blocks

| Block | Description |
|-------|-------------|
| **Chudington Ore** | Spawns in the Aether (replaces holystone). Emits light level 7. 10% chance to spawn a Spud Rat when mined. Silk Touch returns the block. |
| **Spuddington Block** | 9 Spuddington (shaped). Drops itself. Wearable on feet slot (right-click to equip). |

## Custom Effect: Gripper Feet

A custom potion effect that grants:
- **Speed** (level scales with amplifier)
- **Jump Boost** (level scales with amplifier)
- **Wall Climbing** — walk into any wall and climb up like a spider

Obtained by eating Spud (level I, 10s) or Spuddington (level III, 90s).

## Mobs

### Spud Rat

A small mob that spawns from Chudington Ore (10% chance on mine). Rendered as a flat billboard sprite that always faces the player and glows.

**Wild:**
- Hostile, attacks players on sight
- 8 HP, 2 attack damage, fast (0.35 speed)
- Drops 1-2 Toe Jam on death

**Taming:**
- Feed it a Spud (right-click). 1 in 3 chance to succeed.
- Heart particles on success, smoke on fail.

**Tamed:**
- Right-click: toggle sit/follow
- Shift+right-click: mount on your head
- Attacks what you attack, defends you
- Feed Spud to heal (4 HP per spud)
- No longer drops Toe Jam on death

**On your head:**
- Invulnerable to all damage
- Scans for Chudington Ore within 8 blocks every 2 seconds
- Spawns green particles on nearest ore to guide you

## Recipes

### Vanilla Crafting
- 8 Spudding → 1 Spuddington (shapeless)
- 1 Spuddington → 8 Spudding (shapeless)
- 9 Spuddington → 1 Spuddington Block (shaped 3x3)
- 1 Spuddington Block → 9 Spuddington (shapeless)
- 3 Griptium + 2 Sticks → Griptium Pickaxe (pickaxe shape)

### Smelting / Blasting
- Raw Spud → Spud (furnace 200t / blast 100t)

### Create (requires Create mod)
- **Crushing:** Chudington Ore → 1 Raw Spud + 50% chance 1 extra (200 ticks)
- **Crushing:** Toe Jam → 1 Crushed Toe Jam + 25% chance 1 extra (150 ticks)
- **Mixing (superheated):** 2 Crushed Toe Jam + 500mb Lava → Molten Grip Compound (300 ticks)
- **Pressing:** Molten Grip Compound → Grip Plate
- **Pressing:** Raw Spud → Spud
- **Mixing (heated):** Raw Spud + 250mb Water → Spud (200 ticks)

### Ars Nouveau (requires Ars Nouveau mod)
- **Enchanting Apparatus:** 4 Raw Spud pedestals + 1 Raw Spud reagent → Spudding (500 source)
- **Enchanting Apparatus:** 4 Spudding pedestals + Grip Plate reagent → Griptium (1000 source)
- **Enchanting Apparatus:** 2 Spudding + 2 Griptium + 2 Toe Jam pedestals + Spuddington reagent → Spud Familiar (2000 source)

### Griptium Production Chain
```
Toe Jam (rat drop)
  → [Create Crushing Wheels] → Crushed Toe Jam
    → [Create Superheated Mixer + Lava] → Molten Grip Compound
      → [Create Mechanical Press] → Grip Plate
        → [Ars Enchanting Apparatus + 4 Spudding] → Griptium
```

## Loot Table Injection

All items can appear randomly in any chest (including Lootr chests):

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

## Advancements

### Main Progression
```
Aether Addon (root - auto unlock)
├── It's Chudding Time (get Raw Spud)
│   ├── Baked Potato? (get Spud)
│   │   ├── Hot Potato (be on fire holding Spud) [hidden]
│   │   └── Couch Potato (hold Spud in mainhand) [hidden]
│   ├── Getting Spuddy (get Spudding)
│   │   ├── Full Spud (get Spuddington)
│   │   │   ├── Spud Lord (get Spuddington Block) [challenge]
│   │   │   │   ├── I'm Spudding It (place Spuddington Block)
│   │   │   │   │   └── I Got Spud On My Grippers (wear as boots) [hidden]
│   │   │   │   │       ├── One Small Step for Spud (fall 10+ blocks in boots) [hidden]
│   │   │   │   │       └── Mashed Potato (take damage in boots) [hidden]
│   │   │   └── Mr. Potato Head (rename Spuddington on anvil) [hidden]
│   │   └── Arcane Rodent (get Spud Familiar) [hidden, challenge]
│   ├── Spud Gun (hit entity with thrown Raw Spud) [hidden]
│   ├── Spud-nik (fall 30+ blocks holding Raw Spud) [hidden]
│   ├── You've Been Chudded (mine ore bare-fisted) [hidden, challenge]
│   ├── Best Friends Forever (tame a Spud Rat) [hidden]
│   │   └── Ratatouille (put rat on head) [hidden]
│   └── That's Nasty (get Toe Jam) [hidden]
│       └── Grip Check (get Griptium)
│           └── No Grip, No Trip (craft Griptium Pickaxe)
└── The Spud Rises (carry all 4 spud items + enter Aether) [hidden, challenge]
```

## Death Messages

- `"Player was spudded to death"` — killed by thrown Raw Spud
- `"Player was spudded to death by OtherPlayer"` — PvP spud kill
- `"Player was nibbled to death by a Spud Rat"` — killed by Spud Rat

## Item Tooltips

All items have gray italic tooltip descriptions:
- Raw Spud: *"Throwable. Smeltable. Questionable."*
- Spud: *"Tastes like speed. Literally."*
- Spuddington: *"The ultimate superfood. You can feel it in your bones."*
- Spudding: *"Enchanted by arcane spud magic. Smells faintly of feet."*
- Toe Jam: *"Dropped by Spud Rats. Don't ask where it comes from."*
- Crushed Toe Jam: *"Ground down to a fine, disgusting powder."*
- Molten Grip Compound: *"Hot. Sticky. Visceral."*
- Grip Plate: *"Pressed flat. Ready for arcane infusion."*
- Griptium: *"Forged through feet, fire, and forbidden magic."*
- Griptium Pickaxe: *"Only works on Chudington Ore. Grips it real good."*
- Spud Familiar: *"A rat spirit bound to spud magic. Boosts your mana significantly."*
- Chudington Ore: *"It glows with spuddy energy. Watch out for rats."*
- Spuddington Block: *"9 Spuddingtons compressed into pure drip. Wearable on feet."*

## World Generation

Chudington Ore generates in Aether biomes:
- Replaces holystone
- Vein size: 8
- 7 veins per chunk
- Y level: 0-128

## Building

```bash
./gradlew build
```

Output jar: `build/libs/aetheraddon-1.0.0.jar`

## Textures To Replace

Placeholder textures that need art:
- `textures/item/toe_jam.png`
- `textures/item/crushed_toe_jam.png`
- `textures/item/molten_grip_compound.png`
- `textures/item/grip_plate.png`
- `textures/item/griptium.png`
- `textures/item/griptium_pickaxe.png`
- `textures/item/spud_familiar.png`
- `textures/entity/spud_rat.png`
- `textures/mob_effect/gripper_feet.png` (18x18)
