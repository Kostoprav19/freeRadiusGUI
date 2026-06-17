# ---------- Build stage ------------------------------------------------------
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /src

COPY pom.xml ./
COPY src ./src
RUN mvn -B -ntp -DskipTests clean package \
 && ls -la target/freeradiusgui.war

RUN mkdir -p /build/ROOT \
 && (cd /build/ROOT && jar -xf /src/target/freeradiusgui.war)


# ---------- Runtime stage ----------------------------------------------------
FROM tomcat:10.1-jre25-temurin

# Tools the app shells out to: pgrep (procps) for local status checks, and
# curl to drive the freeradius sibling container via the Docker API.
RUN apt-get update \
 && apt-get install -y --no-install-recommends procps curl \
 && rm -rf /var/lib/apt/lists/*

# Drop Tomcat's sample webapps and the default ROOT, then install ours as ROOT
RUN rm -rf "$CATALINA_HOME/webapps/"* "$CATALINA_HOME/webapps.dist"

COPY --from=build /build/ROOT/ "$CATALINA_HOME/webapps/ROOT/"
COPY docker/app/entrypoint-tomcat.sh /usr/local/bin/entrypoint-tomcat.sh
RUN chmod 755 /usr/local/bin/entrypoint-tomcat.sh

# OCI labels: "docker inspect …Labels" and registry UIs.
LABEL org.opencontainers.image.title="freeRadiusGui" \
      org.opencontainers.image.description="FreeRADIUS admin UI; requires external mounts for RADIUS data and log dirs. See docker/README.md in the source tree." \
      lv.freeradiusgui.recommended.volumes="/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/config.properties, /etc/freeradius/clients.conf, /etc/freeradius/users, /var/log/freeradius/radacct, /var/log/freeradiusgui" \
      lv.freeradiusgui.strict_mounted_startup="always on; set FREERADIUSGUI_CLIENTS_FILE/USERS_FILE/RADACCT_DIR/LOGBACK_DIR; see entrypoint and docker/README.md"

ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=UTC"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:8080/ >/dev/null || exit 1

ENTRYPOINT ["/usr/local/bin/entrypoint-tomcat.sh"]
CMD ["catalina.sh", "run"]
