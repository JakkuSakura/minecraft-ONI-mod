# World Foundation and Biome Layout

This is a hard requirement for the ONI-style experience.

## World Topology

- World is horizontally constrained with bedrock side borders.
- Top boundary is "space exposure" (vacuum-biased zone).
- Bottom boundary is lava/oil/igneous danger zone.
- Playable world uses a finite-width colony map, not infinite terrain.

## Map Shape

- Recommended default size:
  - `X`: `-1024 .. 1024`
  - `Z`: `-1024 .. 1024`
  - normal Minecraft vertical range, with custom strata behavior
- Bedrock border walls at min/max X/Z to prevent leaving colony bounds.
- Optional config presets:
  - `small`: 1024x1024 footprint
  - `standard`: 2048x2048 footprint
  - `large`: 4096x4096 footprint

## Vertical Strata

1. Space Layer (upper band):
- Very low pressure, poor O2 retention, extreme thermal swing.
- Meteor/event hook point for future content.

2. Mid Colony Layer:
- Primary buildable habitat and industrial area.
- Mixed biomes containing water pockets, algae, polluted zones, gas vents.

3. Deep Hazard Layer:
- High heat, magma/lava pools, rare minerals, pressurized gases.
- Primary late-game geothermal and advanced materials source.

## Atmosphere Boundary Rules

- Top space layer continuously drains atmospheric mass upward.
- Bottom magma layer emits heat and occasional gas pockets.
- Side bedrock walls are impermeable to gas/liquid transfer.
- Top layer positions are tagged as `void` occupancy behavior.

## Generation Objectives

- Guarantee nearby early survival resources:
  - water access
  - algae/biomass starter oxygen source
  - at least one early metal node
- Guarantee at least one reachable mid-game heat source.
- Guarantee at least one late-game extreme zone for progression gate.
- Guarantee starter liquid pockets with finite mass (no infinite-source assumptions).

## Technical Constraints

- Border walls and top/bottom bands should be deterministic from seed.
- Generation must avoid hard-lock starts (no oxygen + no water + no algae).
- Configurable but server-authoritative world size and boundary behavior.
