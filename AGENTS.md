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

| Layer        | Tech                                                   |
|--------------|--------------------------------------------------------|
| Build        | Maven (`pom.xml`), packaging = `war`                   |
| Language     | Java 8 (`source/target 1.8`)                           |
| Web          | Spring Web MVC **4.2.3**, Spring Security **4.0.4**    |
| Persistence  | Hibernate **5.1** + MySQL 5.x (`mysql-connector 5.1`)  |
| View         | Thymeleaf 2.1 (+ spring‑security and java8time extras) |
| DB pool      | c3p0, commons-dbcp                                     |
| Logging      | SLF4J + Logback                                        |
| Mail         | `javax.mail` 1.5                                       |
| Tests        | JUnit 4.12, Spring Test, Surefire                      |
| Servlet ctr. | Tomcat 7 (via `tomcat7-maven-plugin`)                  |

This is **not** Spring Boot — bootstrapping is via
`WebApplicationInitializer` (`config/AppInitializer.java`), not a `main()`.

## Repository Layout

```
.
├── pom.xml                         # Maven build
├── databaseCreationScript.sql      # MySQL schema + seed data (admin/user, pw: 123456)
├── files/                          # Sample FreeRADIUS files (clients.conf, auth-detail-*, db.xlsx)
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

Preferred entry point is [`mise`](https://mise.jdx.dev/) (see `mise.toml`):

```bash
mise install                        # install pinned Java 8 + Maven 3.9
mise run build                      # package WAR (skip tests)
mise run test                       # run unit tests
mise run run                        # embedded Tomcat 7 on :8080/freeradiusgui
mise run docker:build               # build Docker image (freeradiusgui:latest)
mise run docker:run                 # run container with host FreeRADIUS mounts
mise run docker:run-dev             # run container without host mounts (DB only)
```

Raw Maven equivalents (no mise needed):

```bash
mvn clean package                   # build target/freeradiusgui.war
mvn tomcat7:run                     # run embedded Tomcat 7 on :8080/freeradiusgui
mvn test                            # run unit tests (Surefire: *Test / *IT / *TestIT)
mvn -Dtest=DeviceDAOImplTest test   # run a single test
```

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

- `compose.yaml` defines two services:
  - `db` — `mysql:5.7` with the `databaseCreationScript.sql` mounted into
    `/docker-entrypoint-initdb.d/`. Pinned to 5.7 because Connector/J
    5.1.38 can't handle MySQL 8's default `caching_sha2_password` auth.
  - `app` — gated behind the `app` Compose profile
    (`docker compose --profile app up`). It bind-mounts
    `config/config.properties` whose `dbUrl` points at `jdbc:mysql://db:3306/...`.
- Credentials live in `.env` (copy `.env.example`); defaults match
  `src/main/resources/config.properties`.
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
- **Entities**: JPA annotations + Hibernate. Use
  `lv.freeradiusgui.utils.CustomLocalDateTime` as the `@Type` for
  `LocalDateTime` columns (Hibernate 5.1 predates native JSR‑310 support
  here).
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
  boots `HybernateConfig` with a real c3p0 DataSource. Run
  `mise run db:up` before `mise run test`. DAO tests fail with
  `CannotCreateTransaction — Could not open Hibernate session` when the
  DB is unreachable; service tests that only exercise pure helpers
  (`removeComments`, `parseValue`, …) pass even without MySQL because
  context init doesn't connect lazily.
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
- **No `main()` method** — do not try to run this like Spring Boot. Use
  `mvn tomcat7:run` or deploy the WAR to an external servlet container
  (Tomcat 7/8 / Java EE 7 compatible).
- **Old Spring 4.x / Thymeleaf 2.x**: APIs differ from Spring 5/6 and
  Thymeleaf 3. Do not copy snippets from modern docs without checking
  they exist in these versions. Avoid upgrading framework versions
  unless explicitly asked — the dep set is tightly coupled (Hibernate
  5.1 ↔ Spring 4.2 ↔ Thymeleaf 2.1 ↔ spring‑security 4.0).
- **Java 8 only**: do not introduce `var`, records, switch expressions,
  `Optional` features from 9+, or other post‑8 syntax.
- **MySQL connector 5.1 / `useSSL=false`**: the JDBC URL format in
  `config.properties` is the 5.1 dialect; do not switch to
  `mysql-connector-j` 8 casually.

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
