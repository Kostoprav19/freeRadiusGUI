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
    │   │   ├── config/             # Spring, Security, Persistence, Thymeleaf, MVC, init
    │   │   ├── controllers/        # @Controller (Account, Admin, Devices, Switches, Logs, Server, Login)
    │   │   ├── services/           # @Service business logic
    │   │   │   ├── filesServices/  # Read/write FreeRADIUS users + clients.conf + log files
    │   │   │   ├── serverServices/ # Restart freeradius, status checks
    │   │   │   ├── shellServices/  # ShellExecutor + ShellCommands constants
    │   │   │   └── mailServices/   # SMTP notifications
    │   │   ├── repositories/       # Spring Data JDBC CrudRepository<T,Integer>
    │   │   ├── domain/             # Spring Data Relational entities: Account, AccountRoleRef, Role, Device, Switch, Log, Server
    │   │   ├── validators/         # Spring Validator implementations
    │   │   ├── interceptors/       # SessionVariablesInterceptor
    │   │   ├── listeners/          # Auth success/failure + startup listeners
    │   │   ├── scheduler/          # @Scheduled tasks
    │   │   ├── constants/          # Views.java — ALL view names live here
    │   │   └── utils/              # OperationResult
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
mise install                        # install pinned Java 17 + Maven 3.9
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
mvn -Dtest=DeviceRepositoryTest test # run a single test
mvn spotless:check                  # lint
mvn spotless:apply                  # format
```

There is **no embedded-servlet entry point** — to run the app locally
you either build the WAR and drop it into an external Tomcat 10.1, or
use the full compose stack (`mise run compose:up`) which builds the
app image and serves it from a Tomcat 10.1 container.

### Containerization

- `Dockerfile` — multi-stage: Maven 3.9/JDK 17 build → Tomcat 10.1/JDK 17 runtime.
  The runtime stage sets `JAVA_OPTS` with two `--add-opens` flags Spring 6.1
  needs under JDK 17 (`java.base/java.lang`, `…/java.lang.reflect`).
  Mirrored in `pom.xml`'s surefire `<argLine>`. Spring Data JDBC does not
  need the `java.lang.invoke` and `java.util` opens that Hibernate 5.6
  required (dropped in Phase 5).
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
- `mailEnabled` (boolean, defaults to `true` when missing), `mailFrom`,
  `mailTo`, `mailSmtpServer` — set `mailEnabled = false` to skip SMTP
  setup at startup and short-circuit `MailService#sendMail()` to a
  SUCCESS no-op (used by the lab profile to avoid the unreachable
  default SMTP host blocking startup).

Credentials and server IPs in this file are **committed to the repo** as
defaults — treat them as local/dev values, not secrets. Do not add real
production credentials here.

## Architecture Conventions

- **Layering**: `Controller → Service (interface + *Impl) → Repository
  (Spring Data JDBC CrudRepository) → Domain entity`. Keep new code
  in this shape; do not call repositories directly from controllers.
- **View names**: always reference through
  `lv.freeradiusgui.constants.Views` constants — do not hard‑code view
  name strings in controllers.
- **Entities**: Spring Data Relational annotations
  (`@Table`, `@Column`, `@Id`, `@MappedCollection`, `@Transient`).
  `LocalDateTime` is read/written natively via JDBC 4.2 — no custom
  `UserType`. Many-to-one FKs use `AggregateReference<T, Integer>` for
  the persisted side, paired with a `@Transient` typed field that is
  hydrated in the service layer (see `Device.aSwitch`, `Log.aSwitch`,
  `Log.device`). The `accounts ↔ roles` many-to-many uses
  `AccountRoleRef` as a `@MappedCollection` join-row, with a
  `@Transient Set<Role> roles` view; setters call `rebuildRoleRefs()` so
  controller/form code keeps working with `Set<Role>`.
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
  boots `PersistenceConfig` with a real HikariCP `DataSource`. Run
  `mise run db:up` before `mise run test`. Repository tests fail when
  the DB is unreachable; service tests that only exercise pure helpers
  (`removeComments`, `parseValue`, …) pass even without MySQL because
  context init only touches the DB when a repository is actually used.
