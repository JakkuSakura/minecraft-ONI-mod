# Mass System

This system defines how all matter quantities are represented and conserved.

## 1. Unit and Storage Model

- Canonical unit: `kg` (kilogram).
- Gas cell stores per-species gas mass in kg:
  - `gas_mass_kg[species]`
- Liquid cell stores:
  - `liquid_mass_kg`
  - `liquid_type`
- `vacuum` means all masses are `0`.
- `void` is a boundary sink where mass may be removed by rule.

## 2. Conservation Rules

In closed regions (no `void`, no machine conversion), total mass is conserved:

- `sum(gas_mass) + sum(liquid_mass) + contained_item_mass = constant`

Mass can only change through:

- machine transforms (example: electrolyzer water -> O2 + H2)
- world boundary sinks (`void`)
- scripted events (meteor vents, geysers, etc.)

## 3. Transfer Semantics

Per update step, each edge transfer is clamped:

- `transfer_kg <= edge_max_kg_per_step`
- `transfer_kg <= source_available_kg`
- `transfer_kg <= destination_capacity_kg`

This prevents oscillation and negative mass.

## 4. Gas Mass and Pressure

Pressure is derived from gas mass and temperature.

- Heuristic formula:
  - `pressure_kpa = f(total_gas_mass_kg, temperature_k, cell_volume_m3)`
- Keep formula and coefficients centralized in config for tuning.

## 5. Liquid Mass and Fill Ratio

- `fill_ratio = liquid_mass_kg / liquid_capacity_kg`
- Flow priority:
  1. down-gradient gravity flow
  2. lateral equalization toward lower fill ratio
  3. upward flow only if pressure head or pump drives it

## 6. Machine Conversion Contracts

Each machine defines:

- exact input mass rates by species/liquid
- exact output mass rates by species/liquid
- optional waste/byproduct channels

Example (`Electrolyzer`):

- consumes water mass per second
- outputs O2 and H2 mass per second
- conversion table is explicit and deterministic

## 7. Validation Invariants

- No negative mass in any store.
- No spontaneous mass creation outside approved producers.
- Closed-box test drift should remain within tiny epsilon over long runs.
