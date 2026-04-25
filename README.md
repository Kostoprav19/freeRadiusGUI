# freeRadiusGui

A web GUI for administering a [FreeRADIUS](https://freeradius.org/) server.
Manage MAC‑based device access, RADIUS clients (switches), user accounts and
auth logs from the browser.

## Features

- Device (MAC) inventory with Accept/Reject access control
- Switch / RADIUS client management — writes `clients.conf`
- Parsing of FreeRADIUS accounting/auth detail logs
- Server status dashboard (FreeRADIUS / Tomcat / MySQL via `pgrep`)
- Email notifications via SMTP

## Tech Stack

Java 17 (jakarta namespace) · Spring MVC 6.1 · Spring Security 6.3 · Hibernate 5.6
*jakarta artifact* · Thymeleaf 3.1 (+ `thymeleaf-spring6`) · MySQL 8.0
(mysql-connector-j 8.4) · HikariCP 5.1 · Logback 1.2 · Maven (WAR packaging) ·
Tomcat 10.1 (Docker image).

> This is **not** Spring Boot — bootstrap is `WebApplicationInitializer`
> (`config/AppInitializer.java`).

## Requirements

| For              | Need                                                       |
|------------------|------------------------------------------------------------|
| Local build/run  | JDK 17, Maven 3.9 (or [`mise`](https://mise.jdx.dev/))     |
| Runtime          | MySQL 8.0 reachable per `config.properties`                |
| Full operation   | A FreeRADIUS install on the same host / in a sidecar       |

## Quick Start with mise (recommended)

[`mise`](https://mise.jdx.dev/) pins the JDK and Maven versions used by the
project (`mise.toml`).

```bash
curl https://mise.jdx.dev/install.sh | sh      # one-time
mise trust                                     # one-time, per clone
mise install                                   # fetches Java 17 + Maven 3.9
```

List available tasks with `mise tasks` and run one with `mise run <task>`.

## Raw Maven (no mise)

```bash
mvn clean package                   # build target/freeradiusgui.war
mvn test                            # run tests
mvn -Dtest=DeviceDAOImplTest test   # single test
mvn spotless:check                  # lint (AOSP style, google-java-format 1.26.0)
mvn spotless:apply                  # auto-fix formatting
```

For local dev, run the app via Docker Compose (`mise run compose:up`); the
embedded Tomcat task was retired with the migration to Tomcat 10.1 / jakarta.

### Linting

Spotless (with `google-java-format 1.26.0` AOSP style) lints every Java file
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
cp lab/.env.example lab/.env   # optional — tweak credentials / ports
mise run db:up                 # or: (cd lab && docker compose up -d db)
```

Useful tasks:

| Task                          | Description                                           |
|-------------------------------|-------------------------------------------------------|
| `mise run db:up`              | Start MySQL in the background                         |
| `mise run db:down`            | Stop the stack (keeps the data volume)                |
| `mise run db:reset`           | Stop stack **and** drop the volume (re‑seeds)         |
| `mise run compose:up`         | Start full stack (app + DB + RADIUS, `--profile app`) |
| `mise run compose:down`       | Stop the full compose stack                           |
| `mise run smoke`              | Full-lifecycle smoke test (compose up → probe → down) |

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

## Running in Docker

```bash
# Build the image
mise run docker:build

# Dev mode — no FreeRADIUS integration, just the UI + DB
docker run --rm -it -p 8080:8080 freeradiusgui:latest

# Full mode — share FreeRADIUS state with the host
docker run --rm -it \
  --name freeradiusgui \
  --pid=host --network=host \
  -v /etc/freeradius:/etc/freeradius \
  -v /var/log/freeradius/radacct:/var/log/freeradius/radacct \
  freeradiusgui:latest
```

### Docker Compose (app + MySQL)

Brings up the app and a matching `mysql` in one go. The whole dev
stack — compose file, `.env`, a lab-only `config.properties` override,
and all FreeRADIUS/radclient fixtures — lives under [`lab/`](lab/).

The `mise` tasks below `cd` into `lab/` for you. Invoking `docker
compose` directly also works from inside `lab/`:

```bash
mise run compose:up     # = (cd lab && docker compose --profile app up --build)
mise run compose:down   # = (cd lab && docker compose --profile app down)
```
