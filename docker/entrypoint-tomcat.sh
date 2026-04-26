#!/bin/sh
# freeRadiusGui Tomcat entrypoint. Optional hardening: set
# FREERADIUSGUI_REQUIRE_MOUNTS=1 so the container exits before boot if
# required paths (defaults match stock config.properties and logback.xml) are
# missing. See docker/README.md and image labels.
set -e

if [ "$FREERADIUSGUI_REQUIRE_MOUNTS" = "1" ]; then
  clients_file="${FREERADIUSGUI_CLIENTS_FILE:-/etc/freeradius/clients.conf}"
  users_file="${FREERADIUSGUI_USERS_FILE:-/etc/freeradius/users}"
  radacct_dir="${FREERADIUSGUI_RADACCT_DIR:-/var/log/freeradius/radacct}"
  logback_dir="${FREERADIUSGUI_LOGBACK_DIR:-${LOGBACK_LOG_PATH:-/var/log/freeradiusgui}}"

  ok=0
  if [ ! -f "$clients_file" ] || [ ! -r "$clients_file" ]; then
    echo "error: RADIUS clients file not available: $clients_file" >&2
    ok=1
  fi
  if [ ! -f "$users_file" ] || [ ! -r "$users_file" ]; then
    echo "error: RADIUS users file not available: $users_file" >&2
    ok=1
  fi
  if [ ! -d "$radacct_dir" ]; then
    echo "error: RADIUS accounting log directory missing: $radacct_dir" >&2
    ok=1
  fi
  if [ ! -d "$logback_dir" ]; then
    echo "error: application log directory (Logback) missing: $logback_dir" >&2
    ok=1
  elif [ ! -w "$logback_dir" ] 2>/dev/null; then
    echo "error: application log directory not writable: $logback_dir" >&2
    ok=1
  fi

  if [ "$ok" -ne 0 ]; then
    echo "" >&2
    echo "Mount host paths or named volumes at these locations, or set the" >&2
    echo "FREERADIUSGUI_* override variables to match your config.properties /" >&2
    echo "LOGBACK_LOG_PATH. See: docker inspect --format='{{.Config.Labels}}' on this" >&2
    echo "image, and read docker/README.md. To skip (not for production), unset" >&2
    echo "FREERADIUSGUI_REQUIRE_MOUNTS or set it to 0." >&2
    exit 1
  fi
fi

exec "$@"
