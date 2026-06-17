# FreeRADIUS image for the freeRadiusGui stack (lab + prod).
#
# Layers on top of upstream 3.2:
#   1. detail.log override so reply_log/auth_log write a flat
#      /var/log/freeradius/radacct/auth-detail-%Y%m%d file, which is exactly
#      what lv.freeradiusgui.services.filesServices.LogFileService reads.
#   2. A patch to sites-enabled/default that un-comments the call to
#      reply_log in post-auth (Accept + Reject), so every reply is logged.
#   3. Symlinks that point the daemon's clients.conf and users (authorize)
#      files at /data/radius-config, the directory shared with the app
#      container via the radius-config volume. The app reads/writes the same
#      files (mounted at /etc/freeradius), so UI edits and the daemon stay in
#      sync without rebuilds.
#
# The entrypoint seeds /data/radius-config with defaults on first boot and
# fixes radacct ownership so the freerad user can write auth-detail files.

FROM freeradius/freeradius-server:latest-3.2

COPY detail.log /etc/raddb/mods-enabled/detail.log

# Use [[:space:]] instead of \t because /bin/sh single-quoted strings don't
# expand backslash escapes.
# 1) Un-comment reply_log in the happy-path post-auth section.
# 2) Also call reply_log inside Post-Auth-Type REJECT, inserted right before
#    the existing attr_filter.access_reject line (unique sentinel there).
# The grep afterwards is a build-time assertion that both edits took effect:
# if upstream reformats those lines the build fails loudly instead of
# silently producing a broken image.
RUN sed -i 's/^#[[:space:]]*reply_log$/\treply_log/' /etc/raddb/sites-enabled/default \
 && sed -i '/^[[:space:]]*attr_filter\.access_reject$/i\\treply_log' /etc/raddb/sites-enabled/default \
 && grep -E '^[[:space:]]+reply_log$' /etc/raddb/sites-enabled/default | wc -l | grep -q '^2$'

# Point the editable config files at the shared data dir. The targets live in
# the radius-config volume (prod) or a bind mount (lab); they don't need to
# exist at build time for the symlinks to be created.
RUN mkdir -p /data/radius-config \
 && rm -f /etc/raddb/clients.conf /etc/raddb/mods-config/files/authorize \
 && ln -s /data/radius-config/clients.conf /etc/raddb/clients.conf \
 && ln -s /data/radius-config/users /etc/raddb/mods-config/files/authorize

COPY seed/ /usr/local/share/freeradius-seed/

COPY entrypoint-wrapper.sh /usr/local/bin/entrypoint-wrapper.sh
RUN chmod 0755 /usr/local/bin/entrypoint-wrapper.sh

ENTRYPOINT ["/usr/local/bin/entrypoint-wrapper.sh"]
