# Atmosphere Separation and Storage

## Purpose

Support stable mixed-gas colonies through filtering, buffering, and routing.

## Core Components

- `GasFilter`
- `GasReservoir`
- `PriorityValve`
- `OverflowVent`

## Rules

- Filters route by gas species with throughput limits.
- Reservoirs store finite gas mass with pressure limits.
- Overflow routes activate when target pressure exceeds threshold.
- Wrong gas in critical line should trigger warnings and optional auto-shutdown.

## Design Patterns

- Oxygen backbone + CO2 waste line.
- Hydrogen capture line for generators.
- Atmosphere buffer rooms between production and habitat.

## UX

- Pipe overlay by gas species and flow direction.
- Reservoir UI with composition percentages and trend.
