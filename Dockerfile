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

# RADIUS config, accounting, and app log paths are not created here. Supply bind
# mounts or named volumes in compose/ops. Tests use Surefire
# (LOGBACK_LOG_PATH=target/junit-logs). Lab: see lab/compose.yaml.

# --add-opens: Spring 6.1 reflection on JDK 17. Mirror in pom.xml surefire <argLine>.
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=UTC \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -fsS http://localhost:8080/ >/dev/null || exit 1

CMD ["catalina.sh", "run"]
