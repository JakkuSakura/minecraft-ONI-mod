# Matter Occupancy, Air, and Liquid Model

This document defines the per-block occupancy contract.

## 1. Occupancy State per Block Position

Each world block position is in one of these states:

- `Solid`: normal Minecraft block with collision/material.
- `Gas`: gaseous matter occupies the space (air or non-air gases).
- `Liquid`: liquid matter occupies the space.
- `Vacuum`: no matter in a sealed or pumped-empty space.
- `Void`: open-to-space exposure region (top boundary behavior).

Rules:

- `Solid` excludes all other matter states.
- `Gas` and `Liquid` are mutually exclusive in one block position.
- `Vacuum` has no gas mass and no liquid mass.
- `Void` is not just low pressure; it is an environment tag that actively removes incoming gas/liquid.

## 2. Gas Species (initial set)

Required:

- `Oxygen (O2)`
- `CarbonDioxide (CO2)`
- `Hydrogen (H2)`

Planned extension:

- `Steam`
- `Chlorine`
- `NaturalGas`
- mod-added gases via registry

Gas behavior:

- Gas stores finite mass, temperature, and species composition.
- Diffusion is pressure-driven between neighboring gas cells.
- Heavier gases bias downward, lighter gases bias upward (small per-tick stratification term).
- `Vacuum` cells do not diffuse out unless connected to gas.
- `Void` cells drain gas each update by configured ratio.

## 3. Liquid Species (initial set)

Required:

- `Water`
- `PollutedWater`
- `CrudeOil`
- `Lava` (as a liquid matter type and heat source)

Liquid behavior:

- Liquids are finite-volume, not infinite source/sink vanilla behavior.
- Each liquid block/cell holds `liquid_mass` up to a max capacity.
- Flow is gravity-first:
  - down
  - lateral equalization
  - upward only via pressure/pump mechanics
- No liquid duplication from source blocks.
- Liquids can displace gas in target cells; cannot displace `Solid`.
- `Void` removes exposed liquid mass at boundary rate.

## 4. State Transitions

- `Solid -> Gas/Liquid` when a block is mined/removed.
- `Gas -> Vacuum` when total gas mass reaches zero in a non-void enclosed cell.
- `Gas -> Void` only if world position is tagged as space exposure.
- `Liquid -> Gas` through evaporation/boiling rules (future phase).
- `Gas -> Liquid` through condensation rules (future phase).

## 5. Networking and Persistence Contract

- Matter data is authoritative server-side.
- Persist per active simulation cell, not per vanilla chunk block entity.
- Clients receive compressed snapshots for:
  - local HUD values
  - overlays
  - nearby machine interaction logic

## 6. Performance Constraints

- Default simulation uses coarse cells, not true per-block CFD.
- Occupancy at block-level is a facade derived from cell state plus solid geometry.
- Updates prioritize player-near and machine-near cells.
