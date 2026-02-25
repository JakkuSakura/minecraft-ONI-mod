# Chunk Simulation and Unload Rules

## Purpose

Keep colony simulation coherent in Minecraft chunk-loading constraints.

## Authority Model

- Server is authoritative for all simulation state.
- Simulation state persists independently from vanilla block entity tick timing.

## Unload Policy

- Active colony chunks can be marked as `simulation-critical`.
- On unload:
  - store full cell/network snapshot
  - store pending machine state
  - store construction/research progress state

## Resume Policy

- On reload, run deterministic catch-up step with capped max elapsed simulation.
- If elapsed time exceeds cap, apply summarized approximation pass instead of full tick replay.

## Safety Rules

- No negative mass/energy on catch-up.
- No instant event burst on reload.
- Player-near chunks always receive highest simulation priority.

## Config

- `simulation_critical_chunk_radius`
- `max_catchup_seconds`
- `catchup_mode` (`replay` or `approximate`)
