FROM freeradius/freeradius-server:latest-3.2

COPY detail.log /etc/raddb/mods-enabled/detail.log

RUN sed -i 's/^#[[:space:]]*reply_log$/\treply_log/' /etc/raddb/sites-enabled/default \
 && sed -i '/^[[:space:]]*attr_filter\.access_reject$/i\\treply_log' /etc/raddb/sites-enabled/default \
 && grep -E '^[[:space:]]+reply_log$' /etc/raddb/sites-enabled/default | wc -l | grep -q '^2$'

RUN mkdir -p /data/radius-config \
 && rm -f /etc/raddb/clients.conf /etc/raddb/mods-config/files/authorize \
 && ln -s /data/radius-config/clients.conf /etc/raddb/clients.conf \
 && ln -s /data/radius-config/users /etc/raddb/mods-config/files/authorize

COPY entrypoint-wrapper.sh /usr/local/bin/entrypoint-wrapper.sh
RUN chmod 0755 /usr/local/bin/entrypoint-wrapper.sh

ENTRYPOINT ["/usr/local/bin/entrypoint-wrapper.sh"]
