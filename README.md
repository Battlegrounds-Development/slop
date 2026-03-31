# slop - Superpowers Plugin

A Paper plugin for Minecraft 1.21.11 where players can select a passive superpower.

## Features

- `/power list` to see available powers
- `/power choose <power>` to pick a power
- `/power info` to see your selected power
- `/power clear` to remove your power
- Automatic re-apply on join and respawn
- Powers are persisted in `config.yml`

## Included powers

- `speedster` - speed and haste
- `titan` - strength and resistance
- `skybound` - jump boost and slow falling

## Build

```bash
mvn clean package
```

Compiled jar will be generated under `target/`.

## Server setup

1. Copy the built jar to your Paper server `plugins/` folder.
2. Start/restart the server.
3. Use `/power list` in-game.

## Notes

- This plugin currently provides passive powers through potion effects.
- You can extend `PowerType` to add more powers and effect combinations.

