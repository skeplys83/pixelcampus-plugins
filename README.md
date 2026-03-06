# pixelcampus-plugins

Custom Minecraft plugins for the **PixelCampus** server network. Each plugin lives in its own sub-folder so that it can be developed, built, and released independently.

## Repository structure

```
pixelcampus-plugins/
├── smp-plugin/          # Plugin for the Survival (SMP) server
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/de/pixelcampus/smp/
│           │   └── SMPPlugin.java
│           └── resources/
│               └── plugin.yml
├── jumpandrun-plugin/   # Plugin for the Jump & Run server
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/de/pixelcampus/jumpandrun/
│           │   └── JumpAndRunPlugin.java
│           └── resources/
│               └── plugin.yml
├── .gitignore
├── LICENSE
└── README.md
```

## Prerequisites

- **Java 21** (or later)
- **Apache Maven 3.8+**

## Building a plugin

Navigate into the plugin folder and run Maven:

```bash
cd smp-plugin
mvn package
```

The compiled `.jar` will be placed in `smp-plugin/target/`. Copy it to your Paper server's `plugins/` directory.

Repeat the same steps for any other plugin folder (e.g. `jumpandrun-plugin`).

## Adding a new plugin

1. Create a new folder at the root of this repository (e.g. `lobby-plugin/`).
2. Add a `pom.xml` following the same structure as the existing plugins.
3. Add your main class under `src/main/java/de/pixelcampus/<pluginname>/`.
4. Add a `src/main/resources/plugin.yml` that points to your main class.
5. Open a pull request – done!

## Servers

| Folder | Server | Description |
|---|---|---|
| `smp-plugin` | SMP | Survival multiplayer world |
| `jumpandrun-plugin` | Jump & Run | Parkour / jump-and-run world |
