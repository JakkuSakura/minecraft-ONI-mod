# Sanitation and Disease Loop

## Purpose

Create ONI-style hygiene pressure and polluted resource management.

## Core Chains

- Clean water -> toilets/sinks -> polluted water.
- Polluted water can off-gas polluted oxygen in warm/low-pressure contexts.
- Polluted oxygen increases stress and disease risk.

## Buildings

- `Latrine` / `Toilet`
- `WashStation`
- `PollutedWaterTank`
- `WaterSieve` (mid game)
- `Deodorizer` (polluted gas cleanup)

## Disease Model (v1-lite)

- Track disease exposure as a scalar per player.
- Exposure increases from:
  - polluted oxygen
  - contaminated water/food
  - unsanitary room usage
- Effects:
  - reduced stamina and work speed
  - increased stress gain

## Recovery

- Clean air/water and sanitation rooms reduce exposure.
- Medical/rest structures accelerate recovery.
