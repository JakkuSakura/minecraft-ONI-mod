# Heat System v1

This system defines temperature, heat transfer, and thermal failure behavior.

## 1. Thermal State

- Each simulation cell stores `temperature_k`.
- Solids use thermal class with effective heat capacity:
  - `low`
  - `medium`
  - `high`
- Machines define:
  - `heat_output_kw` (or equivalent internal unit)
  - `max_operating_temp_k`

## 2. Heat Transfer Channels

- Gas <-> gas conduction between neighboring cells.
- Gas <-> solid exchange at boundaries.
- Fluid <-> gas exchange where adjacent.
- Fluid <-> solid exchange for pipes/tanks/terrain contact.
- Machine heat injection into local cell.

All channels are rate-limited per tick for stability.

## 3. Heat Equation (Gameplay Approximation)

Per cell update:

- `delta_temp = (net_heat_in - net_heat_out) / effective_heat_capacity`
- clamp delta to configured max step for numerical stability.

This is a deterministic approximation, not high-fidelity CFD.

## 4. Thermal Boundaries

- Top `void` region tends toward cold-space baseline and strips heat with escaping mass.
- Bottom lava layer injects constant heat into neighboring cells.
- Bedrock side borders are thermally high-mass, low-transfer boundaries by default.

## 5. Machine Thermal Behavior

States:

- `normal`: `< 80%` max temp
- `warning`: `80%-100%` max temp
- `degraded`: `100%-110%` max temp (reduced throughput)
- `shutdown`: `> 110%` max temp

Recovery:

- Machines auto-restart when below configurable safe hysteresis threshold.

## 6. Gameplay Effects

- Crop growth multipliers by preferred temp band.
- Player heat stress in extreme ambient zones.
- Heat can be moved intentionally with regulators, coolant loops, and venting to space.

## 7. Validation Targets

- Heat rises from industrial clusters if unmanaged.
- Sealed coolant loop should reduce target room temperature predictably.
- Heat runaway should be possible but recoverable with planned systems.
