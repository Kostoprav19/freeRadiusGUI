# AGENTS.md

Guidance for AI agents working in this repository.

## Project Overview

`freeRadiusGui` is a Spring MVC web application that provides a browser UI
for administering a **FreeRADIUS** server running on the same host. It
manages MAC‑based device access, RADIUS clients (switches), users/accounts,
auth logs, and can restart the FreeRADIUS service after config changes.

The app reads/writes the FreeRADIUS configuration files on disk
(`/etc/freeradius/users`, `/etc/freeradius/clients.conf`) and parses log
files from `/var/log/freeradius/radacct` — so it expects to run on the
same machine as FreeRADIUS (Linux), with permissions to execute
`freeradius` / `killall freeradius` via shell.

## Tech Stack

| Layer        | Tech                                                          |
|--------------|---------------------------------------------------------------|
| Build        | Maven (`pom.xml`), packaging = `war`                          |
| Language     | Java 17 (`<release>17</release>`), jakarta namespace          |
| Web          | Spring Web MVC **6.1.14**, Spring Security **6.3.4**          |
| Persistence  | Spring Data JDBC **3.3.5** + MySQL **8.0** (`mysql-connector-j` **8.4**) |
| View         | Thymeleaf **3.1.4** (+ `thymeleaf-spring6` + `thymeleaf-extras-springsecurity6` **3.1.3**) |
| DB pool      | HikariCP **5.1.0**                                            |
| Logging      | SLF4J 1.7 + Logback 1.2.13                                    |
| Mail         | `jakarta.mail` 2.0.2                                          |
| Tests        | JUnit 4.12, Spring Test, Surefire                             |
| Servlet ctr. | Tomcat 10.1 at runtime (via Docker image); no embedded plugin |

This is **not** Spring Boot — bootstrapping is via
`WebApplicationInitializer` (`config/AppInitializer.java`), not a `main()`.

## Repository Layout

```
.
├── pom.xml                         # Maven build
├── databaseCreationScript.sql      # MySQL schema + seed (admin/user, pw: 123456)
├── lab/                            # Local dev/test stack — `docker compose` runs from here
│   ├── compose.yaml, .env.example, config.properties
│   ├── freeradius/                 # FreeRADIUS Dockerfile + overrides, radclient requests
│   ├── dev-seed.sql                # App DB dev-only seed (switches + devices)
│   └── samples/                    # Legacy reference data
├── web/WEB-INF/                    # legacy IntelliJ web module dir — NOT used by Maven
└── src/
    ├── main/java/lv/freeradiusgui/
    │   ├── config/                 # Spring, Security, Persistence, Thymeleaf, MVC, init
    │   ├── controllers/            # @Controller (Account, Admin, Devices, Switches, Logs, Server, Login)
    │   ├── services/{filesServices,serverServices,shellServices,mailServices}/
    │   ├── repositories/           # Spring Data JDBC CrudRepository<T,Integer>
    │   ├── domain/                 # Spring Data Relational entities (Account, AccountRoleRef, Role, Device, Switch, Log, Server)
    │   ├── validators/, interceptors/, listeners/, scheduler/, constants/, utils/
    ├── main/resources/             # config.properties, messages.properties, logback.xml
    ├── main/webapp/WEB-INF/        # web.xml (minimal — init in AppInitializer), views/*.html
    └── test/lv/freeradiusgui/      # NOTE: non-standard path (see "Gotchas")
```

## Common Commands

