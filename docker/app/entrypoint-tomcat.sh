#!/bin/sh
# freeRadiusGui Tomcat entrypoint. Always verifies that the required RADIUS and
# log paths exist (and are readable/writable) before starting Tomcat, then
# execs the server. The paths are supplied explicitly via the compose
# environment - this script assumes no defaults. See docker/README.md.
set -e

clients_file="$FREERADIUSGUI_CLIENTS_FILE"
users_file="$FREERADIUSGUI_USERS_FILE"
radacct_dir="$FREERADIUSGUI_RADACCT_DIR"
logback_dir="$FREERADIUSGUI_LOGBACK_DIR"

ok=0

# All paths must be provided explicitly by the environment (compose).
[ -n "$clients_file" ] || { echo "error: FREERADIUSGUI_CLIENTS_FILE is not set" >&2; ok=1; }
[ -n "$users_file" ]   || { echo "error: FREERADIUSGUI_USERS_FILE is not set" >&2; ok=1; }
[ -n "$radacct_dir" ]  || { echo "error: FREERADIUSGUI_RADACCT_DIR is not set" >&2; ok=1; }
[ -n "$logback_dir" ]  || { echo "error: FREERADIUSGUI_LOGBACK_DIR is not set" >&2; ok=1; }

if [ "$ok" -eq 0 ]; then
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
  elif [ ! -w "$logback_dir" ]; then
    echo "error: application log directory not writable: $logback_dir" >&2
    ok=1
  fi
fi

if [ "$ok" -ne 0 ]; then
  echo "" >&2
  echo "Mount host paths or named volumes at these locations and set the" >&2
  echo "FREERADIUSGUI_* variables in the compose environment to match your" >&2
  echo "config.properties / LOGBACK_LOG_PATH. See docker/README.md." >&2
  exit 1
fi

exec "$@"
