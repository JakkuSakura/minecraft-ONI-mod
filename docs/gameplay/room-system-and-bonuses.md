# Room System and Bonuses

## Purpose

Reward intentional base layout and provide readable colony quality signals.

## Room Detection

- Room is valid when:
  - enclosed by solid walls/doors
  - size is within category bounds
  - required blocks are present
  - forbidden blocks are absent

## Initial Room Types

- `Barracks`: sleep quality and stress reduction.
- `MessHall`: food quality bonus and stress reduction.
- `ResearchLab`: research speed bonus.
- `PowerRoom`: reduced stress from noise/heat handling bonuses.
- `SanitationRoom`: hygiene bonus and disease risk reduction.

## Bonus Policy

- Bonuses apply only while requirements remain valid.
- Invalid room transitions immediately show reason in UI.
- Multiple room bonuses stack only if categories allow stacking.

## UX

- Room overlay and inspector:
  - room type
  - valid/invalid
  - missing requirement list
