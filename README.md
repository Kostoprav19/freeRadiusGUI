# freeRadiusGui

A web GUI for administering a [FreeRADIUS](https://freeradius.org/) server. Manage MAC‑based device access,
RADIUS clients (switches), user accounts and auth logs from the browser.

## Features

- Device (MAC) inventory with Accept/Reject access control
- Switch / RADIUS client management — writes `clients.conf`
- Parsing of FreeRADIUS accounting/auth detail logs
- Server status dashboard (FreeRADIUS / Tomcat / MySQL via Docker API)
- Email notifications via SMTP

## Tech Stack

Java 25 (jakarta namespace) · Spring MVC 6.2 · Spring Security 6.4 · Spring Data JDBC 3.4 ·
Thymeleaf 3.1 (+ `thymeleaf-spring6`) · MySQL 8.0 (mysql-connector-j 9.7) · HikariCP 5.1 ·
SLF4J 2 + Logback 1.5 · Maven (WAR packaging) · Tomcat 10.1 (Docker image).

## Requirements

| For              | Need                                                   |
|------------------|--------------------------------------------------------|
| Local build/run  | JDK 25, Maven 3.9 (or [`mise`](https://mise.jdx.dev/)) |
| Runtime          | MySQL 8.0 reachable per `config.properties`            |
| Full operation   | A FreeRADIUS install on the same host / in a sidecar   |

## Quick Start with mise (recommended)

[`mise`](https://mise.jdx.dev/) pins the JDK and Maven versions used by the project (`mise.toml`).

```bash
curl https://mise.jdx.dev/install.sh | sh      # one-time
mise trust                                     # one-time, per clone
mise install                                   # fetches Java 25 + Maven 3.9
```

List available tasks with `mise tasks` and run one with `mise run <task>`.

## Raw Maven (no mise)

```bash
mvn clean package                   # build target/freeradiusgui.war
mvn test                            # run tests
mvn -Dtest=DeviceRepositoryTest test   # single test
mvn spotless:check                  # lint (AOSP style, google-java-format 1.29.0)
mvn spotless:apply                  # auto-fix formatting
```

For local dev, run the app via Docker Compose (`mise run compose:up`); the embedded Tomcat task was
retired with the migration to Tomcat 10.1 / jakarta.

### Linting

Spotless (with `google-java-format 1.29.0` AOSP style) lints every Java file under
`src/main/java` and `src/test/java`. Rules: 4‑space indent, sorted imports, unused imports
removed, trailing whitespace trimmed, files end with a newline.

```bash
mise run lint     # mvn spotless:check — read-only, non-zero on violations
mise run format   # mvn spotless:apply — rewrites files in place
```

## Database Setup

### Option A — MySQL via Docker Compose (recommended for dev)

```bash
# edit lab/.env to tweak credentials / ports if needed
mise run db:up                 # or: (cd lab && docker compose up -d db)
```

Useful tasks:

| Task                    | Description                                                 |
|-------------------------|-------------------------------------------------------------|
| `mise run db:up`        | Start MySQL in the background                               |
| `mise run db:down`      | Stop the stack (keeps the data volume)                      |
| `mise run db:reset`     | Stop stack **and** drop the volume (re‑seeds)               |
| `mise run compose:up`   | Start full stack (app + DB + RADIUS + radclient)            |
| `mise run compose:down` | Stop the full compose stack                                 |
| `mise run smoke`        | Full-lifecycle smoke test (compose up → probe → tear down)  |

### Option B — existing MySQL server

```bash
mysql -u root -p < lab/databaseCreationScript.sql
```

Either option seeds two accounts (password `123456`):

| Login | Role         |
|-------|--------------|
| admin | `ROLE_ADMIN` |
| user  | `ROLE_USER`  |

## Configuration

Runtime settings live in [`src/main/resources/config.properties`](src/main/resources/config.properties):

- `usersfilepath`, `clientsfilepath`, `logfilesdirpath` — FreeRADIUS paths
- `dbDriverClass`, `dbUrl`, `dbUser`, `dbPassword`, pool sizing
- `mailFrom`, `mailTo`, `mailSmtpServer`
- `appVersion` — displayed in the UI footer and login page

The lab compose uses [`lab/config.properties`](lab/config.properties) (mounted as a volume override),
and the production compose uses a similar override from [`deploy/`](deploy/).

## Running in Docker

```bash
# Build the app + FreeRADIUS images
mise run docker:build
```

### Lab (dev) — via Docker Compose

The full dev stack — app, MySQL, FreeRADIUS, and radclient traffic generator — is defined in
[`lab/compose.yaml`](lab/compose.yaml) with overrides in [`lab/.env`](lab/.env) and
[`lab/config.properties`](lab/config.properties).

```bash
mise run compose:up     # = (cd lab && docker compose --profile app up --build)
mise run compose:down   # = (cd lab && docker compose --profile app down)
mise run smoke          # full-lifecycle smoke test (resets DB, probes all pages)
```

### Production — via Docker Compose

Production deployment lives in [`deploy/`](deploy/). It adds HAProxy for TLS termination
(`deploy/haproxy.cfg`, certs in `deploy/certs/`) and drops the dev-only radclient service.
See [`deploy/README.md`](deploy/README.md) for details.

```bash
docker compose -f deploy/compose.yaml up -d
```
