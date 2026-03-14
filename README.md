# pixelcampus-plugins

Monorepo for PixelCampus Minecraft plugins.

Each plugin is a standalone Java project (Paper/Folia/Bukkit API, Java 21) with its own Gradle build and release flow.

## What is in this repo

```
pixelcampus-plugins/
|- xyz-plugin/
|- .github/workflows/
|- LICENSE
`- README.md
```

## Build and deploy locally

Requirements:
- Java 21
- Gradle wrapper files present in each plugin project, especially `gradle/wrapper/gradle-wrapper.jar`

Build a plugin:

```bash
cd xyz-plugin
./gradlew clean build
```

Test locally. For this, set your local Paper/Folia server plugin path in `build.gradle`::

```groovy
def localServerPluginsDir = file("/absolute/path/to/your/server/plugins")
```

```bash
./gradlew build deployToLocalServer
```

## GitHub Actions workflow model

Workflow files:
- `.github/workflows/release-smp.yml`
- `.github/workflows/release-jumpandrun.yml`
- `.github/workflows/deploy-smp-on-release.yml`
- `.github/workflows/deploy-jumpandrun-on-release.yml`

Release tag patterns:
- `smp-v*`

Pipeline (simplified):
1. Tag push (example: `smp-v1.0.0`)
2. Matching release workflow builds plugin jar and creates a GitHub Release with that jar attached
3. Publishing that release triggers the matching deploy workflow
4. Deploy workflow downloads the jar asset from the GitHub Release
5. Deploy workflow calls plugin `scripts/deploy.sh`
6. `deploy.sh` uploads the jar via SFTP to Pterodactyl (`/plugins/...jar`) and sends a restart notice via API
7. Plugin jar is on the server (manual restart applies the update)

## Adding a new plugin to the monorepo

1. Create `<new-plugin>/` at repo root as a standalone Gradle project.
2. Add a release workflow (`release-<plugin>.yml`) with a dedicated tag prefix.
3. Add a deploy-on-release workflow (`deploy-<plugin>-on-release.yml`) filtered to that tag prefix.
4. Add a plugin-specific `scripts/deploy.sh` that uploads `DEPLOY_JAR_PATH`.

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0).

See `LICENSE` for the full text.
