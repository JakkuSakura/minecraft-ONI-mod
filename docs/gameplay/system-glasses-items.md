# System Glasses Items

## Goal

Add one diagnostic wearable item per colony system.  
On right-click, it shows layered properties for the selected system at the targeted/current location.

## Item Family

- `AtmosphereGlasses`
- `FluidGlasses`
- `ThermalGlasses`
- `OxygenGlasses`
- `PowerGlasses`
- `StressGlasses`
- `ResearchGlasses`
- `ConstructionGlasses`

## Interaction Contract

1. Equip or hold glasses item.
2. Right-click while looking at a block/cell.
3. UI panel (or chat fallback) shows layered properties.
4. Optional secondary right-click toggles compact vs detailed mode.

## Layered Output Model

Each system exposes properties as:

- `layer`: logical subgroup
- `key`: metric name
- `value`: formatted value

Examples:

- Atmosphere:
  - `matter.occupancy`
  - `pressure.kPa`
  - `gas.O2_kg`, `gas.CO2_kg`, `gas.H2_kg`
- Oxygen:
  - `oxygen.o2_fraction`
  - `oxygen.co2_fraction`
  - `oxygen.breathing_band`
- Power:
  - `power.generation_w`
  - `power.demand_w`
  - `power.stored_j`
  - `power.tripped`

## UX Rules

- Never hide critical values behind extra clicks.
- Red/yellow/green semantic coloring for danger bands.
- Keep right-click read operation side-effect free.
- Provide chat fallback when overlay UI is unavailable.

## Runtime Integration

- Data source is the simulation runtime inspector service.
- Right-click item action maps `item -> system lens`.
- WorldEdit and other bulk edit workflows must not break read operations.

## Implementation Notes

- Initial implementation can use command-backed inspector output.
- Later iteration should render an on-screen panel with pagination.
