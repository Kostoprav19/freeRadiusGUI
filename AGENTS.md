# AGENTS.md

Guidance for AI agents working in this repository.

## Project Overview

`freeRadiusGui` is a Spring MVC web application that provides a browser UI
for administering a **FreeRADIUS** server running on versionsthe same host. It
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
| Language     | Java 8 (`source/target 1.8`)                                  |
| Web          | Spring Web MVC **5.3.39**, Spring Security **5.8.15**         |
| Persistence  | Hibernate **5.6.15.Final** + MySQL **8.0** (`mysql-connector-j` **8.4**) |
| View         | Thymeleaf **3.1.4** (+ `thymeleaf-extras-springsecurity5` **3.1.3**) |
| DB pool      | HikariCP **4.0.3** (4.x is the last JDK 8 line)               |
| Logging      | SLF4J 1.7 + Logback 1.1                                       |
| Mail         | `javax.mail` 1.5                                              |
| Tests        | JUnit 4.12, Spring Test, Surefire                             |
| Servlet ctr. | Tomcat 9 at runtime (via Docker image); no embedded plugin    |

This is **not** Spring Boot — bootstrapping is via
`WebApplicationInitializer` (`config/AppInitializer.java`), not a `main()`.

## Repository Layout

```
.
├── pom.xml                         # Maven build
├── databaseCreationScript.sql      # MySQL schema + seed data (admin/user, pw: 123456)
├── lab/                            # Local dev/test stack — run docker compose from here
│   ├── compose.yaml                #   (no compose file at repo root anymore)
│   ├── .env.example                #   tracked template for local .env
│   ├── config.properties           #   Lab-only config override (dbUrl = jdbc:mysql://db:3306/...)
│   ├── freeradius/                 #   FreeRADIUS Dockerfile + overrides, radclient requests
│   ├── dev-seed.sql                #   App DB dev-only seed (switches + devices)
│   └── samples/                    #   Legacy reference data (auth-detail-*, stock clients.conf, db.xlsx)
├── web/WEB-INF/                    # (legacy IDEA web module dir — not used by Maven build)
└── src/
    ├── main/
    │   ├── java/lv/freeradiusgui/
    │   │   ├── config/             # Spring, Security, Hibernate, Thymeleaf, MVC, init
    │   │   ├── controllers/        # @Controller (Account, Admin, Devices, Switches, Logs, Server, Login)
    │   │   ├── services/           # @Service business logic
    │   │   │   ├── filesServices/  # Read/write FreeRADIUS users + clients.conf + log files
    │   │   │   ├── serverServices/ # Restart freeradius, status checks
    │   │   │   ├── shellServices/  # ShellExecutor + ShellCommands constants
    │   │   │   └── mailServices/   # SMTP notifications
    │   │   ├── dao/                # Hibernate DAOs (AbstractGenericBaseDao + per-entity)
    │   │   ├── domain/             # JPA entities: Account, Role, Device, Switch, Log, Server
    │   │   ├── validators/         # Spring Validator implementations
    │   │   ├── interceptors/       # SessionVariablesInterceptor
    │   │   ├── listeners/          # Auth success/failure + startup listeners
    │   │   ├── scheduler/          # @Scheduled tasks
    │   │   ├── constants/          # Views.java — ALL view names live here
    │   │   └── utils/              # CustomLocalDateTime (Hibernate UserType), OperationResult
    │   ├── resources/
    │   │   ├── config.properties   # DB URL, file paths, mail settings
    │   │   ├── messages.properties
    │   │   └── logback.xml
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   ├── web.xml         # Minimal — real init is in AppInitializer
    │       │   └── views/          # Thymeleaf templates (.html)
    │       └── resources/          # Static css/js (Bootstrap, jQuery, datepicker)
    └── test/lv/freeradiusgui/      # NOTE: non-standard path (see "Gotchas")
```

## Common Commands

