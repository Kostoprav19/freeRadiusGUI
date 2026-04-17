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

List available tasks with `mise tasks` and run one with `mise run <task>`.

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

Useful tasks:

| Task                   | Description                                  |
|------------------------|----------------------------------------------|
| `mise run db:up`       | Start MySQL in the background                |
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

Brings up the app and a matching `mysql:5.7` in one go. The app container
reads [`config/config.properties`](config/config.properties), which points
`dbUrl` at the compose `db` service.

```bash
mise run compose:up     # docker compose --profile app up --build
mise run compose:down   # docker compose --profile app down
```
