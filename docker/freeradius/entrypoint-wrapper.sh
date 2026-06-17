#!/bin/sh
# Wrapper around the upstream freeradius image entrypoint.
#
#   1. Seeds /data/radius-config with default clients.conf and users on first
#      boot (when the shared volume is empty). Existing files are left alone,
#      so UI edits and lab bind-mounts are never clobbered.
#   2. Fixes ownership of the radacct log dir so the freerad user the daemon
#      drops to can write auth-detail-YYYYMMDD files (compose named volumes
#      are root-owned on first creation, otherwise reply_log fails with EACCES).
#
# Finally execs the upstream entrypoint so the daemon becomes PID 1 and
# receives signals directly.
set -e

mkdir -p /data/radius-config

if [ ! -f /data/radius-config/clients.conf ]; then
    cp /usr/local/share/freeradius-seed/clients.conf /data/radius-config/clients.conf
    chmod 0644 /data/radius-config/clients.conf
fi

if [ ! -f /data/radius-config/users ]; then
    cp /usr/local/share/freeradius-seed/users /data/radius-config/users
    chmod 0644 /data/radius-config/users
fi

mkdir -p /var/log/freeradius/radacct
chown -R freerad:freerad /var/log/freeradius
chmod 0750 /var/log/freeradius/radacct

exec /docker-entrypoint.sh "$@"
