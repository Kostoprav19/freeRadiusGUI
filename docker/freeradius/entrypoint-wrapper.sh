#!/bin/sh
# Wrapper around the upstream freeradius image entrypoint.
#
#   1. Refuses to start unless clients.conf and users have been supplied in
#      /data/radius-config (bind-mounted from the host). The image ships no
#      placeholder seeds: prod copies real files from the RADIUS server, lab
#      copies lab/radius-config.example/*. Booting with an implicit/empty
#      config would silently reject every device, so fail loudly instead.
#   2. Fixes ownership of the radacct log dir so the freerad user the daemon
#      drops to can write auth-detail-YYYYMMDD files (compose named volumes
#      are root-owned on first creation, otherwise reply_log fails with EACCES).
#
# Finally execs the upstream entrypoint so the daemon becomes PID 1 and
# receives signals directly.
set -e

CONFIG_DIR=/data/radius-config

missing=
for f in clients.conf users; do
    [ -s "$CONFIG_DIR/$f" ] || missing="$missing $f"
done
if [ -n "$missing" ]; then
    echo "FATAL: missing required RADIUS config in $CONFIG_DIR:$missing" >&2
    echo "Supply clients.conf and users via a host bind mount at $CONFIG_DIR:" >&2
    echo "  prod: cp <server>/{clients.conf,users} deploy/radius-config/" >&2
    echo "  lab:  cp lab/radius-config.example/{clients.conf,users} lab/radius-config/" >&2
    exit 1
fi

mkdir -p /var/log/freeradius/radacct
chown -R freerad:freerad /var/log/freeradius
chmod 0750 /var/log/freeradius/radacct

exec /docker-entrypoint.sh "$@"
