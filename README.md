# freeRadiusGui

A web GUI for administering a [FreeRADIUS](https://freeradius.org/) server.
Manage MAC‑based device access, RADIUS clients (switches), user accounts and
auth logs from the browser; the app edits FreeRADIUS config files
(`/etc/freeradius/users`, `/etc/freeradius/clients.conf`) and can restart the
`freeradius` service after changes.

## Features

- Device (MAC) inventory with Accept/Reject access control
- Switch / RADIUS client management — writes `clients.conf`
- Account management with Spring Security (BCrypt, role‑based)
- Parsing of FreeRADIUS accounting/auth detail logs
- Server status dashboard (FreeRADIUS / Tomcat / MySQL via `pgrep`)
- Email notifications via SMTP

## Tech Stack

Java 8 · Spring MVC 4.2 · Spring Security 4.0 · Hibernate 5.1 · Thymeleaf 2.1
· MySQL 5.x · Maven (WAR packaging) · Tomcat 7/8.5/9.

> This is **not** Spring Boot — bootstrap is `WebApplicationInitializer`
> (`config/AppInitializer.java`).

## Requirements

| For              | Need                                                       |
|------------------|------------------------------------------------------------|
| Local build/run  | JDK 8, Maven 3.9 (or [`mise`](https://mise.jdx.dev/))      |
| Runtime          | MySQL 5.x reachable per `config.properties`                |
| Full operation   | A FreeRADIUS install on the same host / in a sidecar       |

## Quick Start with mise (recommended)

[`mise`](https://mise.jdx.dev/) pins the JDK and Maven versions used by the
project (`mise.toml`).

```bash
curl https://mise.jdx.dev/install.sh | sh      # one-time
mise trust                                     # one-time, per clone
mise install                                   # fetches Java 8 + Maven 3.9
```

Common tasks (run `mise tasks` to list all):

| Task                    | Description                                               |
|-------------------------|-----------------------------------------------------------|
| `mise run build`        | Compile and package (`target/freeradiusgui.war`), no tests|
| `mise run test`         | Run unit tests (JUnit 4 / Surefire)                       |
| `mise run verify`       | Full compile + test + package                             |
| `mise run run`          | Launch on embedded Tomcat 7 at http://localhost:8080/freeradiusgui |
| `mise run clean`        | `mvn clean`                                               |
| `mise run lint`         | Lint all Java files (Spotless + google-java-format AOSP)  |
| `mise run format`       | Auto-fix formatting & imports on all Java files           |
| `mise run docker:build` | Build the Docker image (`freeradiusgui:latest`)           |
| `mise run docker:run`   | Run the container with host FreeRADIUS mounts             |
| `mise run docker:run-dev` | Run the container without host mounts (DB‑only mode)    |
| `mise run docker:shell` | Open a shell inside a running container                   |
| `mise run docker:stop`  | Stop the running container                                |

## Raw Maven (no mise)

```bash
mvn clean package                   # build target/freeradiusgui.war
mvn test                            # run tests
mvn tomcat7:run                     # embedded Tomcat at :8080/freeradiusgui
mvn -Dtest=DeviceDAOImplTest test   # single test
mvn spotless:check                  # lint (AOSP style, google-java-format 1.7)
mvn spotless:apply                  # auto-fix formatting
```

### Linting

Spotless (with `google-java-format 1.7` AOSP style) lints every Java file
under `src/main/java` and `src/test/java`. Rules: 4‑space indent, sorted
imports, unused imports removed, trailing whitespace trimmed, files end
with a newline.

```bash
mise run lint     # mvn spotless:check — read-only, non-zero on violations
mise run format   # mvn spotless:apply — rewrites files in place
```

## Database Setup

### Option A — MySQL via Docker Compose (recommended for dev)

```bash
cp .env.example .env        # optional — tweak credentials / ports
mise run db:up              # or: docker compose up -d db
```

This spins up `mysql:5.7` with DB `freeradiusgui`, user `freeradius`,
password `koJasmoJas2` (matching the defaults in `config.properties`), and
seeds schema + accounts from `databaseCreationScript.sql` on first boot.
MySQL 5.7 is used because the project's JDBC driver
(`mysql-connector-java 5.1.38`) predates MySQL 8's default auth plugin.

Useful tasks:

| Task                   | Description                                  |
|------------------------|----------------------------------------------|
| `mise run db:up`       | Start MySQL in the background                |
| `mise run db:logs`     | Tail MySQL logs                              |
| `mise run db:shell`    | Open a `mysql` client inside the container   |
| `mise run db:down`     | Stop the stack (keeps the data volume)       |
| `mise run db:reset`    | Stop stack **and** drop the volume (re‑seeds)|
| `mise run compose:up`  | Start full stack (app + DB, `--profile app`) |
| `mise run compose:down`| Stop the full stack                          |

### Option B — existing MySQL server

```bash
mysql -u root -p < databaseCreationScript.sql
```

Either option seeds two accounts (password `123456`):

| Login | Role         |
|-------|--------------|
| admin | `ROLE_ADMIN` |
| user  | `ROLE_USER`  |

## Configuration

Runtime settings live in
[`src/main/resources/config.properties`](src/main/resources/config.properties):

- `usersfilepath`, `clientsfilepath`, `logfilesdirpath` — FreeRADIUS paths
- `dbDriverClass`, `dbUrl`, `dbUser`, `dbPassword`, pool sizing
- `mailFrom`, `mailTo`, `mailSmtpServer`

The file is loaded from the classpath at startup (`config/AppConfig.java`).
There is **no** environment‑variable or system‑property override — change
the file and rebuild, or (for Docker) bind‑mount over the copy inside the
exploded WAR (see below).

> Defaults checked into the repo are dev values. Do not commit real
> production credentials here.

## Running in Docker

Two options:

- **Just the app** — use the [`Dockerfile`](Dockerfile) directly against an
  externally managed MySQL.
- **App + MySQL together** — use [`compose.yaml`](compose.yaml) with the
  `app` profile (`mise run compose:up`). The app container reads
  [`config/config.properties`](config/config.properties), which points
  `dbUrl` at the compose `db` service instead of `localhost`.

A multi‑stage [`Dockerfile`](Dockerfile) is provided:

- **Build stage:** `maven:3.9-eclipse-temurin-8`
- **Runtime stage:** `tomcat:9.0-jdk8-temurin` — Tomcat 9 is
  backwards‑compatible with the Servlet 3.1 API this project targets
  (Tomcat 8.5 is EOL).
- The WAR is **exploded** into `$CATALINA_HOME/webapps/ROOT/` so
  `config.properties` can be overridden without rebuilding.
- `procps` + `psmisc` are installed to provide `pgrep` / `killall`.

### Build & run

```bash
# Build the image
mise run docker:build
# or: docker build -t freeradiusgui:latest .

# Dev mode — no FreeRADIUS integration, just the UI + DB
mise run docker:run-dev

# Full mode — share FreeRADIUS state with the host
docker run --rm -it \
  --name freeradiusgui \
  --pid=host --network=host \
  -v /etc/freeradius:/etc/freeradius \
  -v /var/log/freeradius/radacct:/var/log/freeradius/radacct \
  freeradiusgui:latest
```

Then visit **http://localhost:8080/**.

### Overriding config at runtime

Bind‑mount a customised `config.properties` over the one inside the
exploded WAR:

```bash
docker run --rm -it \
  -p 8080:8080 \
  -v "$PWD/config.properties":/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/config.properties:ro \
  freeradiusgui:latest
```

### Operational notes

- **FreeRADIUS is not installed in the image** (one‑process‑per‑container).
  To make the "restart freeradius" / status checks work, run the container
  with `--pid=host` alongside a FreeRADIUS install on the host, or use a
  sidecar container and share PID / volumes.
- **MySQL is not in the image** either. Point `dbUrl` at a reachable host
  (a real server, a Docker Compose service, or use `--network=host`).
- Tune the JVM via `-e JAVA_OPTS="..."` (default:
  `-Xms256m -Xmx512m -Duser.timezone=UTC`).

## Project Layout

```
src/main/java/lv/freeradiusgui/
├── config/        # Spring / Security / Hibernate / Thymeleaf / MVC wiring
├── controllers/   # @Controller classes
├── services/      # Business logic (+ filesServices, serverServices, shellServices, mailServices)
├── dao/           # Hibernate DAOs on top of AbstractGenericBaseDao
├── domain/        # JPA entities: Account, Role, Device, Switch, Log, Server
├── validators/    # Spring Validator implementations
├── interceptors/  # SessionVariablesInterceptor
├── listeners/     # Auth success/failure + startup
├── scheduler/     # @Scheduled tasks
├── constants/     # Views.java — central view-name registry
└── utils/         # CustomLocalDateTime (Hibernate UserType), OperationResult
src/main/resources/    # config.properties, messages.properties, logback.xml
src/main/webapp/       # WEB-INF/views (Thymeleaf) + static resources (Bootstrap, jQuery)
databaseCreationScript.sql  # MySQL schema + seed data
Dockerfile · .dockerignore · mise.toml
```

Agent‑oriented contributor notes are in [`AGENTS.md`](AGENTS.md).

## License

No license file is currently included in the repository.
