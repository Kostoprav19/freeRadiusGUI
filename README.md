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
· MySQL (mysql-connector-j 8.4) · HikariCP 4.0 · Maven (WAR packaging) ·
Tomcat 9 (Docker image).

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
mvn -Dtest=DeviceDAOImplTest test   # single test
mvn spotless:check                  # lint (AOSP style, google-java-format 1.7)
mvn spotless:apply                  # auto-fix formatting
```

For local dev, run the app via Docker Compose (`mise run compose:up`); the
embedded Tomcat task was retired alongside the `tomcat7-maven-plugin`.

### Linting

Spotless (with `google-java-format 1.7` AOSP style) lints every Java file
under `src/main/java` and `src/test/java`. Rules: 4‑space indent, sorted
imports, unused imports removed, trailing whitespace trimmed, files end
with a newline.

```bash
mise run lint     # mvn spotless:check — read-only, non-zero on violations
mise run format   # mvn spotless:apply — rewrites files in place
```

**Editor integration.** Most editors' built-in Java formatters do **not**
produce byte-identical output to `google-java-format 1.7` in AOSP style,
so letting them format-on-save will immediately fail `mvn spotless:check`.
The repo ships [`.zed/settings.json`](.zed/settings.json) that disables
format-on-save for Java in Zed specifically — always format Java via
`mise run format` (or `mvn spotless:apply`) instead. IntelliJ users can
install the [google-java-format plugin][gjf-idea] and enable AOSP mode to
match.

[gjf-idea]: https://github.com/google/google-java-format#intellij-jre-config

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
| `mise run radius:up`          | Start only FreeRADIUS + radclient (no app / DB)       |
| `mise run radius:down`        | Stop FreeRADIUS + radclient                           |
| `mise run radius:logs`        | Tail freeradius server debug output                   |
| `mise run radius:client-logs` | Tail radclient Accept/Reject responses                |

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

Brings up the app and a matching `mysql:5.7` in one go. The whole dev
stack — compose file, `.env`, a lab-only `config.properties` override,
and all FreeRADIUS/radclient fixtures — lives under [`lab/`](lab/) so
the repo root stays free of dev-only clutter. The app container reads
[`lab/config.properties`](lab/config.properties), which points `dbUrl`
at the compose `db` service (`jdbc:mysql://db:3306/...`); the packaged
[`src/main/resources/config.properties`](src/main/resources/config.properties)
keeps its `localhost` default for direct-host runs.

The `mise` tasks below `cd` into `lab/` for you. Invoking `docker
compose` directly also works from inside `lab/`:

```bash
mise run compose:up     # = (cd lab && docker compose --profile app up --build)
mise run compose:down   # = (cd lab && docker compose --profile app down)

# or, by hand:
cd lab
docker compose --profile app up --build
```

### Local FreeRADIUS for development

The `app` profile also includes a minimal FreeRADIUS 3.2 server plus a
`radclient` loop that generates steady auth traffic. Auth-detail log files
written by the server are shared with the GUI so the **Logs** page renders
real entries without needing a host-side FreeRADIUS install.

- `freeradius` — custom image built from
  [`lab/freeradius/Dockerfile.freeradius`](lab/freeradius/Dockerfile.freeradius),
  which layers two small overrides on top of
  `freeradius/freeradius-server:latest-3.2`:
  - `mods-enabled/detail.log` — `reply_log` writes flat
    `auth-detail-%Y%m%d` files (no per-client-IP subdir), matching
    `LogFileService#getFileName()`.
  - `sites-enabled/default` — `reply_log` enabled in both the happy-path
    `post-auth` section and the `Post-Auth-Type REJECT` block, so Accepts
    **and** Rejects are logged.
  Runs in debug mode (`-X`); publishes UDP `1812` (auth) and `1813`
  (accounting) on the host.
- `radclient` — stock image, fires the packets in
  [`lab/freeradius/requests.txt`](lab/freeradius/requests.txt) at the server
  every `RADCLIENT_INTERVAL` seconds (default `5`). Request attributes
  (`User-Name`=MAC, `NAS-IP-Address`, `NAS-Port`, `Connect-Info`,
  `NAS-Identifier`) line up with the seeded switches + devices so the
  GUI's log parser resolves every row to real entities.
- Dev seed data — [`lab/dev-seed.sql`](lab/dev-seed.sql) is mounted into
  the `db` service's `/docker-entrypoint-initdb.d/` as
  `20-dev-seed.sql`, right after the schema script. Seeds two switches
  + four devices whose MACs / IPs / ports match `requests.txt`, so
  every radclient packet resolves to a real device + switch on the
  Logs page. Runs **only on an empty data volume** — `mise run db:reset`
  to re-seed. ⚠️ Because it ships with the `db` service, `mise run db:up`
  (used by the unit-test workflow) also applies it; the
  `DeviceDAOImplTest` suite expects empty `devices`/`switches` tables
  and will fail against a seeded volume. Run `mvn test` against a fresh
  (unseeded) MySQL if you need a clean run.
- `app` — the GUI, bind-mounts
  [`lab/freeradius/clients.conf`](lab/freeradius/clients.conf) and
  [`lab/freeradius/users`](lab/freeradius/users) at the `/etc/freeradius/`
  paths [`lab/config.properties`](lab/config.properties) points at, and
  shares `/var/log/freeradius/radacct` with the `freeradius` container
  via the `radius-logs` named volume.

The [`lab/freeradius/users`](lab/freeradius/users) file doubles as both the
GUI's legacy users file (parsed by `UsersFileService`) and FreeRADIUS's
`mods-config/files/authorize` input. `Auth-Type := Accept` / `:= Reject`
short-circuits password checking so the same file works for both
consumers without a plaintext password.

```bash
# All commands below run from the lab/ directory (or via the mise
# wrappers which `cd` there automatically — see `mise run radius:*`).
cd lab

# Bring up only the RADIUS bits (no app / DB)
docker compose --profile app up -d freeradius radclient

# Watch auth traffic (server-side debug output)
docker compose logs -f freeradius

# Watch Accept / Reject responses (client-side)
docker compose logs -f radclient

# Manual test from the host (needs `freeradius-utils` / `radtest`)
radtest testuser testpass localhost 0 testing123
```

MAC identities defined in [`lab/freeradius/users`](lab/freeradius/users) and
seeded into the DB by [`lab/dev-seed.sql`](lab/dev-seed.sql):

| User-Name (MAC) | Device name            | Switch          | Port | Outcome         |
|-----------------|------------------------|-----------------|------|-----------------|
| `001122334455`  | seed-printer-office    | dev-sw-office   | 7    | `Access-Accept` |
| `aabbccddeeff`  | seed-workstation-lab   | dev-sw-lab      | 14   | `Access-Accept` |
| `ccddeeff0011`  | seed-laptop-dev        | dev-sw-office   | 23   | `Access-Accept` |
| `deadbeef1234`  | seed-rogue-device      | dev-sw-lab      | 4    | `Access-Reject` |
| `feedface9999`  | *(no DB row)*          | dev-sw-office   | 48   | `Access-Reject` (falls through to `DEFAULT`) |

Shared secret for all clients on the compose network (`172.16/12`) is
`testing123`. Override ports / interval via `.env`:

```bash
RADIUS_AUTH_PORT=1812
RADIUS_ACCT_PORT=1813
RADCLIENT_INTERVAL=5
```

Log in at `http://localhost:8080/` as `admin` / `123456` and open **Logs**
— you'll see rows filling in as `radclient` fires requests, with device
names, switch names, ports and speeds resolved against the seeded DB rows.
