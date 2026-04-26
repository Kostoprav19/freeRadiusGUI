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

## Strict startup check (recommended for production)

Set on the container:

```text
FREERADIUSGUI_REQUIRE_MOUNTS=1
```

The entrypoint exits before Tomcat if defaults are missing, unless you point it
at your own paths (must stay aligned with `config.properties` and Logback):

- `FREERADIUSGUI_CLIENTS_FILE`
- `FREERADIUSGUI_USERS_FILE`
- `FREERADIUSGUI_RADACCT_DIR`
- `FREERADIUSGUI_LOGBACK_DIR` (or `LOGBACK_LOG_PATH` as a plain environment
  variable, same value as in `-DLOGBACK_LOG_PATH=...` if you use a property)

Example `docker run` (minimal illustration only):

```bash
docker run -e FREERADIUSGUI_REQUIRE_MOUNTS=1 -e LOGBACK_LOG_PATH=/data/app-logs \
  -e JAVA_OPTS="-DLOGBACK_LOG_PATH=/data/app-logs <opens flags…>" \
  -v /srv/fr/clients.conf:/etc/freeradius/clients.conf:ro \
  -v /srv/fr/users:/etc/freeradius/users:ro \
  -v /srv/fr/radacct:/var/log/freeradius/radacct \
  -v /srv/app-logs:/data/app-logs \
  -v /srv/lab/config.properties:…/config.properties:ro \
  -p 8080:8080 \
  freeradiusgui:latest
```

The `lab/compose.yaml` profile `app` is a **working reference** of mounts and
`JAVA_OPTS` for a full local stack; copy patterns, not file contents, for
production.

## Lab vs production

- **Unit tests** (`mvn test`): Surefire sets `LOGBACK_LOG_PATH` to
  `target/junit-logs` only for the test JVM. No change needed on your part.
- **lab compose**: `FREERADIUSGUI_REQUIRE_MOUNTS` is **not** set, so the stack
  is tolerant of iteration; turn strict mode on in prod-like environments.

See also `AGENTS.md` (Containerization and Configuration sections).