- **`@Transactional` + `@Rollback`** on repository tests keeps MySQL
  state clean — safe to re‑run freely.
- **`web/` directory at repo root** is a legacy IntelliJ IDEA web module
  layout. Maven uses `src/main/webapp/` — ignore `web/` for build
  changes.
- **No `main()` method** — do not try to run this like Spring Boot.
  There is no embedded-servlet Maven plugin anymore (`tomcat7-maven-plugin`
  was dropped in the deps refresh). To run locally: `mise run compose:up`
  (runs the WAR in Tomcat 10.1 inside Docker), or deploy
  `target/freeradiusgui.war` into any external Servlet 5.0+ / Jakarta EE
  9+ container (Tomcat 10.1, or any other compliant Jakarta runtime).
- **JDK 17 + jakarta namespace**: post-Phase-5 the codebase is on
  Spring 6.1.x / Spring Security 6.3.x / Spring Data JDBC **3.3.5**
  (Hibernate is gone) / Thymeleaf 3.1 *with `-spring6` integration
  extras* / jakarta.mail 2.0.2 / Tomcat 10.1 (Servlet 6.0). Spring
  Data 3.3.x is aligned to Spring 6.1.x — do not bump Spring to 6.2
  without bumping Spring Data in lockstep. Tomcat 11 / Servlet 6.1 /
  Jakarta EE 11 also runs on JDK 17, but the version-pin gate isn't
  a JDK question — Spring 6.1 + Spring Security 6.3 are aligned to
  Servlet 6.0 (Jakarta EE 10), not 6.1.
- **`hasRole(...)` prepends `ROLE_` automatically**: in
  `SecurityConfig` the role names are passed bare (`"ADMIN"`,
  `"USER"`) because `.hasRole(X)` and `.hasAnyRole(X, …)` prepend
  `ROLE_` internally. The DB stores roles as `ROLE_ADMIN` /
  `ROLE_USER`. Passing `"ROLE_ADMIN"` here would produce
  `ROLE_ROLE_ADMIN` checks and deny every request to the matched URL
  (fail-closed, not a bypass). The legacy SpEL `.access("hasRole('ROLE_X')")`
  form tolerated the prefix; the new lambda DSL does not.
- **Spring Security 6 dropped the implicit `formLogin().loginPage()`
  permitAll**: in 5.x the configured login page was auto-permitted;
  in 6.x it isn't, and you get an infinite redirect loop. `SecurityConfig`
  has an explicit `requestMatchers("/login").permitAll()` for this —
  don't remove it.
- **`jakarta.annotation.PostConstruct` requires an explicit artifact**:
  removed from the JDK in Java 11 by JEP 320, and `jakarta.annotation`
  lives entirely outside the JDK, so `pom.xml` carries
  `jakarta.annotation:jakarta.annotation-api:2.1.1`. Don't drop it —
  two classes (`WebMVCConfig`, `MailServiceImpl`) import it.
- **`--add-opens` is load-bearing**: Spring 6.1 reflective scans fail
  with `InaccessibleObjectException` on JDK 17 without the two
  `--add-opens` flags (`java.base/java.lang`, `…/java.lang.reflect`)
  in `Dockerfile` `JAVA_OPTS` and surefire `<argLine>`. If a new
  test throws `InaccessibleObjectException` on some other package,
  add a matching `--add-opens` in both places.
- **`jakarta.servlet-api 6.0.0` is pinned to match Tomcat 10.1**:
  Spring 6.1 declares Servlet 5.0+ as its baseline, but Tomcat 10.1
  ships Servlet 6 and exposes `jakarta.servlet.ServletConnection`
  which Spring's reflective scan touches. Pinning the API jar at
  6.0.0 (`provided` scope, runtime supplied by Tomcat) keeps compile
  and runtime aligned. Tomcat 11 / Servlet 6.1 also runs on JDK 17,
  but the actual blocker for moving past 10.1 is that Spring 6.1 is
  aligned to Servlet 6.0; Spring 6.2+ would be the gate for that.