Preferred entry point is [`mise`](https://mise.jdx.dev/) (see `mise.toml`).
Run `mise tasks` for the authoritative list; the most-used tasks are:

```bash
mise install                        # pinned Java 17 + Maven 3.9
mise run build | test | verify      # mvn clean package | test | clean verify
mise run lint  | format             # mvn spotless:check | apply
mise run docker:build               # build freeradiusgui:latest
mise run db:up | db:down | db:reset # MySQL only (db:reset wipes volume + re-seeds)
mise run compose:up | compose:down  # full stack: app + DB + FreeRADIUS + radclient
```

Raw Maven works too (`mvn clean package`, `mvn test`,
`mvn -Dtest=DeviceRepositoryTest test`). No embedded-servlet entry
point — run via `mise run compose:up` or deploy the WAR to an
external Tomcat 10.1.

### Containerization

- `Dockerfile` — multi-stage: Maven 3.9/JDK 17 build → Tomcat 10.1/JDK 17
  runtime. `JAVA_OPTS` carries two `--add-opens` flags Spring 6.1 needs on
  JDK 17 (`java.base/java.lang`, `…/java.lang.reflect`); mirrored in
  surefire `<argLine>`.
- WAR is exploded into `$CATALINA_HOME/webapps/ROOT/`. Override config
  by bind-mounting `/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/config.properties`.
- The image does **not** ship the `freeradius` daemon. Shell ops
  (`freeradius`, `killall`, `pgrep`) need `--pid=host` + access to a
  FreeRADIUS install (or a sidecar). `procps` + `psmisc` provide
  `pgrep` / `killall`.

### Compose (MySQL + optional app)

The whole dev/test stack lives under `lab/` — `compose.yaml`,
`.env(.example)`, lab-only `config.properties`, FreeRADIUS image +
overrides, dev-only DB seed. Run `docker compose` from inside `lab/`
(or via `mise run compose:* / db:*`). No compose file at repo root.

- `lab/compose.yaml` services:
  - `db` — `mysql:8.0` (utf8mb4_0900_ai_ci) mounting
    `../databaseCreationScript.sql` into `/docker-entrypoint-initdb.d/`.
    `caching_sha2_password` (8.0 default) is handled by
    `mysql-connector-j` 8.4 + `allowPublicKeyRetrieval=true` in the lab
    JDBC URL. Downgrading to `mysql:5.7` is unsupported (volume is 8.0).
  - `app` — gated behind the `app` profile, built from repo root.
    Bind-mounts `lab/config.properties` (lab fork pointing `dbUrl` at
    `jdbc:mysql://db:3306/...`) and `lab/freeradius/{clients.conf,users}`;
    shares `/var/log/freeradius/radacct` (named volume `radius-logs`)
    with `freeradius` so the Logs page sees live data.
  - `freeradius`, `radclient` — dev-only helpers; see README.
- Credentials live in `lab/.env` (copy `lab/.env.example`); defaults
  match `src/main/resources/config.properties` + `${VAR:-default}`
  fallbacks in `compose.yaml`, so the stack works with no `.env`.
- `databaseCreationScript.sql` is idempotent, but MySQL only runs init
  scripts on an empty data dir — use `mise run db:reset` to wipe + re-seed.

### Database bootstrap

```bash
mysql -u root -p < databaseCreationScript.sql
```

Seeds accounts: `admin` (ROLE_ADMIN) and `user` (ROLE_USER), both with
password `123456` (bcrypt hashed in the script).

## Configuration

Runtime config lives in `src/main/resources/config.properties`:

- `usersfilepath`, `clientsfilepath`, `logfilesdirpath` — FreeRADIUS paths
- `dbUrl`, `dbUser`, `dbPassword`, pool sizing
- `mailEnabled` (boolean, defaults to `false` — opt-in), `mailFrom`,
  `mailTo`, `mailSmtpServer`. When `false`, `MailService#init()` skips
  SMTP setup and `sendMail()` short-circuits to a SUCCESS no-op, so an
  unreachable `mailSmtpServer` cannot block startup.

Credentials and server IPs in this file are **committed to the repo** as
defaults — treat them as local/dev values, not secrets. Never add real
production credentials.

## Architecture Conventions

- **Layering**: `Controller → Service (interface + *Impl) → Repository
  (Spring Data JDBC `CrudRepository`) → Domain entity`. Don't call
  repositories directly from controllers.
- **View names**: always reference `lv.freeradiusgui.constants.Views`
  constants — never hard‑code view name strings in controllers.
- **Entities**: Spring Data Relational annotations (`@Table`, `@Column`,
  `@Id`, `@MappedCollection`, `@Transient`). `LocalDateTime` is read/written
  natively via JDBC 4.2 — no custom `UserType`. Many-to-one FKs use
  `AggregateReference<T, Integer>` for the persisted side, paired with a
  `@Transient` typed field hydrated in the service layer (see
  `Device.aSwitch`, `Log.aSwitch`, `Log.device`). The `accounts ↔ roles`
  many-to-many uses `AccountRoleRef` as a `@MappedCollection` join-row
  with a `@Transient Set<Role> roles` view; setters call
  `rebuildRoleRefs()` so controller/form code keeps working with `Set<Role>`.
- **Validators**: one per form/entity in `validators/`, wired via
  `@InitBinder` (see `DevicesController`).
- **Shell calls**: go through `ShellExecutor` with constants from
  `ShellCommands` — never hard‑code commands. Linux-specific.
- **Flash messages**: `RedirectAttributes` with keys `msg` and `msgType`
  (`success` / `danger`), rendered by `views/fragments/alert.html`.
- **Security**: config in `config/SecurityConfig.java`; user lookup in
  `SecurityUserDetailsServiceImpl`. Passwords are BCrypt.
- **Scheduling/Async**: enabled globally in `WebMVCConfig` (`@EnableAsync`,
  `@EnableScheduling`). Periodic jobs go in `scheduler/ScheduledTasks`.

## Gotchas

- **Tests need a live MySQL**: every test boots `WebMVCConfig` →
  `PersistenceConfig` with a real HikariCP `DataSource`. Run
  `mise run db:up` before `mise run test`. Repository tests fail without
  DB; pure-helper service tests pass (DB is touched lazily). Repository
  tests use `@Transactional` + `@Rollback`, so re-running is safe.
- **`web/` at repo root** is a legacy IntelliJ web module dir. Maven
  uses `src/main/webapp/` — ignore `web/` for build changes.
- **No `main()` and no embedded-servlet plugin**. Run via
  `mise run compose:up`, or drop the WAR into any Servlet 5.0+ /
  Jakarta EE 9+ container.
- **Version pin (post-Phase-5)**: Spring 6.1.x / Security 6.3.x /
  Spring Data JDBC **3.3.5** / Thymeleaf 3.1 + `-spring6` / jakarta.mail
  2.0.2 / Tomcat 10.1 (Servlet 6.0). Spring Data 3.3.x is aligned to
  Spring 6.1.x — bump them in lockstep. Tomcat 11 / Servlet 6.1 needs
  Spring 6.2+ first.
- **`hasRole(...)` auto-prepends `ROLE_`**: pass bare names (`"ADMIN"`)
  in `SecurityConfig`. Passing `"ROLE_ADMIN"` produces `ROLE_ROLE_ADMIN`
  checks (fail-closed). The legacy SpEL `.access("hasRole('ROLE_X')")`
  tolerated the prefix; the lambda DSL does not.
- **Spring Security 6 dropped implicit `formLogin` permitAll**:
  `SecurityConfig` has an explicit `requestMatchers("/login").permitAll()`
  to avoid an infinite redirect loop — don't remove it.
- **`jakarta.annotation.PostConstruct` needs an explicit artifact**:
  `pom.xml` carries `jakarta.annotation-api:2.1.1` (used by `WebMVCConfig`,
  `MailServiceImpl`). Don't drop it.
- **`--add-opens` is load-bearing**: Spring 6.1 reflective scans fail
  with `InaccessibleObjectException` on JDK 17 without the two flags in
  `Dockerfile` `JAVA_OPTS` and surefire `<argLine>`. If a new test
  throws `InaccessibleObjectException` on another package, add a matching
  `--add-opens` in **both** places.
- **`jakarta.servlet-api 6.0.0` pinned `provided`** to match Tomcat 10.1
  (Spring 6.1 reflective-scans Servlet 6's `ServletConnection`).
- **Thymeleaf 3.1 removed `#request`/`#session`/`#servletContext`/`#response`**
  and `ServletContextTemplateResolver`. Templates use controller-provided
  model attributes (`SessionVariablesInterceptor`, `LoginController`'s
  `loginError`); `ThymeleafConfig` uses `SpringResourceTemplateResolver`
  from `thymeleaf-spring6`.
- **JDBC URL params required**:
  `useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` — needed
  by `mysql-connector-j` 8.x against MySQL 8 (`caching_sha2_password`
  needs `allowPublicKeyRetrieval` when not TLS-protected).
- **Driver is `com.mysql.cj.jdbc.Driver`** (Connector/J 8), not legacy
  `com.mysql.jdbc.Driver`. Do not "fix" it back.
- **Java 17 source**: `var`, records, switch expressions, text blocks
  compile — but keep new code consistent with existing style unless a
  feature materially helps.

## Testing

- JUnit 4 + `spring-test`. Surefire includes `**/*IT.java`,
  `**/*TestIT.java`, `**/*Test.java`.
- New repository/service tests follow `DeviceRepositoryTest` /
  `ClientsConfFileServiceTest`.
- Put new tests under `src/test/java/...` (see Gotchas).

## Coding Style

- Formatting enforced by **Spotless** (`mvn spotless:check` /
  `mise run lint`) using `google-java-format 1.26.0` in **AOSP** style
  (4-space indent, braces same line, specific import order); no ratchet.
  Run `mvn spotless:apply` (or `mise run format`) before committing.
- Do **not** add narrating comments. Legacy `/** Created by X on DATE */`
  headers are IDE boilerplate — don't propagate them.
- Use SLF4J (`LoggerFactory.getLogger(getClass())`); never `System.out` /
  `printStackTrace` in new code.
- Keep controllers thin; delegate to services.
- Spotless/GJF pinned to the newest JDK 17-compatible line
  (`spotless-maven-plugin` 2.44.5, `google-java-format` 1.26.0); bumping
  past these requires a JDK 21 phase.

## Agent setup (architect → coder → reviewer)

Non-trivial changes use a **three-agent workflow**. Canonical prompts
live in `agents/` at repo root (vendor-neutral); `.cursor/agents`,
`.claude/agents`, and `.codex/agents` are symlinks to it so Cursor,
Claude Code, and Codex CLI all see the same definitions:

- **`architect`** (`claude-opus-4-7`, read-only) — surveys options,
  writes a phased plan under `.cursor/plans/<slug>.plan.md` (YAML
  `todos` + per-phase changes / verification / out-of-scope /
  rollback). Does **not** write code.
- **`coder`** (`gpt-5.3-codex`, read/write) — implements **one phase
  at a time**, strictly in scope, runs verification, updates the
  plan's `todos` to `completed`, stages the diff.
- **`reviewer`** (`claude-4.6-sonnet`, read-only) — independent second
  opinion in plan-review and code-review modes. Different model
  family from the coder so single-model blind spots can't slip through.

### Standard flow

1. `architect` writes the plan; `reviewer` (plan-review) gates it
   (BLOCKING → architect amends).
2. `coder` implements one phase, runs verification, stages the diff;
   `reviewer` (code-review) gates the staged diff (BLOCKING → coder
   fixes and re-runs; never commit with open BLOCKING findings).
3. Commit: conventional-commits prefix, imperative subject, body
   explains *why* + plan phase id + verification results. **No
   tool-attribution trailers** (`Made-with: Cursor`, `Co-authored-by:
   …[bot]`, etc.). Reference the verdict in the session summary.

The gate is **per commit**. May be skipped for trivial docs-only
commits (mention the skip), reverts of an already-approved commit, or
explicit user override (call it out).

## What NOT to do

- Don't convert this to Spring Boot as a side effect of another task.
- Don't commit real passwords / SMTP creds to `config.properties`.
- Don't introduce new framework versions or swap persistence providers
  without an explicit ask.
- Don't shell out directly from services — go through `ShellExecutor`.
- Don't hard‑code FreeRADIUS file paths — read from `config.properties`.
