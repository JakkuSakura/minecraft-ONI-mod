# Errands and Actor Model

## Purpose

Define who performs tasks in a Minecraft environment while keeping ONI-style planning.

## Baseline Choice

- Default: player performs errands directly.
- Optional extension: helper drones for logistics/construction/research support.

## Errand Types

- construction
- material delivery
- research operation
- maintenance/reset tasks

## Scheduling Rules

- Player actions are primary; automation only assists.
- If helper drones are enabled, use priority queue:
  - life support tasks first
  - then blocked production tasks
  - then optimization tasks

## Design Constraint

- Do not require full duplicant AI in the initial scope.
- Keep the system deterministic and server-authoritative.
