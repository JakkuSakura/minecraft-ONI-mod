# Oxygen System v1

This system defines oxygen generation, distribution, and player survival effects.

## 1. Oxygen Sources

Primary v1 producers:

- `AlgaeTerrarium`: low-rate passive oxygen.
- `Electrolyzer`: high-rate oxygen + hydrogen from water.
- plant/biome passive contribution (optional low baseline).

Each source defines:

- input requirements
- output `O2_kg_per_s`
- heat byproduct

## 2. Oxygen Sinks

- player breathing consumption
- combustion-like machine consumption (future)
- leaks to `void` space exposure
- displacement by heavier gases in poorly ventilated areas

## 3. Breathability Evaluation

At player head cell:

- `o2_fraction = O2_mass / total_gas_mass`
- Evaluate with pressure band:
  - healthy: O2 `>= 18%` and pressure `70-180 kPa`
  - stressed: O2 `12-18%` or thin pressure
  - critical: O2 `< 12%` or vacuum

CO2 thresholds:

- soft penalty at `>= 6%`
- hard penalty at `>= 12%`

## 4. Player Effects

- Healthy: no penalties.
- Stressed:
  - mining speed penalty
  - higher stamina/sprint drain
- Critical:
  - rapid debuffs and periodic damage
  - eventually death if unresolved

Effects should ramp, not instantly jump, to support recoverability.

## 5. Distribution Mechanics

- Natural diffusion handles baseline mixing.
- `GasPump` + pipes + vents handle directed distribution.
- Room sealing quality influences oxygen retention.
- Pressure doors and airlocks reduce oxygen loss during transit.

## 6. UI and Debug Visibility

- HUD:
  - local O2%
  - local pressure
  - warning state icon
- Overlay:
  - O2 concentration heatmap
  - CO2 concentration heatmap

## 7. Balance Targets

- Early game: manual maintenance but no immediate hard death spiral.
- Mid game: stable oxygen requires infrastructure investment.
- Late game: oxygen should be cheap only if automation and resource loops are well designed.
