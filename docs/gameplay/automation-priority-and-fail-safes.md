# Automation Priority and Fail-Safes

## Purpose

Prevent colony collapse by codifying emergency behavior.

## Priority Model

- Define load classes:
  - `P0` life support (oxygen, scrubbers, critical pumps)
  - `P1` thermal safety and sanitation
  - `P2` production/refining
  - `P3` luxury and non-critical systems

## Default Fail-Safes

- Brownout:
  - disable P3 first, then P2
  - preserve P0/P1 while possible
- Oxygen shortage:
  - prioritize habitat vents and scrubbers
- Heat emergency:
  - force cooling and venting loops

## Control Tools

- threshold sensors (pressure/temp/power/stress)
- priority switches
- emergency latch states

## UX

- Emergency panel shows current active fail-safe state.
- Players can override policy manually with clear risk warning.
