# freeRadiusGui — production deploy

Production deploy runs freeRadiusGui, FreeRADIUS, and MySQL all in containers. The app controls the FreeRADIUS daemon through the **Docker API** over a mounted `/var/run/docker.sock`: it reads the container's status and restarts it (applying `users` and `clients.conf` changes) without any shared PID namespace. Mounting the Docker socket grants root-on-host access — acceptable for this single-host deploy, but be aware of it.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│  Docker Compose (freeradiusgui-prod)                    │
│                                                         │
│  ┌──────────┐  ┌──────────────────┐  ┌──────────────┐  │
│  │    db    │  │    freeradius    │  │     app      │  │
│  │ MySQL 8  │  │ FreeRADIUS 3.2   │  │  Tomcat 10   │  │
│  │  :3306   │  │ :1812/udp :1813  │  │    :8080     │  │
│  └──────────┘  └──────────────────┘  └──────┬───────┘  │
│                       ▲ Docker API (socket)  │          │
│                       └──────────────────────┘          │
│                                                         │
│  Volumes: db-data, radius-logs, app-logs                │
│  Bind mount: deploy/radius-config (clients.conf, users) │
└─────────────────────────────────────────────────────────┘
```

## Prerequisites

- Docker Engine + Docker Compose plugin

## Steps

1. Copy and edit env vars:
   - `cp deploy/.env.example deploy/.env`
   - Set strong DB passwords and image tag values.
2. Copy and edit app config:
   - `cp deploy/config.properties.example deploy/config.properties`
   - Ensure DB credentials match `.env`.
3. Supply the RADIUS config files:
   - Place your real `clients.conf` and `users` into `deploy/radius-config/`
     (git-ignored; these hold real shared secrets). For example, straight from
     the RADIUS server:
     - `cp /path/to/configs_from_server/clients.conf deploy/radius-config/clients.conf`
     - `cp /path/to/configs_from_server/users deploy/radius-config/users`
   - No files to start from? Copy the templates and edit them:
     - `cp deploy/radius-config.example/clients.conf deploy/radius-config/clients.conf`
     - `cp deploy/radius-config.example/users deploy/radius-config/users`
   - The `freeradius` container refuses to start until both files exist.
4. Build and start the stack:
   - `docker compose --env-file deploy/.env -f deploy/compose.yaml up -d --build`
   - For logs: `docker compose --env-file deploy/.env -f deploy/compose.yaml logs -f`
5. First login:
   - Open `http://<host>:8080/`
   - Sign in with `admin` / `123456`
   - Change the admin password immediately.

## RADIUS config (supplied, not seeded)

The image ships no default `clients.conf`/`users`. You supply them in
`deploy/radius-config/` (step 3), which is bind-mounted into both the
`freeradius` and `app` containers. The `freeradius` container refuses to start
if either file is missing, so the app never runs against an implicit/empty
config. After start, edits made in the web UI ("Apply changes") are written
back to these same files and applied on a daemon restart.

## Verifying

- Check containers: `docker compose --env-file deploy/.env -f deploy/compose.yaml ps`
- Test RADIUS auth (use a MAC from your `users` file and the `localhost` secret from your `clients.conf`):
  - `docker compose --env-file deploy/.env -f deploy/compose.yaml exec freeradius sh -c 'echo "User-Name=001122334455" | radclient -x 127.0.0.1:1812 auth <localhost-secret>'`
  - Expect an `Access-Accept` reply.
- Confirm app can see FreeRADIUS process from the UI Server page.

## Volumes & mounts

| Name | Type | Purpose |
|---|---|---|
| `db-data` | named volume | MySQL data directory |
| `deploy/radius-config/` | host bind mount | FreeRADIUS `clients.conf` and `users` (shared with app; operator-supplied) |
| `radius-logs` | named volume | FreeRADIUS radacct auth-detail logs (shared with app) |
| `app-logs` | named volume | Application Logback logs |

## Files In This Directory

- `compose.yaml` — production compose stack (`app`, `freeradius`, `db`)
- `.env.example` — env template used with `--env-file`
- `config.properties.example` — app config template mounted into Tomcat
- `config.properties` — local deploy config (create from example)
- `.env` — local deploy env file (create from example)
- `radius-config.example/` — template `clients.conf` + `users` (tracked)
- `radius-config/` — operator-supplied `clients.conf` + `users` (git-ignored; bind-mounted into the stack)
