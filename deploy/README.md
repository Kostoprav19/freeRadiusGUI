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
│  Volumes: db-data, radius-config, radius-logs, app-logs │
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
3. Build and start the stack:
   - `docker compose --env-file deploy/.env -f deploy/compose.yaml up -d --build`
   - For logs: `docker compose --env-file deploy/.env -f deploy/compose.yaml logs -f`
4. First login:
   - Open `http://<host>:8080/`
   - Sign in with `admin` / `123456`
   - Change the admin password immediately.

## First Boot

On first start, the FreeRADIUS container seeds the `radius-config` volume with default `clients.conf` and `users` files (a `localhost` client with the placeholder secret `changeme` and one sample MAC entry). Replace the placeholder secret and use the web UI to configure your actual devices and switches, which writes to the shared config volume. The seed defaults live in `docker/freeradius/seed/`.

## Verifying

- Check containers: `docker compose --env-file deploy/.env -f deploy/compose.yaml ps`
- Test RADIUS auth (uses the seeded sample MAC and the seeded `localhost` secret — adjust if you changed them):
  - `docker compose --env-file deploy/.env -f deploy/compose.yaml exec freeradius sh -c 'echo "User-Name=001122334455" | radclient -x 127.0.0.1:1812 auth changeme'`
  - Expect an `Access-Accept` reply.
- Confirm app can see FreeRADIUS process from the UI Server page.

## Volumes

| Volume | Purpose |
|---|---|
| `db-data` | MySQL data directory |
| `radius-config` | FreeRADIUS `clients.conf` and `users` files (shared with app) |
| `radius-logs` | FreeRADIUS radacct auth-detail logs (shared with app) |
| `app-logs` | Application Logback logs |

## Files In This Directory

- `compose.yaml` — production compose stack (`app`, `freeradius`, `db`)
- `.env.example` — env template used with `--env-file`
- `config.properties.example` — app config template mounted into Tomcat
- `config.properties` — local deploy config (create from example)
- `.env` — local deploy env file (create from example)
