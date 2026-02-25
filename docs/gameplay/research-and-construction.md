# Research and Construction v1

This reworks building from instant crafting to a research + construction workflow.

## 1. Core Player Flow

1. Unlock technology via research.
2. Acquire recipe book entry (blueprint knowledge).
3. Gather required materials/components.
4. Place construction ghost.
5. Build completes over time with progress, not instantly.

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
  - Liquids (pumps, purification, thermal fluids)
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