Preferred entry point is [`mise`](https://mise.jdx.dev/) (see `mise.toml`).
Run `mise tasks` for the authoritative list; the most-used tasks are:

```bash
mise install                        # install pinned Java 8 + Maven 3.9
mise run build                      # mvn clean package (skips tests)
mise run test                       # mvn test (needs MySQL up — see db:up)
mise run verify                     # mvn clean verify (compile + test + package)
mise run lint                       # mvn spotless:check
mise run format                     # mvn spotless:apply (auto-fix formatting)
mise run docker:build               # build Docker image (freeradiusgui:latest)

# Compose stack (from lab/, but mise cd's there for you):
mise run db:up                      # just MySQL, for unit tests
mise run db:down                    # stop stack, keep DB volume
mise run db:reset                   # stop stack AND wipe DB volume (re-seeds)
mise run compose:up                 # full stack: app + DB + FreeRADIUS + radclient
mise run compose:down               # stop the full stack
```

Raw Maven equivalents (no mise needed):

```bash
mvn clean package                   # build target/freeradiusgui.war
mvn test                            # run unit tests (Surefire: *Test / *IT / *TestIT)
mvn -Dtest=DeviceDAOImplTest test   # run a single test
mvn spotless:check                  # lint
mvn spotless:apply                  # format
```

There is **no embedded-servlet entry point** — to run the app locally
you either build the WAR and drop it into an external Tomcat 8/9, or
use the full compose stack (`mise run compose:up`) which builds the
app image and serves it from a Tomcat 9 container.

### Containerization

- `Dockerfile` — multi-stage: Maven 3.9/JDK 8 build → Tomcat 9/JRE 8 runtime.
- The WAR is exploded into `$CATALINA_HOME/webapps/ROOT/` at image build
  time. To override config without rebuilding, bind-mount over
  `/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/config.properties`.
- The image does **not** include the `freeradius` daemon. Shell
  operations (`freeradius`, `killall freeradius`, `pgrep`) only work if
  the container runs with `--pid=host` (and network/volume access to a
  FreeRADIUS install), or alongside a FreeRADIUS sidecar.
- `procps` + `psmisc` are installed to provide `pgrep` / `killall`.

### Compose (MySQL + optional app)

The whole dev/test stack lives under `lab/` — `compose.yaml`,
`.env(.example)`, a lab-only `config.properties` override, the
FreeRADIUS server image + overrides, and the dev-only DB seed. Run
`docker compose` from inside `lab/` (or via the `mise run compose:*` /
`db:*` tasks, which `cd` there for you). The repo root no longer
contains a compose file.

- `lab/compose.yaml` defines:
  - `db` — `mysql:8.0` with `--character-set-server=utf8mb4
    --collation-server=utf8mb4_0900_ai_ci` and
    `../databaseCreationScript.sql` (the app's real schema, at repo
    root) mounted into `/docker-entrypoint-initdb.d/`. `caching_sha2_password`
    is the default auth plugin on 8.0 — `mysql-connector-j` 8.4
    handles it, and the lab JDBC URL already carries
    `allowPublicKeyRetrieval=true` for it. Downgrading the image to
    `mysql:5.7` is no longer supported — the volume holds 8.0 data
    dirs; wipe with `mise run db:reset` first.
  - `app` — gated behind the `app` Compose profile
    (`docker compose --profile app up`). Built from repo root
    (`context: ..`, `dockerfile: Dockerfile`). Bind-mounts
    `lab/config.properties` — a lab-only fork of
    `src/main/resources/config.properties` with `dbUrl` pointed at
    `jdbc:mysql://db:3306/...`. Also bind-mounts
    `lab/freeradius/{clients.conf,users}` and shares
    `/var/log/freeradius/radacct` (named volume `radius-logs`) with
    `freeradius`, so the Logs page sees live data.
  - `freeradius`, `radclient` — dev-only helpers, see README
    "Local FreeRADIUS for development". Dev fixtures (`dev-seed.sql`)
    are mounted into `db`'s initdb.d as `20-dev-seed.sql`.
- Credentials live in `lab/.env` (copy `lab/.env.example`); defaults
  match `src/main/resources/config.properties` and the
  `${VAR:-default}` fallbacks in `compose.yaml`, so the stack works
  with no `.env` at all.
- `databaseCreationScript.sql` is idempotent, but MySQL only runs
  init scripts on an empty data dir — use `mise run db:reset` to wipe
  the volume and re-seed.

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
- `mailFrom`, `mailTo`, `mailSmtpServer`

Credentials and server IPs in this file are **committed to the repo** as
defaults — treat them as local/dev values, not secrets. Do not add real
production credentials here.

## Architecture Conventions

- **Layering**: `Controller → Service (interface + `*Impl`) → DAO
  (interface + `*Impl` extends `AbstractGenericBaseDao`) → Domain entity`.
  Keep new code in this shape; do not call DAOs directly from controllers.
- **View names**: always reference through
  `lv.freeradiusgui.constants.Views` constants — do not hard‑code view
  name strings in controllers.
- **Entities**: JPA annotations + Hibernate. `LocalDateTime` columns
  use `lv.freeradiusgui.utils.CustomLocalDateTime` as the `@Type`.
  Hibernate 5.6 ships native JSR‑310 support, so the custom
  `EnhancedUserType` is retained only for migration minimality — it
  can be dropped whenever entities are willing to switch to plain
  `@Column LocalDateTime` (no Hibernate-6 blocker here).
- **Validators**: one per form/entity in `validators/`, wired via
  `@InitBinder` in the controller (see `DevicesController`).
- **Shell calls**: go through `ShellExecutor` with constants from
  `ShellCommands` — never hard‑code commands elsewhere. Commands are
  Linux‑specific (`pgrep`, `killall`, `freeradius`).
- **Flash messages**: controllers use `RedirectAttributes` with keys
  `msg` and `msgType` (`success` / `danger`), rendered by
  `views/fragments/alert.html`.
- **Security**: config in `config/SecurityConfig.java`; user lookup in
  `SecurityUserDetailsServiceImpl`. Passwords are BCrypt.
- **Scheduling/Async**: enabled globally in `WebMVCConfig`
  (`@EnableAsync`, `@EnableScheduling`). Put periodic jobs in
  `scheduler/ScheduledTasks`.

## Gotchas

- **Tests need a live MySQL**: every test uses
  `@ContextConfiguration(classes = WebMVCConfig.class)` which transitively
  boots `HybernateConfig` with a real HikariCP `DataSource`. Run
  `mise run db:up` before `mise run test`. DAO tests fail with
  `CannotCreateTransaction — Could not open Hibernate session` when the
  DB is unreachable; service tests that only exercise pure helpers
  (`removeComments`, `parseValue`, …) pass even without MySQL because
  context init only touches the DB when a DAO is actually used.
- **`@Transactional` + `@Rollback`** on `DeviceDAOImplTest` means the
  tests don't leave state behind in MySQL — safe to re‑run freely.
- **DAO package casing inconsistency**: both `dao/deviceDAO/` and
  `dao/DeviceDAO/` exist (the latter contains `DeviceDAOImpl.java`).
  This is a pre‑existing quirk — don't refactor it incidentally; it may
  break imports elsewhere.
- **Typo `HybernateConfig`**: intentional file/class name
  (`config/HybernateConfig.java`). Do not rename without a coordinated
  change.
- **`web/` directory at repo root** is a legacy IntelliJ IDEA web module
  layout. Maven uses `src/main/webapp/` — ignore `web/` for build
  changes.
- **No `main()` method** — do not try to run this like Spring Boot.
  There is no embedded-servlet Maven plugin anymore (`tomcat7-maven-plugin`
  was dropped in the deps refresh). To run locally: `mise run compose:up`
  (runs the WAR in Tomcat 9 inside Docker), or deploy
  `target/freeradiusgui.war` into any external Servlet 3.1+ / JSP 2.3
  container (Tomcat 8/9, or Java EE 7+).
- **Spring 5.3 / Thymeleaf 3.1 — still `javax`, still JDK 8**: do not
  mix snippets from Spring 6 / Spring Security 6 / Hibernate 6 /
  Thymeleaf 3.2+ docs — those are all `jakarta` + JDK 17 and will
  either fail to compile or blow up at runtime here. Avoid upgrading
  framework versions unless explicitly asked — the dep set is tightly
  coupled on the last javax line (Hibernate 5.6 ↔ Spring 5.3 ↔
  Thymeleaf 3.1 ↔ spring‑security 5.8 ↔ `thymeleaf-extras-springsecurity5`
  3.1.x). Moving past any of these requires JDK 11+ and a synchronized
  jump of all of them (Phase 3).
- **Thymeleaf 3.1 removed `#request` / `#session` / `#servletContext` /
  `#response`** from default expression objects, and the
  `ServletContextTemplateResolver` class too. Templates use
  controller-provided model attributes instead (see
  `SessionVariablesInterceptor` for the header badge pattern +
  `LoginController` for `loginError`); `ThymeleafConfig` uses
  `SpringResourceTemplateResolver` from `thymeleaf-spring5`.
- **Java 8 only**: do not introduce `var`, records, switch expressions,
  `Optional` features from 9+, or other post‑8 syntax.
- **JDBC URL params**: the JDBC URL already carries
  `useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` — required
  combo for `mysql-connector-j` 8.x against either MySQL 5.7
  (`mysql_native_password`, no TLS needed locally) or MySQL 8
  (`caching_sha2_password`, which needs `allowPublicKeyRetrieval` when the
  connection isn't TLS-protected). Don't drop any of these without
  verifying against both server versions.
- **Driver class is `com.mysql.cj.jdbc.Driver`** (Connector/J 8 series),
  not the legacy `com.mysql.jdbc.Driver`. Kept that way deliberately;
  do not "fix" it back.

## Testing

- Framework: JUnit 4 + `spring-test`.
- Surefire includes: `**/*IT.java`, `**/*TestIT.java`, `**/*Test.java`.
- When adding tests for DAOs/services, follow the pattern in
  `DeviceDAOImplTest` / `ClientsConfigFileServiceImplTest`.
- Prefer putting new tests under `src/test/java/...` (see Gotchas).

## Coding Style

- Formatting is enforced by **Spotless** (`mvn spotless:check` /
  `mise run lint`) using `google-java-format 1.7` in **AOSP** style —
  4-space indent, braces on same line, specific import order. It lints
  every Java file under `src/main/java` and `src/test/java`, no ratchet.
- Run `mvn spotless:apply` (or `mise run format`) before committing new
  Java code to auto-fix formatting, imports, trailing whitespace, and
  missing trailing newlines.
- Do **not** add narrating comments (`// autowire service`, etc.). The
  existing `/** Created by X on DATE */` headers are legacy IDE
  boilerplate — don't propagate them to new files.
- Use SLF4J (`LoggerFactory.getLogger(getClass())`) for logging, never
  `System.out` / `printStackTrace` in new code (some legacy code does;
  don't extend that pattern).
- Keep controller methods thin — delegate to services.
- Spotless/google-java-format versions are pinned to the last JDK 8
  compatible releases (plugin 2.27.2, formatter 1.7). Do not upgrade
  without also bumping the project to JDK 11+.

## What NOT to do

- Don't convert this to Spring Boot as a side effect of another task.
- Don't commit real passwords / SMTP creds to `config.properties`.
- Don't introduce new framework versions or swap persistence providers
  without an explicit ask.
- Don't shell out directly from services — go through `ShellExecutor`.
- Don't hard‑code FreeRADIUS file paths — read from `config.properties`.
