# Emotional Pressure System v1

This system adds ONI-style stress pressure without duplicant AI.

## 1. Core Concept

- Player has `stress` in range `0..100`.
- Stress rises from environmental and workload pressure.
- Stress falls from recovery conditions and comfort infrastructure.
- High stress creates performance penalties and temporary breakdown events.

## 2. Stress Inputs

Primary positive stress gain:

- Low oxygen / high CO2 exposure.
- Heat stress or cold stress.
- Hunger/thirst/sleep deprivation systems (when enabled).
- Working in darkness or cramped sealed rooms for long periods.
- Repeated hazard damage (scalding, suffocation, toxic gas).

Primary stress reduction:

- Safe breathable room with stable temperature.
- Rest/comfort blocks (bed, recreation, decor module).
- Consistent food/water availability.
- Temporary consumables (future extension).

## 3. Stress Bands

- `0-29`: stable, no penalties.
- `30-59`: pressured, minor efficiency penalty.
- `60-84`: strained, stronger penalties and warning state.
- `85-100`: breakdown risk; may trigger episode behavior.

## 4. Gameplay Effects

At higher stress, apply:

- reduced mining/build speed
- increased stamina drain
- reduced research/build throughput

Breakdown episode examples (short duration):

- panic (movement-only behavior, cannot build for a short time)
- idle freeze (action lock for a brief period)
- overconsume (higher food/water use if available)

Episodes should be recoverable and non-lethal by themselves.

## 5. System Coupling

- Stress uses oxygen/heat data directly from those systems.
- Construction and research speeds read current stress modifiers.
- Hospital/comfort room concept can later hook into stress recovery.

## 6. UX Requirements

- HUD stress meter with trend arrow.
- Warning tiers at `60+` and `85+`.
- Tooltip explains top current stress contributors.
- Breakdown warning should provide actionable mitigation hint.

## 7. Balance Targets

- Stress should punish neglect, not perfect play.
- A single short failure should not permanently spiral the player.
- Good base design should naturally stabilize stress near low bands.
