# ONI Gameplay Design for Minecraft (`mconi`)

This folder defines the first-pass gameplay design for bringing core *Oxygen Not Included* ideas into Minecraft.

## Goals

- Make breathable atmosphere, pressure, and heat first-class survival mechanics.
- Keep the experience readable and "Minecraft-native" instead of a full ONI clone.
- Build systems in phases so we can ship playable slices early.

## Document Map

- `docs/gameplay/world-foundation.md`: constrained world layout and biome stack.
- `docs/gameplay/matter-model.md`: block occupancy state machine and matter simulation model.
- `docs/gameplay/mass-system.md`: mass units, conservation rules, and transfer semantics.
- `docs/gameplay/thermal-system.md`: thermal model, transfer rules, and machine overheat behavior.
- `docs/gameplay/oxygen-and-breathing-system.md`: oxygen lifecycle from generation to breathing penalties.
- `docs/gameplay/stress-system.md`: stress, morale pressure, and behavior penalties.
- `docs/gameplay/power-grid-system.md`: ONI-style watt network with simplified IC2-like constraints.
- `docs/gameplay/research-and-construction.md`: research tree, recipe book gating, and timed construction workflow.
- `docs/gameplay/simulation-kernel-spec.md`: simulation contracts and formulas.
- `docs/gameplay/progression-framework.md`: progression, buildings, and unlocks.
- `docs/gameplay/atmo-suits-and-checkpoints.md`: suit lifecycle, dock behavior, and hazard gating.
- `docs/gameplay/room-system-and-bonuses.md`: room detection, categories, and buffs.
- `docs/gameplay/sanitation-and-disease-loop.md`: toilets, polluted chains, and disease pressure.
- `docs/gameplay/food-and-farming-loop.md`: farming constraints, spoilage, and cooking tiers.
- `docs/gameplay/renewable-vents-and-geysers.md`: cyclical renewable resource sources.
- `docs/gameplay/phase-change-and-state-transitions.md`: boiling/condensation/freezing gameplay rules.
- `docs/gameplay/atmosphere-separation-and-storage.md`: gas filtering, buffering, and overflow control.
- `docs/gameplay/automation-priority-and-fail-safes.md`: load shedding and emergency control doctrine.
- `docs/gameplay/colony-events-and-disasters.md`: periodic hazards and recovery gameplay.
- `docs/gameplay/milestones-and-victory-track.md`: colony objectives and success states.
- `docs/gameplay/errands-and-actor-model.md`: who performs work and how task execution scales.
- `docs/gameplay/chunk-simulation-and-unload-rules.md`: chunk unload policy for persistent simulation.

## Design Principles

- Deterministic simulation where possible.
- Chunk-local updates with bounded per-tick work.
- Strong visual feedback before complexity.
- Configurable difficulty multipliers for server owners.
