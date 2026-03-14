# SMP Plugin

SMP gameplay plugin for PixelCampus.

## Features

- Online and global player statistics leaderboards
- Per-player stats lookup
- Ladder/vine speed boost mechanic while sneaking and looking up
- Action-bar hint for speed ladder usage
- Folia-supported plugin setup (`folia-supported: true`)

## Commands

- `/stats [player|playtime|deaths|streak|kills|blocks|distance]`
  - No argument: online leaderboard (default sort: playtime)
  - Sort keyword: online leaderboard sorted by that metric
  - Player name: single-player stat line
- `/statsall [playtime|deaths|streak|kills|blocks|distance]`
  - Full leaderboard across known players (includes offline players with minimum playtime)

## Permissions

- `smp.stats.online` (default: `true`)
- `smp.stats.all` (default: `op`)
- `smp.speedladder.use` (default: `true`)

## Tech Notes

- Java 21
- Paper API `1.21.11`
- Main class: `org.pixelcampus.smp.Smp`
- Plugin descriptor: `src/main/resources/plugin.yml`

## Build and deploy locally

Requirements:
- Java 21
- Gradle wrapper files present in each plugin project, especially `gradle/wrapper/gradle-wrapper.jar`

Build a plugin:

```bash
cd smp-plugin
./gradlew clean build
```

Test locally. For this, set your local Paper/Folia server plugin path in `build.gradle`::

```groovy
def localServerPluginsDir = file("/absolute/path/to/your/server/plugins")
```

```bash
./gradlew build deployToLocalServer
```