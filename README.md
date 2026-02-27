# Oxygen Not Included (MCONI)

Bring ONI-style colony simulation into Minecraft. This repo focuses on a constrained ONI world, layered matter simulation, and gameplay systems that mirror Oxygen Not Included without abandoning Minecraft ergonomics.

## Highlights

- Constrained ONI world with bedrock borders, space exposure up top, lava layer below.
- Multi-layer simulation: atmosphere, liquids, thermal, stress, power, research, construction.
- Debug HTTP server for runtime inspection and test automation.
- System glasses items for in-world visualization.
- Multi-loader support (Fabric, NeoForge, Spigot).

## Quickstart (NeoForge)

```bash
./gradlew :neoforge:runClient -PmcVer=1.21.11 --no-daemon
```

## Docs Index

- World foundation and constraints: `docs/gameplay/world-foundation.md`
- Simulation kernel: `docs/gameplay/simulation-kernel-spec.md`
- Matter model: `docs/gameplay/matter-model.md`
- Thermal system: `docs/gameplay/thermal-system.md`
- Oxygen and breathing: `docs/gameplay/oxygen-and-breathing-system.md`
- Power grid: `docs/gameplay/power-grid-system.md`
- Research and construction: `docs/gameplay/research-and-construction.md`
- Stress system: `docs/gameplay/stress-system.md`
- System glasses: `docs/gameplay/system-glasses-items.md`
- Full gameplay overview: `docs/gameplay/README.md`

## Branding

- Maven group: `mconi`
- Mod ID: `mconi`
- Mod name: `Oxygen Not Included`

## Status

Active development. Docs lead the implementation; code is expected to converge on the specs in `docs/`.
