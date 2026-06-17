# Container image: production operations

The runtime image is **Tomcat 10.1 + the exploded WAR** only. It does **not**
pre-create RADIUS or host log paths. Operators must provide real files and
directories (bind mount or network storage).

## What to mount (default paths)

With the stock `config.properties` keys, the app expects:

| Path | Role |
|------|------|
| `WEB-INF/classes/config.properties` in the app | JDBC, mail, `clientsfilepath`, `usersfilepath`, `logfilesdirpath` (usually mounted over `…/webapps/ROOT/WEB-INF/classes/config.properties` in the container) |
| Path of `clientsfilepath` (default `/etc/freeradius/clients.conf`) | RADIUS clients; mount your real `clients.conf` read-only if possible. |
| Path of `usersfilepath` (default `/etc/freeradius/users`) | RADIUS users; mount your real `users` file. |
| Path of `logfilesdirpath` (default `/var/log/freeradius/radacct`) | RADIUS `auth-detail` logs for the UI log viewer. |
| Logback `LOGBACK_LOG_PATH` (default `/var/log/freeradiusgui`) | App log files. Set `JAVA_OPTS` to include `-DLOGBACK_LOG_PATH=...` to match the mount, or set the same path as environment `LOGBACK_LOG_PATH` and reference it in `-D`. |

If you change paths in your properties file, mount accordingly or set
`JAVA_OPTS` / `-D` to match (see *Strict startup check* below for env overrides
used by the entrypoint.

## Inspecting the image

OCI labels (including a short `README` URL to this file) are set on the image;
they show up in registry UIs and in:

```bash
docker inspect -f '{{json .Config.Labels}}' freeradiusgui:latest
```

`lv.freeradiusgui.recommended.volumes` lists the same paths in one line for a
quick checklist.

**Do not** rely on anonymous `VOLUME` declarations to “invent” empty
directories for config or RADIUS data; use explicit `-v` / `volumes` in
compose or Kubernetes with real sources.

## Startup mount check (always on)

The entrypoint **always** verifies the required paths before starting Tomcat and
exits if any is missing/unreadable. There is no toggle - the paths are not
assumed, so they must be set **explicitly** in the environment (the compose
files do this) and stay aligned with `config.properties` and Logback:

- `FREERADIUSGUI_CLIENTS_FILE`
- `FREERADIUSGUI_USERS_FILE`
- `FREERADIUSGUI_RADACCT_DIR`
- `FREERADIUSGUI_LOGBACK_DIR` (same path as `-DLOGBACK_LOG_PATH=...`)

Example `docker run` (minimal illustration only):

```bash
docker run \
  -e FREERADIUSGUI_CLIENTS_FILE=/etc/freeradius/clients.conf \
  -e FREERADIUSGUI_USERS_FILE=/etc/freeradius/users \
  -e FREERADIUSGUI_RADACCT_DIR=/var/log/freeradius/radacct \
  -e FREERADIUSGUI_LOGBACK_DIR=/data/app-logs \
  -e JAVA_OPTS="-DLOGBACK_LOG_PATH=/data/app-logs" \
  -v /srv/fr/clients.conf:/etc/freeradius/clients.conf:ro \
  -v /srv/fr/users:/etc/freeradius/users:ro \
  -v /srv/fr/radacct:/var/log/freeradius/radacct \
  -v /srv/app-logs:/data/app-logs \
  -v /srv/lab/config.properties:…/config.properties:ro \
  -p 8080:8080 \
  freeradiusgui:latest
```

The `lab/compose.yaml` profile `app` is a **working reference** of mounts and
env for a full local stack; copy patterns, not file contents, for production.

## Lab vs production

- **Unit tests** (`mvn test`): Surefire sets `LOGBACK_LOG_PATH` to
  `target/junit-logs` only for the test JVM. No change needed on your part.
- **lab + prod compose**: both set the `FREERADIUSGUI_*` paths explicitly and
  run the same always-on startup check.

See also `AGENTS.md` (Containerization and Configuration sections).
