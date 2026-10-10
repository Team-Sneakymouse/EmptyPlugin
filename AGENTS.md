# Agent instructions (Team Sneakymouse / Lords of Minecraft)

This repo is a **Paper plugin template** (Kotlin, Paper 26.2, Gradle). All coding agents (Cursor, Claude Code, Codex, Antigravity, Hermes, etc.) should follow this file.

## Commands

| Task | Command |
|------|---------|
| Build + unit tests | `./gradlew check` |
| Unit tests only | `./gradlew test` |
| Local Paper server | `./gradlew runServer` |
| Publish (CI) | `./gradlew publishMavenPublicationToSneakyrpRepository` (requires Maven credentials) |

Builds require a **git checkout** with at least one commit (version is derived from `HEAD`).

## Architecture (keep plugins testable)

Put code where it can be tested without a live server when possible:

```text
src/main/kotlin/<package>/
  EmptyPlugin.kt       # Bootstrap only: config, register commands/listeners
  service/             # Rules, calculations, domain logic — no org.bukkit imports
  command/             # Parse args, call services, send messages
  listener/            # Events → services
  util/                # Pure helpers (see TextUtility)
src/test/kotlin/<package>/   # Mirror packages; *Test.kt next to the unit under test
```

Guidelines:

- **Business logic** belongs in `service/` or `util/`, not in listeners or command `execute` bodies.
- **Commands and listeners** are thin adapters over services.
- Avoid new uses of **`EmptyPlugin.instance`**; prefer constructor parameters (`JavaPlugin`, services) so tests stay simple.
- Classes loaded by MockBukkit (plugin, commands) must be **`open`** in Kotlin.

## Testing policy (required)

1. If you change behavior under `src/main/`, **add or update** tests under `src/test/` in the same change.
2. Run **`./gradlew test`** (or `./gradlew check`) and fix failures before finishing the task.
3. Do not rely on `./gradlew runServer` as the only verification for logic changes.

### What to test how

| Kind of code | Approach |
|--------------|----------|
| `util/`, `service/` (no Bukkit) | Plain **JUnit 5** (see `TextUtilityTest`) |
| Permissions, command wiring, enable/disable | **MockBukkit** (`mockbukkit-v26.2`, see `EmptyPluginTest`, `CommandBaseTest`) |
| Full gameplay / integration | Manual **`runServer`** smoke tests |

MockBukkit lifecycle in every MockBukkit test class:

- `@BeforeEach`: `MockBukkit.mock()`
- `@AfterEach`: `MockBukkit.unmock()`

Skipped tests from unimplemented MockBukkit APIs are acceptable; failing tests are not.

## Code style

- Match existing Kotlin style and package layout.
- Keep diffs minimal; do not refactor unrelated code.
- `CommandBase` sets permission to `{identifier}.command.{name}` using `EmptyPlugin.IDENTIFIER`.

## Definition of done

- [ ] Behavior covered or updated in `src/test/`
- [ ] `./gradlew check` passes locally
- [ ] No secrets committed (Maven credentials via env / Infisical only)
- [ ] `paper-plugin.yml` and POM metadata updated if plugin identity changed

## Human docs

- `README.md` — template usage, publish flow, rename checklist
