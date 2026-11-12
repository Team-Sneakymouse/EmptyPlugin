# Agent instructions (Team Sneakymouse / Lords of Minecraft)

This repo is a **Paper plugin template** (Kotlin, Paper 26.2, Gradle). All coding agents (Cursor, Claude Code, Codex, Antigravity, Hermes, etc.) should follow this file.

<!-- BEGIN TEAM-POLICY MANAGED BOOTSTRAP -->
This project is enrolled in Team-Sneakymouse development policy via a sibling checkout (see `.team-policy/pointer.json`).

Resolve the policy root: try each `local_candidates` path in the pointer relative to this project root. If none exist, `git clone` the pointer `remote` into the first candidate using the developer's existing Git credentials. If a checkout exists but is behind `origin/<track_branch>`, `git fetch` and fast-forward that branch when safe; if local edits block the update, report and ask.

Before implementing, review `<policy-root>/README.md` as the current policy index and load the linked guidance relevant to the task. At minimum, when authorization or risk is unclear, read `<policy-root>/policy/core.md`; read `<policy-root>/policy/scope-and-testing.md` when classifying risk or changing behavior; and read `<policy-root>/workflows/development.md` for nontrivial multi-step work. For substantial Paper features, persistent world state, or changes spanning components, read `<policy-root>/policy/paper-plugin-maintainability.md`. Read `<policy-root>/policy/sneakyutils.md` when adding or sharing Paper util/manager code. Load optional skills from `<policy-root>/skills/` only when relevant.

Do not substitute remembered rules, a web mirror, or an unlisted path. Follow local build/architecture facts and explicit exceptions below. When host instructions and team policy repeat the same norm, follow the host wording. If they require incompatible actions, surface the conflict before expanding work. Distinguish running checks from implementing tests; a source edit does not automatically require a new test.
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
