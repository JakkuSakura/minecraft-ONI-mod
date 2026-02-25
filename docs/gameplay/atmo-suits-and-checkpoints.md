# Atmo Suits and Checkpoints

## Purpose

Provide controlled access to low-oxygen, toxic, high-heat, and low-pressure areas.

## Core Flow

1. Build `SuitDock` and `Checkpoint`.
2. Supply dock with oxygen and charged suit battery.
3. Crossing checkpoint requires equipped suit unless zone policy is permissive.
4. Returning to dock recharges oxygen/power and stores suit.

## Mechanics

- Suit stats:
  - internal O2 capacity
  - battery capacity
  - thermal protection range
  - gas/toxin resistance profile
- Suit penalties:
  - reduced movement/build speed
  - higher stamina drain
- Failure states:
  - depleted O2 -> emergency suffocation timer
  - depleted power -> reduced protection and slower movement

## Gating Role

- Early game: optional for brief excursions.
- Mid game: required for polluted/hot/deep sectors.
- Late game: required for space-exposed operations.

## UX

- Suit HUD: O2%, battery%, protection status.
- Checkpoint UI: missing suit reason, dock supply state.
