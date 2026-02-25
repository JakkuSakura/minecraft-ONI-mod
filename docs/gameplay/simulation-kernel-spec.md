# Simulation Kernel Specification

This is the implementation-facing simulation contract. Values are initial targets and should be data-driven.

Related detailed specs:

- `docs/gameplay/mass-system.md`
- `docs/gameplay/thermal-system.md`
- `docs/gameplay/oxygen-and-breathing-system.md`
- `docs/gameplay/stress-system.md`
- `docs/gameplay/power-grid-system.md`
- `docs/gameplay/research-and-construction.md`

## 0. World Constraints

- Simulation assumes a bounded colony map (not infinite overworld).
- Side borders are bedrock and treated as impermeable walls.
- Upper zone is space-exposed and acts as atmosphere sink.
- Lower zone is lava-dominant and acts as persistent heat source.

## 1. Matter Occupancy Contract

- Every block position resolves to one runtime occupancy:
  - `solid`
  - `gas`
  - `fluid`
  - `vacuum`
  - `void` (space-exposed)
- `solid` excludes other states.
- `gas` and `fluid` are mutually exclusive at one position.
- `vacuum` means no matter and no automatic refill.
- `void` is an active boundary sink, not just low pressure.

## 2. Atmosphere Model

### Cell Granularity

- Simulate gases on a coarse grid (`4x4x4` blocks per cell by default).
- Each cell stores:
  - `total_moles`
  - gas composition map (required core set): `O2`, `CO2`, `H2`
  - gas composition map (optional/next): `Cl2`, `Steam`, `NaturalGas`
  - `temperature_k`
  - `pressure_kpa`

### Update Strategy

- Tick interval: every `10` game ticks (0.5s).
- Process only active cells:
  - near players
  - containing machines/pipes
  - recently changed by block updates
- Use bounded diffusion iterations per tick to cap CPU.

### Pressure

- Derived from moles and temperature in simplified ideal-gas form.
- Pressure bands:
  - `vacuum`: `< 20 kPa`
  - `thin`: `20-70 kPa`
  - `breathable`: `70-180 kPa`
  - `overpressure`: `> 180 kPa`

### Boundary Interactions

- Side bedrock borders: zero cross-cell transfer.
- Top space cells: each update removes configurable gas mass ratio.
- Bottom lava cells: inject heat and optional trace volcanic gases.

## 3. Breathing and Health Effects

### Breathability Score

- Compute at player head position from local gas composition.
- O2 fraction targets:
  - healthy: `>= 18%`
  - stressed: `12-18%`
  - critical: `< 12%`

### Effects

- Healthy: no penalty.
- Stressed:
  - mining speed `-15%`
  - sprint drain `+20%`
- Critical:
  - periodic damage (suffocation-like)
  - severe movement penalty

### CO2 Toxicity

- Soft threshold at `>= 6%`, hard threshold at `>= 12%`.
- Applies stacking fatigue and regen suppression before direct damage.

## 4. Fluid System

### Core Rule

- Fluids are finite-mass and do not use vanilla infinite-source duplication logic.

### Species (required core set)

- `Water`
- `PollutedWater`
- `CrudeOil`
- `Lava`

### Per-Cell Liquid Data

- `fluid_type`
- `fluid_mass`
- `temperature_k`

### Flow Order

1. Downward fill by gravity.
2. Sideways equalization.
3. Upward movement only by pressure or pump input.

### Occupancy Interactions

- Fluid entering a `gas` cell displaces gas mass.
- Fluid cannot enter `solid`.
- Fluid exposed to `void` is removed at boundary sink rate.

## 5. Thermal System

### Heat Entities

- Atmosphere cell temperature.
- Block thermal mass class (`low`, `medium`, `high`).
- Machine heat output in `kDTU/s` equivalent internal units.

### Rules

- Machines emit heat into neighboring atmosphere cells.
- Temperature exchange with nearby blocks is rate-limited per tick.
- Overheat state:
  - warning: at `80%` of max temp
  - degraded throughput at `100%`
  - emergency stop above `110%`

## 6. Utilities

### Networks

- Separate pipe graph types:
  - gas pipes
  - liquid pipes
  - power wires
- Throughput is edge-limited, not node-limited.

### Priority Behavior

- Pumps pull from highest pressure adjacent source.
- Vents output only when target cell can accept mass.
- Valves enforce max flow and can be sensor-controlled.

## 7. Machines (Initial Set)

- `Electrolyzer`: water -> O2 + H2, high heat.
- `AlgaeTerrarium`: passive O2, low output, early-game.
- `GasPump`: extracts gas from cell to pipe network.
- `GasVent`: inserts gas into target cell.
- `CO2Scrubber`: consumes CO2, requires filtration medium/power.
- `ThermalRegulator`: moves heat from one cell/network to another.
- `BatteryBank`: stores power, leaks minor heat.

## 8. Research and Build Gating

- Core industrial machines require:
  - research unlock
  - blueprint/recipe-book knowledge
  - timed construction completion
- Instant vanilla crafting is retained only for primitive/emergency items.
- Construction entities track:
  - required components
  - progress state
  - pause reason
  - completion transform target

## 9. Emotional Pressure

- Player stress score (`0..100`) is affected by:
  - oxygen/CO2 quality
  - thermal stress
  - hazard exposure
  - workload and poor living conditions
- Stress bands apply escalating efficiency penalties.
- High-stress band can trigger short breakdown events.
- Stress is recoverable through stable habitat and comfort systems.

## 10. Electricity

- Power uses graph-based wire networks with rated capacity tiers.
- Consumers draw watt demand; generators and batteries supply.
- If supply is insufficient, machines enter brownout/pause states.
- If wire capacity is exceeded, network segments can trip and require recovery.
- No instant destructive failure by default; hard failure is config-controlled.

## 11. Simulation Safety Limits

- Hard cap active cells per dimension per tick budget.
- If budget exceeded:
  - defer low-priority cells
  - preserve player-near and life-critical updates first
- Server config options:
  - cell size
  - tick interval
  - max active cells
  - effects severity multipliers
  - mass transfer clamp per edge
  - thermal transfer multipliers
  - oxygen consumption/penalty multipliers
  - stress gain/recovery multipliers
  - wire overload tolerance multipliers

## 12. Visual and UX Signals

- Overlay modes:
  - oxygen concentration
  - pressure
  - temperature
  - gas composition
- Block/device states:
  - normal
  - warning
  - failed/offline
- Player HUD widget:
  - local O2%
  - local pressure band
  - temperature warning indicator
  - stress band indicator
- Build UI widget:
  - research lock reason
  - missing materials
  - construction progress
- Power UI:
  - generation vs demand
  - wire load
  - battery state
