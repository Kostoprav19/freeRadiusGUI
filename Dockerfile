# ---------- Build stage ------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /src

# BuildKit cache mount for ~/.m2 keeps Maven artifacts across rebuilds
# without relying on `dependency:go-offline` (which chokes on Spring 4.2's
# optional IBM WebSphere UOW transitive dep, only hosted in an IBM repo).
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -B -ntp -DskipTests clean package \
 && ls -la target/freeradiusgui.war

RUN mkdir -p /build/ROOT \
 && (cd /build/ROOT && jar -xf /src/target/freeradiusgui.war)


# ---------- Runtime stage ----------------------------------------------------
FROM tomcat:10.1-jdk17-temurin

# Tools the app shells out to (pgrep -> procps, killall -> psmisc).
RUN apt-get update \
 && apt-get install -y --no-install-recommends procps psmisc \
 && rm -rf /var/lib/apt/lists/*

# Drop Tomcat's sample webapps and the default ROOT, then install ours as ROOT
RUN rm -rf "$CATALINA_HOME/webapps/"* "$CATALINA_HOME/webapps.dist"

COPY --from=build /build/ROOT/ "$CATALINA_HOME/webapps/ROOT/"
COPY docker/entrypoint-tomcat.sh /usr/local/bin/entrypoint-tomcat.sh
RUN chmod 755 /usr/local/bin/entrypoint-tomcat.sh

# OCI labels: "docker inspect …Labels" and registry UIs. Do not add empty VOLUME
# declarations here — that creates anonymous empty volumes and hides bad mounts.
LABEL org.opencontainers.image.title="freeRadiusGui" \
      org.opencontainers.image.description="FreeRADIUS admin UI; requires external mounts for RADIUS data and log dirs. See docker/README.md in the source tree." \
      lv.freeradiusgui.recommended.volumes="/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/config.properties, /etc/freeradius/clients.conf, /etc/freeradius/users, /var/log/freeradius/radacct, /var/log/freeradiusgui" \
      lv.freeradiusgui.strict_mounted_startup="set FREERADIUSGUI_REQUIRE_MOUNTS=1; see entrypoint and docker/README.md"

# --add-opens: Spring 6.1 reflection on JDK 17. Mirror in pom.xml surefire <argLine>.
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=UTC \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:8080/ >/dev/null || exit 1

ENTRYPOINT ["/usr/local/bin/entrypoint-tomcat.sh"]
CMD ["catalina.sh", "run"]
