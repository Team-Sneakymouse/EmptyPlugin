# EmptyPlugin

Standard **Paper** plugin template for Team-Sneakymouse / Lords of Minecraft.

Clone this repo (or use it as a GitHub template), rename the project, and start implementing.

## What’s included

- Kotlin + Java 25 toolchain, Gradle wrapper (9.3+)
- `run-paper` for a local Paper `26.2` server (`./gradlew runServer`)
- JUnit 5 + MockBukkit (`mockbukkit-v26.2`) unit test setup under `src/test/kotlin`
- Git-derived versions and `maven-publish` to `https://maven.sneakyrp.com/releases`
- GitHub Actions CI (`check`) and publish workflow (Infisical OIDC → Maven credentials)
- **[AGENTS.md](./AGENTS.md)** — shared instructions for LLM coding agents (Cursor, Claude Code, Codex, etc.)

## Versioning

Builds **require a git checkout** with at least one commit. The version is:

```text
yyyy.MM.dd-<12-char-git-hash>
```

derived from `git show -s --format=%ct:%h --abbrev=12 HEAD` (UTC date + abbreviated hash).

Plugin JAR name: `EmptyPlugin-<version>.jar`.

Maven coordinates (after publish):

```text
io.github.team-sneakymouse:emptyplugin:<version>
```

## Publish flow

On every push to `main`, `.github/workflows/publish.yml`:

1. Builds with Java 25
2. Fetches `MAVEN_USERNAME` / `MAVEN_PASSWORD` from Infisical via OIDC
3. Runs `./gradlew publishMavenPublicationToSneakyrpRepository`

### GitHub secrets (repository-level setup only for private repos)

Public Team-Sneakymouse repositories inherit the following organization secrets; no repository-level setup is needed. Private repositories must configure these secrets in the repository's GitHub Actions settings.

| Secret | Purpose |
|--------|---------|
| `INFISICAL_IDENTITY_ID` | Infisical machine identity for OIDC |
| `INFISICAL_DOMAIN` | Infisical domain (e.g. `https://…`) |

The Infisical identity must allow OIDC from this repository (`Team-Sneakymouse/EmptyPlugin`). Allowlist the new repo in Infisical if the first Actions run fails at the secrets step.

Credentials are passed to Gradle as `sneakyrpUsername` / `sneakyrpPassword` (`PasswordCredentials` for the `sneakyrp` Maven repository).

## Internal dependencies

`mavenCentral()` and the Paper repo are enabled by default. To depend on other Team-Sneakymouse artifacts, uncomment the sneakyrp repository in `build.gradle.kts`:

```kotlin
maven("https://maven.sneakyrp.com/releases")
```

and add a `compileOnly` / `implementation` dependency as needed.

## Rename checklist (new plugin from this template)

1. Rename the GitHub repo / local folder.
2. `settings.gradle.kts` → `rootProject.name = "YourPlugin"`.
3. `build.gradle.kts` → update POM name/description/URLs; set `artifactId` (usually lowercase, e.g. `yourplugin`).
4. Move `com.emptyplugin` → your package (sibling plugins often use `com.yourplugin`).
5. Update `paper-plugin.yml` (`name`, `main`, `description`, `website`).
6. Update JAR naming if you keep `archiveFileName` tied to `rootProject.name`.
7. Point workflow publish task at the same publication name (`maven`) unless you rename it.
8. Ensure Infisical OIDC allowlists the new repo. Public repos inherit the organization secrets; only private repos need repository-level `INFISICAL_IDENTITY_ID` and `INFISICAL_DOMAIN` secrets.
9. Run `./gradlew publishPreflight` (or `bash scripts/publish-preflight.sh`) before the first publish push — catches non-executable `gradlew`, missing wrapper jar, and missing `workflow_dispatch`.
10. Replace this `README.md` with documentation for your plugin (description, setup, usage, and publishing details).

### Publish preflight (agents / CI)

`scripts/publish-preflight.sh` is wired into `./gradlew check`. Run it against any plugin checkout:

```bash
bash /path/to/EmptyPlugin/scripts/publish-preflight.sh /path/to/OtherPlugin
```

## Local build

```bash
./gradlew build
```

## Testing

```bash
./gradlew test          # unit tests only
./gradlew check         # tests + other verification (same gate as CI)
```

Example tests: `TextUtilityTest` (pure logic), `CommandBaseTest` (permissions), `EmptyPluginTest` (MockBukkit load/enable).

## Agent / LLM instructions

Point your tool’s project instructions at **[AGENTS.md](./AGENTS.md)**. Cursor also loads `.cursor/rules/`; Claude Code reads `CLAUDE.md`; GitHub Copilot can use `.github/copilot-instructions.md`.
