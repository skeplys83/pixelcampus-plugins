# pixelcampus-plugins

Monorepo for PixelCampus Minecraft server plugins.

The repository contains multiple standalone Java plugin projects (Folia/Paper/Bukkit-compatible, Java 21), one folder per server/domain.

## What is in this repo

```
pixelcampus-plugins/
|- smp-plugin/          # Survival plugin project
|- jumpandrun-plugin/   # Jump and Run plugin project
|- .github/workflows/   # One CI/CD workflow file per plugin
|- LICENSE
`- README.md
```

Each plugin folder is a separate Gradle project with its own:
- `build.gradle`
- `settings.gradle`
- `gradlew`/`gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `src/main/java` and `src/main/resources/plugin.yml`

## Build locally (Gradle)

Requirements:
- Java 21
- The Gradle wrapper files in each plugin project, especially `gradle/wrapper/gradle-wrapper.jar`

Build a plugin:

```bash
cd smp-plugin
./gradlew clean build
```

or:

```bash
cd jumpandrun-plugin
./gradlew clean build
```

## Deploy to local dev server

The intended local flow is:

```bash
./gradlew build deployToLocalServer
```

To use this, set your local Paper/Folia test server plugin folder in `build.gradle`:

```groovy
def localServerPluginsDir = file("/absolute/path/to/your/server/plugins")
```

Notes:
- `smp-plugin/build.gradle` already defines `deployToLocalServer`.
- `jumpandrun-plugin/build.gradle` currently has this task template commented out. Uncomment/adapt it before using `deployToLocalServer` there.

Optional for fast local testing:

```bash
./gradlew runServer
```

## GitHub Actions workflow model

This repo uses one workflow file per plugin for build/release and one for release-based deploy:
- `.github/workflows/release-smp.yml`
- `.github/workflows/release-jumpandrun.yml`
- `.github/workflows/deploy-smp-on-release.yml`
- `.github/workflows/deploy-jumpandrun-on-release.yml`

Each workflow triggers independently:
- Release workflows:
	- On `push` of plugin tags (`smp-v*`, `jumpandrun-v*`)
	- On manual `workflow_dispatch`
- Deploy workflows:
	- On GitHub `release.published`
	- Filtered by release tag prefix (`smp-v*` or `jumpandrun-v*`)

High-level workflow steps:
1. Checkout code
2. Setup Java 21
3. Make plugin `scripts/deploy.sh` executable
4. Verify SFTP connectivity from the runner
5. Set runner MTU (workaround for upload stability)
6. Run plugin-specific deploy script

Each deploy script then:
1. Uploads the release jar to server `/plugins/` via SFTP (SSH key from secrets)
2. Calls the Pterodactyl API to notify in-game players

## Adding a new plugin to the monorepo

1. Create `<new-plugin>/` at repo root as a standalone Gradle project.
2. Add a dedicated workflow file under `.github/workflows/` for that plugin.
3. Use `paths` filter so only that plugin's changes trigger its deployment workflow.

## License and usage

This project is licensed under the GNU General Public License v3.0 (GPL-3.0).

GPL allows usage, modification, and redistribution under the same GPL terms.

See `LICENSE` for the full license text.
