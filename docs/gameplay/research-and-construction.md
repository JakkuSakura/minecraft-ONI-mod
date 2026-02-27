# Research and Construction System

This reworks building from instant crafting to a research + construction workflow.

## 1. Core Player Flow

1. Unlock technology via research.
2. Acquire recipe book entry (blueprint knowledge).
3. Gather required materials/components.
4. Place construction ghost.
5. Build completes over time with progress, not instantly.

## 1.1 ONI-Style Construction Flow (In-Game)

This mod uses an ONI-like construction flow driven by a Blueprint Book and construction sites.

Player actions:

1. Craft a `Blueprint Book` (4x Dirt).
2. Open the book to select a blueprint and the construction materials for each slot.
3. Right click in air to receive a `Blueprint` item for the selected building.
4. Right click a target location to place a `Construction Site` directly (no intermediate item).
5. Right click the site to deliver materials over time (per-second rate).
6. When all materials are delivered, right click and hold to build over time.
7. Construction can be interrupted and resumed at any time.
8. Breaking a site refunds all delivered materials (full refund).

Notes:

- All blueprints are selectable for now (research may still pause completion).
- Material requirements are based on Oxygen Not Included building materials.

Current blueprint materials (ONI wiki reference values for the primary material, plus a secondary MC material to enforce multi-material recipes):

| Blueprint | ONI building | Slot 1 (ONI material) | Amount (kg) | Slot 2 (MC structural material) | Amount |
| --- | --- | --- | --- | --- | --- |
| oxygen_diffuser | Oxygen Diffuser | Metal Ore | 200 | Raw Mineral | 50 |
| algae_deoxidizer | Oxygen Diffuser (material values) | Metal Ore | 200 | Raw Mineral | 50 |
| co2_scrubber | Carbon Skimmer | Metal Ore | 100 | Raw Mineral | 50 |
| liquid_pump | Liquid Pump | Metal Ore | 400 | Refined Metal | 50 |
| gas_pump | Gas Pump | Metal Ore | 50 | Refined Metal | 25 |
| manual_generator | Manual Generator | Metal Ore | 200 | Raw Mineral | 100 |
| battery | Battery | Metal Ore | 200 | Refined Metal | 50 |
| power_wire | Wire | Metal Ore | 25 | Raw Mineral | 10 |
| power_generator | Coal Generator | Metal Ore | 800 | Refined Metal | 200 |
| research_desk | Research Station | Metal Ore | 400 | Raw Mineral | 100 |

Notes:

- Slot 1 follows ONI material costs.
- Slot 2 is a mod-specific structural requirement to ensure multi-material recipes in Minecraft.

## 2. Research System

### Research Stations

- `ResearchDesk` (tier 0/1)
- `AdvancedResearchLab` (tier 2+)

### Research Inputs

- Research packs (crafted from specific resources).
- Power consumption while researching.
- Optional gas/temperature prerequisites for advanced research.

### Tech Tree Structure

- Directed acyclic graph with tier dependencies.
- Example branches:
  - Atmosphere (oxygen, gas filtration)
  - Liquids (pumps, purification, thermal liquids)
  - Thermal (regulators, insulation, cooling)
  - Automation (sensors, valves, logic controllers)

## 3. Recipe Book / Blueprint Gating

- A building is not constructible unless its blueprint is known.
- Blueprint knowledge sources:
  - unlocked from research node
  - discovered in world structures (optional extension)
  - granted by progression milestones

Data contract per blueprint:

- `blueprint_id`
- required research node(s)
- construction material list
- build time base seconds
- required construction station/tool tier

## 4. Timed Construction

### Placement Model

- Player places `construction ghost` block/entity.
- Ghost stores required materials and progress.
- Materials can be deposited all-at-once or incrementally.

For the ONI-style construction site:

- Delivery and build progress advance per second, not per click.
- Rate is multiplied by the builder's personal stress score.
- If materials are incomplete, progress is paused with a reason shown.

### Build Progress

- Progress advances only if:
  - all required materials are present
  - required environment constraints are valid
  - builder has power/tool prerequisites if applicable
- Progress speed modifiers:
  - nearby construction module bonuses
  - player skill/perk system (future)
  - debuffs from low oxygen/high CO2/high heat

### Completion/Failure

- On completion, ghost converts to final machine/block.
- If conditions fail mid-build, progress pauses (no silent rollback).

## 5. Recipe Rework Model

- Replace direct vanilla crafting for core machines with:
  - component crafting (`plate`, `pipe segment`, `circuit`, etc.)
  - assembly through construction ghosts
- Keep vanilla crafting only for:
  - primitive tools
  - emergency survival items
  - selected low-tier blocks

## 6. Balance Targets

- Early game:
  - simple buildings have short build times and low gating friction.
- Mid game:
  - infrastructure takes meaningful planning and staging.
- Late game:
  - high-tier systems require multi-resource chains and research depth.

## 7. UX Requirements

- Build ghost UI must show:
  - missing materials
  - missing research/blueprint
  - estimated completion time
  - paused reason
- Research UI must show:
  - prerequisites
  - costs
  - unlocked blueprints

## 8. Anti-Frustration Rules

- Never allow hidden build requirements.
- Show exact blocker text, not generic "cannot build."
- Permit cancel/deconstruct with partial material refund rules.
