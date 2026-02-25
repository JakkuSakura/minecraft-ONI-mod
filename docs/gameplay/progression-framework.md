# Progression Framework

## Tier 0: Crash Survival (Day 1-2)

Player objectives:

- Stabilize breathable shelter.
- Secure basic water and food.
- Prevent early CO2 buildup.
- Learn finite-liquid constraints (water is limited and recoverable).
- Maintain low stress with minimal comfort and stable shelter.

Unlocks/content:

- `AlgaeTerrarium`
- `ManualAirPump` (low throughput)
- `BasicGasVent`
- `PressureDoor` (room sealing utility)
- `ManualGenerator` (emergency electricity)

Worldgen dependency:

- Starter biome must contain algae/biomass and water in reachable distance.

## Tier 1: Utility Foundation

Player objectives:

- Build first gas network loop.
- Centralize oxygen generation.
- Add basic thermal management.

Unlocks/content:

- `Electrolyzer`
- `GasPump`
- `GasPipe`, `GasBridge`, `GasValve`
- `LiquidPump`
- `LiquidPipe`, `LiquidValve`
- `CO2Scrubber`
- `BatteryBank`
- `BasicWire`, `PowerTransformer`

Worldgen dependency:

- Reach polluted biome and metal nodes.

## Tier 2: Industrial Colony

Player objectives:

- Scale to high-throughput power, refining, and climate control.
- Separate hot/cold zones and automate utility routing.

Unlocks/content:

- `ThermalRegulator`
- `SmartSensor` (gas, temp, pressure)
- stress-relief and decor modules
- advanced material refinery chain
- high-capacity utility variants
- medium/high-tier wire systems

Worldgen dependency:

- Access deep hazard layer for geothermal and rare resources.

## Tier 3: Space and Extreme Engineering

Player objectives:

- Operate reliably near space exposure and deep lava pressures.
- Build closed-loop resource systems.

Unlocks/content:

- vacuum-rated structures
- high-efficiency atmospheric processors
- late-game automation and resilience modules

Worldgen dependency:

- Use top space band and lower magma zones as intentional gameplay spaces.

## Progression Gates

- Tech unlocks tied to resources + crafted research packs.
- Major machines require blueprint unlock from research tree.
- Construction uses timed build ghosts, not instant crafting placement.
- Build requires all required materials/components before progress completes.
- Some machines require environmental prerequisites:
  - minimum pressure
  - max operating temperature
  - specific gas/liquid input
- Colony efficiency is also gated by stress and power stability.

## Failure/Recovery Design

- Failure should degrade systems before hard death.
- Recovery tools must exist one tier earlier than full failure states.
- Core rule: "stabilize, then expand."
