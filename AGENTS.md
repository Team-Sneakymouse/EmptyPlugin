# Agent instructions (Team Sneakymouse / Lords of Minecraft)

This repo is a **Paper plugin template** (Kotlin, Paper 26.2, Gradle). All coding agents (Cursor, Claude Code, Codex, Antigravity, Hermes, etc.) should follow this file.

<!-- BEGIN TEAM-POLICY MANAGED BOOTSTRAP -->
This project follows the Team-Sneakymouse development policy, kept in a sibling checkout that `.team-policy/pointer.json` names.

The policy root is the first `local_candidates` path in the pointer, resolved from this project's root, that exists. If none exists, clone the pointer's `remote` into the first candidate with the developer's Git credentials and say so. Before implementation work (not for read-only investigation), run `git fetch` in the checkout, then fast-forward it to `origin/<track_branch>` if it is behind and clean; if the fetch fails or local changes block the fast-forward, report it and ask.

Before implementing, read `<policy-root>/README.md`. It is the policy index and says which documents apply to which kind of change. If the policy root can't be resolved or read, stop and report that instead of working from memory or another copy.

Build commands, architecture facts and recorded local exceptions in this file are authoritative for this repository. Where this file and the policy require incompatible actions, say so before expanding the work. Running checks and writing tests are separate decisions: a source edit does not by itself require a new test.
<!-- END TEAM-POLICY MANAGED BOOTSTRAP -->

## Project facts

| | |
|--|--|
| Project | EmptyPlugin (Paper plugin template) |
| Runtime targets | Paper API 26.2, Java/Kotlin JVM 25 |
| Shared Paper utils | SneakyUtils `compileOnly` + runtime `depend` (see team policy `sneakyutils.md`) |
| Deployment | push to `main` triggers publish via `.github/workflows/publish.yml` |
| Build output | `~/.cache/EmptyPlugin-gradle-build` (keeps Gradle off FUSE/NTFS checkouts) |

Custom item look, CraftEngine/pack model id, server resource pack, or ItemDisplay place/orient → read `<policy-root>/policy/paper-items.md`.

<!-- EMPTYPLUGIN-ONLY LOCAL EXCEPTION: remove from repositories created from this template -->
### Template-only local exception — EmptyPlugin only

- **Stricter testing:** while working on this repository as the EmptyPlugin template, if you change behavior under `src/main/`, add or update tests under `src/test/` in the same change and run `./gradlew test` (or `check`). Rationale: the template should demonstrate its test harness. Approver: pilot enrollment 2026-10-18. This exception applies only to the EmptyPlugin repository. It does not transfer to a plugin created from this template; `scripts/rename-template.sh` removes every block between these markers, unless the new repository separately records an approved exception.
<!-- END EMPTYPLUGIN-ONLY LOCAL EXCEPTION -->

## Commands

| Task | Command |
|------|---------|
| Build + unit tests | `./gradlew check` (includes both preflight checks) |
| Publish preflight only | `./gradlew publishPreflight` or `bash scripts/publish-preflight.sh` |
| Agent-instruction preflight | `./gradlew templatePolicyPreflight` (also included in `check`) |
| Unit tests only | `./gradlew test` |
| Local Paper server | `./gradlew runServer` (stages `paper-plugin.yml` server deps from matching `compileOnly` jars) |
| Preview staged deps | `./gradlew resolveRunServerPlugins` |
| Publish (CI) | `./gradlew publishMavenPublicationToSneakyrpRepository` (requires Maven credentials) |

Builds require a **git checkout** with at least one commit (version is derived from `HEAD`).

<!-- EMPTYPLUGIN-ONLY LOCAL EXCEPTION: remove from repositories created from this template -->
Template only: `bash scripts/rename-template.sh <PluginName>` turns this checkout into a new plugin, and `./gradlew templateRenameSmoke` (part of `check` here) tests that script on a scratch copy. Template-only Gradle logic lives in `gradle/template.gradle.kts`, which the script deletes.
<!-- END EMPTYPLUGIN-ONLY LOCAL EXCEPTION -->

## Architecture (keep plugins testable)

Put code where it can be tested without a live server when possible. Only `commands/` exists at the start; add the
other packages when the plugin needs them:

```text
src/main/kotlin/com/<id>/
  EmptyPlugin.kt       # Bootstrap only: config, register commands/listeners
  commands/            # CommandBase, ReloadCommand. Parse args, call services, send messages
  service/             # Rules, calculations, domain logic — no org.bukkit imports
  listener/            # Events → services
  util/                # Plugin-specific pure helpers (do not copy TextUtility here)
src/test/kotlin/com/<id>/   # Mirror packages; *Test.kt next to the unit under test
```

Shared text helpers (`com.sneakyutils.util.TextUtility`) come from the **SneakyUtils**
`compileOnly` dependency — keep the `paper-plugin.yml` server dependency on `SneakyUtils`.

Guidelines:

- **Business logic** belongs in `service/` or `util/`, not in listeners or command `execute` bodies.
- **Commands and listeners** are thin adapters over services.
- Avoid global plugin singletons; prefer constructor parameters (`JavaPlugin`, services) so tests stay simple.
- Classes loaded by MockBukkit (plugin, commands) must be **`open`** in Kotlin.

## Testing

Run `./gradlew check` before finishing. Do not rely on `./gradlew runServer` as the only verification for logic changes.

### What to test how

| Kind of code | Approach |
|--------------|----------|
| `util/`, `service/` (no Bukkit) | Plain **JUnit 5** (no `MockBukkit` import) |
| Permissions, command wiring, enable/disable | **MockBukkit** (`mockbukkit-v26.2`) |
| Full gameplay / integration | Manual **`runServer`** smoke tests |

MockBukkit lifecycle in every MockBukkit test class:

- `@BeforeEach`: `MockBukkit.mock()`
- `@AfterEach`: `MockBukkit.unmock()`

Failing tests are not acceptable. A skipped test does not demonstrate the behavior it skips. If MockBukkit lacks a needed API, use another suitable check and report the evidence gap.

Team-wide Paper rules (threads, reload, public hooks, stable ids, docs) live in `<policy-root>/policy/paper-conventions.md`.
