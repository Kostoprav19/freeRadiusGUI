#!/bin/sh
# Wrapper around the upstream freeradius image entrypoint. Fixes ownership
# of the radacct log directory so that the `freerad` user the daemon drops
# to can write auth-detail-YYYYMMDD files there. Needed because compose
# named volumes are root-owned on first creation.
set -e
chown -R freerad:freerad /var/log/freeradius
chmod 0750 /var/log/freeradius/radacct
exec /docker-entrypoint.sh "$@"