- **Thymeleaf 3.1 removed `#request` / `#session` / `#servletContext` /
  `#response`** from default expression objects, and the
  `ServletContextTemplateResolver` class too. Templates use
  controller-provided model attributes instead (see
  `SessionVariablesInterceptor` for the header badge pattern +
  `LoginController` for `loginError`); `ThymeleafConfig` uses
  `SpringResourceTemplateResolver` from `thymeleaf-spring6` (the
  jakarta-aligned integration jar — same Thymeleaf core 3.1.4).
- **Java 17 source level**: `var`, records, switch expressions,
  text blocks, and sealed classes all compile — but keep new code
  consistent with the existing plain-Java-8 style unless a feature
  materially improves readability (this is a legacy codebase being
  modernized in phases, not a greenfield project).
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
- When adding tests for repositories/services, follow the pattern in
  `DeviceRepositoryTest` / `ClientsConfFileServiceTest`.
- Prefer putting new tests under `src/test/java/...` (see Gotchas).

## Coding Style

- Formatting is enforced by **Spotless** (`mvn spotless:check` /
  `mise run lint`) using `google-java-format 1.26.0` in **AOSP** style —
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
- Spotless/GJF are pinned to the newest JDK 17-compatible line:
  `spotless-maven-plugin` 2.44.5 (2.31.0+ requires JRE 11+) and
  `google-java-format` 1.26.0 (1.27.0+ requires JDK 21). Bumping past
  these requires Phase 4 (JDK 21).

## Pre-commit workflow (mandatory for agents)

Before running `git commit` on anything non-trivial, AI agents MUST
run the `reviewer` subagent on the staged changes and act on its
findings. This is the same gate human reviewers would apply in a PR,
moved earlier so broken diffs never reach the history.

The sequence is:

1. Stage the changes (`git add …`). Review the staged diff yourself
   first (`git diff --staged`) — the reviewer is a second opinion,
   not a first one.
2. Launch the `reviewer` subagent in readonly mode with:
   - the branch / commit range being reviewed,
   - the plan file the work is executing against (if any),
   - a short summary of scope and verification already performed
     (`mvn test` / `mvn spotless:check` / `mise run smoke` / …).
3. If the reviewer returns **BLOCKING** findings, fix them and loop
   back to step 1. Do not commit a diff with open BLOCKING findings.
4. If the reviewer returns only **SUGGESTED** / **NITS**, decide per
   finding whether to fold them into the current commit, defer them
   to a follow-up commit on the same branch, or explicitly skip them
   (with a short reason in the final summary to the user).
5. Only then run `git commit`. Reference the reviewer verdict in the
   session summary so the user can see it was gated.

When the workflow may be skipped:

- Pure docs-only commits to `README.md` / `AGENTS.md` that are trivial
  (typo fixes, one-line wording tweaks) — but still mention the skip.
- Reverts of a commit that was itself reviewer-approved (the revert
  diff is the inverse of an already-audited diff).
- When the user explicitly overrides (`"skip the reviewer, just
  commit"`) — honour that, but call out that it was overridden.

Multi-commit work on a plan: the gate is **per commit** — run the
reviewer before each logically distinct commit, not just once at the
end of a branch. An end-of-phase audit covering the whole branch is
encouraged as an *additional* safety net before push, but it does
not replace the per-commit reviews, because an issue caught at the
end is harder to fix cleanly once it's split across several commits.

## What NOT to do

- Don't convert this to Spring Boot as a side effect of another task.
- Don't commit real passwords / SMTP creds to `config.properties`.
- Don't introduce new framework versions or swap persistence providers
  without an explicit ask.
- Don't shell out directly from services — go through `ShellExecutor`.
- Don't hard‑code FreeRADIUS file paths — read from `config.properties`.
