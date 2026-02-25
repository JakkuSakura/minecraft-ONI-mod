# Electricity System

This system is inspired by IC2 network constraints, but simplified and ONI-like in operation.

## 1. Core Model

- Power network is wire-connected graph segments.
- Primary unit: `W` (watts); optional display aggregate `kW`.
- Per tick, each network computes:
  - total generation
  - total demand
  - storage charge/discharge
  - overload state

## 2. Supply and Demand Behavior

- Generators push available watt output into the network.
- Consumers request rated watt demand.
- If supply < demand:
  - priority loads stay online first
  - lower-priority machines brownout (pause, not destroy)
- If supply > demand:
  - charge batteries first
  - excess is wasted or dumped to optional sink devices

## 3. Wire Capacity (IC2-Like Constraint)

- Each wire tier has max transfer capacity:
  - `basic_wire`: low capacity
  - `conductive_wire`: medium capacity
  - `heavy_wire`: high capacity
- Overload rule:
  - sustained flow above capacity triggers `tripped` state
  - tripped wires stop carrying power until reset/cooldown
- Default behavior avoids random explosions; "hard failure" can be a config option.

## 4. Storage

- `BatteryBank` stores finite energy (`J` or internal watt-tick units).
- Charge/discharge rates are capped.
- Batteries leak small heat into local thermal cell.

## 5. Generators (Initial Set)

- `ManualGenerator`: player-powered, emergency startup.
- `CoalGenerator` or equivalent fuel burner: stable early-mid source.
- `HydrogenGenerator`: consumes H2 from gas system.
- `SteamTurbine` (later tier): consumes thermal gradient/steam loop.

## 6. Integration Rules

- Power is required for:
  - pumps
  - scrubbers
  - electrolyzers
  - research stations
  - advanced construction modules
- Brownout should pause machines cleanly without deleting buffered mass/fluids.
- Automation can disable non-critical loads when grid stress is high.

## 7. UX Requirements

- Overlay for wire utilization and bottlenecks.
- Network UI panel:
  - generation
  - demand
  - stored energy
  - overload/trip alerts
- Machine tooltip shows real-time power state (`online`, `brownout`, `no power`).

## 8. Balance Targets

- Early game power is fragile but understandable.
- Mid game requires load planning and wire tier upgrades.
- Late game supports stable automated colonies with explicit redundancy planning.
