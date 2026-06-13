# freeRadiusGui — production deploy

Production deploy runs freeRadiusGui + MySQL in containers, while FreeRADIUS stays host-managed.

## Prerequisites

- Docker Engine + Docker Compose plugin on the host
- Host FreeRADIUS installed and managed by systemd
- Existing `/etc/freeradius/clients.conf` and `/etc/freeradius/users`
- Access to host radacct logs at `/var/log/freeradius/radacct`

## Steps

1. Copy and edit env vars:
   - `cp deploy/.env.example deploy/.env`
   - Set strong DB passwords and image tag values.
2. Copy and edit app config:
   - `cp deploy/config.properties.example deploy/config.properties`
   - Ensure DB credentials match `.env` and FreeRADIUS paths are correct.
3. Start the stack:
   - `docker compose --env-file deploy/.env -f deploy/compose.yaml up -d`
   - For logs: `docker compose --env-file deploy/.env -f deploy/compose.yaml logs -f app db`
4. First login:
   - Open `http://<host>:8080/`
   - Sign in with `admin` / `123456`
   - Change the admin password immediately.

## Verifying

- Check containers: `docker compose --env-file deploy/.env -f deploy/compose.yaml ps`
- Validate FreeRADIUS systemd unit from host namespace:
  - `nsenter --target 1 --mount --uts --ipc --net --pid systemctl status freeradius`
- Confirm app shell checks can see host processes (`freeradius`, `mysqld`, `tomcat`) from the UI Server page.

## Files In This Directory

- `compose.yaml` — production compose stack (`app`, `db`)
- `.env.example` — env template used with `--env-file`
- `config.properties.example` — app config template mounted into Tomcat
- `config.properties` — local deploy config (create from example)
- `.env` — local deploy env file (create from example)
