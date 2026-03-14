# JumpAndRun Plugin

Jump-and-run helper plugin for PixelCampus parkour gameplay.

## Features

- Sign automation for parkour top records via PlaceholderAPI
- When a sign is written with:
  - line 1: `[1. Platz]`
  - line 2: `<course-name>`
- The plugin fills:
  - line 3: top player for that course
  - line 4: top record time for that course

## Dependencies

- PlaceholderAPI
- Parkour

Defined in `src/main/resources/plugin.yml` as hard dependencies.

## Current Command Status

- `parkourwerte` command class exists (`commands/ParkourWerte.java`)
- It is currently not registered in `onEnable` (registration line is commented out)
- `plugin.yml` has a `parkourwerte` key but no command metadata yet

## Tech Notes

- Java 21
- Paper API `1.21.11`
- Main class: `org.pixelcampus.jumpnrun.Jumpnrun`
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