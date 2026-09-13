# slop - Superpowers Plugin

A Paper plugin for Minecraft 1.21.8 where players can select a passive superpower.

## Features

- `/power` to open a visual power selector UI
- `/power gui` to reopen the selector UI
- `/power list` to see available powers
- `/power choose <power>` to pick a power
- `/power info` to see your selected power
- `/power ability` to activate your selected power
- `/power clear` to remove your power
- Sneak + right-click with an empty hand to proc abilities quickly
- Automatic re-apply on join and respawn
- Powers are persisted in `config.yml`

## Included powers

- `speedster` - speed and haste
- `titan` - strength and resistance
- `skybound` - jump boost and slow falling

### Active abilities

- `speedster` -> **Dash** (8s cooldown)
- `titan` -> **Shockwave** (14s cooldown)
- `skybound` -> **Sky Launch** (10s cooldown)

## Build

This project uses **Gradle**:

```bash
./gradlew build
```

Compiled jar will be generated under `build/libs/`.

## Server setup

1. Copy the built jar to your Paper server `plugins/` folder.
2. Start/restart the server.
3. Use `/power` in-game.

## Notes

- This plugin currently provides passive powers through potion effects.
- You can extend `PowerType` to add more powers and effect combinations.
