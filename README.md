# pixelcampus-plugins 

Custom Minecraft plugins for the **PixelCampus** server network. Each plugin lives in its own sub-folder so that it can be developed, built, and released independently.

## Repository structure

```
pixelcampus-plugins/
├── smp-plugin/          # Plugin for the Survival (SMP) server
├── jumpandrun-plugin/   # Plugin for the Jump & Run server
├── .gitignore
├── LICENSE
└── README.md
```

Each plugin folder is a standalone project. Create your project (e.g. with Maven or Gradle) directly inside the matching folder.

## Adding a new plugin

1. Create a new folder at the root of this repository (e.g. `lobby-plugin/`).
2. Set up your project inside that folder.
3. Open a pull request – done!

## Servers

| Folder | Server | Description |
|---|---|---|
| `smp-plugin` | SMP | Survival multiplayer world |
| `jumpandrun-plugin` | Jump & Run | Parkour / jump-and-run world |
