# Phase Change and State Transitions

## Purpose

Turn temperature and pressure control into production mechanics.

## Supported Changes

- `liquid -> gas` (boiling/evaporation)
- `gas -> liquid` (condensation)
- `liquid -> solid` (freezing, where relevant)
- `solid -> liquid` (melting, where relevant)

## Triggers

- Each material defines:
  - temperature thresholds
  - pressure modifiers
  - latent heat cost/release

## System Coupling

- Mass is conserved through phase conversions.
- Heat system handles energy cost/release.
- Atmosphere/liquid occupancy updates after conversion.

## Gameplay Uses

- Steam power loops.
- Cooling/condensing gas capture.
- Freeze-based transport or storage stabilization.
