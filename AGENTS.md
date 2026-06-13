# AGENTS.md

AI guidance for `freeRadiusGui`.

## Overview & Tech Stack
- Spring MVC (not Boot) UI for FreeRADIUS admin on Linux. Bootstraps via `AppInitializer.java`.
- **Tech:** Java 25, Spring 6.2.18, Security 6.4.13, Spring Data JDBC 3.4.13, MySQL 8.0, Thymeleaf 3.1.4, Tomcat 10.1, Maven 3.9. 
- Reads/writes `/etc/freeradius/{users,clients.conf}` and `/var/log/freeradius/radacct`. Requires shell execution permissions for `freeradius` / `killall freeradius`.

## Layout & Commands
- `src/main/java/` (config, controllers, services, repositories, domain)
- `src/main/resources/` (config.properties, logback, messages)
- `lab/` (docker-compose stack, DB seeds)
- Use [`mise`](https://mise.jdx.dev/) (`mise tasks`):
  - `mise run build` / `test` / `verify` / `lint` / `format`
  - `mise run compose:up` / `db:up` / `db:reset`

## Infrastructure
- **Container:** Multi-stage `docker/Dockerfile` (Maven 3.9/JDK 25 → Tomcat 10.1/JRE 25). No `VOLUME`s. Shell ops require `--pid=host`.
- **Compose (`lab/compose.yaml`):** `db` (mysql:8.0, seeds via `databaseCreationScript.sql` into empty dir), `app` (mounts local `config.properties`, radacct, logs).
- **Bootstrap DB:** admin/123456, user/123456.

## Configuration & Architecture
- Config at `src/main/resources/config.properties`. Committed as local defaults—do not add prod secrets. `mailEnabled` defaults false.
- **Layering:** Controller → Service → Repository (`CrudRepository`) → Domain. No direct Controller-to-Repo calls.
- **Entities:** Spring Data Relational. `AggregateReference` for many-to-one with `@Transient` populated in services. `AccountRoleRef` for many-to-many.
- **Views:** Use `lv.freeradiusgui.constants.Views`. No hard-coded view strings.
- **Validation:** Placed in `validators/`, via `@InitBinder`.
- **Shell:** Use `ShellExecutor` with `ShellCommands` (never hard-code or pass user input).
- **Flash Messages:** Use `RedirectAttributes` (`msg`, `msgType`).
- **Security:** Pass bare roles (`hasRole("ADMIN")`). Explicit `requestMatchers("/login").permitAll()`. BCrypt passwords.

## Gotchas & Tests
- **Tests:** Need live MySQL (`mise run db:up`). Repo tests use `@Transactional` + `@Rollback`. Use JUnit 5 + SpringExtension. `web/` dir is legacy, ignore.
- **JDBC:** Driver `com.mysql.cj.jdbc.Driver`. URL must have `useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`.
- **Logging:** Logback defaults to `/var/log/freeradiusgui`. Surefire uses `target/junit-logs`.
- **Pins:** `jakarta.annotation-api` is required. No `--add-opens` flags used.

## Coding Style
- **Spotless:** AOSP style, 4-space indent (`mise run format`). Keep existing style.
- No narrating comments or legacy IDE boilerplate (`/** Created by... */`). Use SLF4J. Keep controllers thin.

## Agent Workflow (3-Agent)
Use `architect` → `coder` → `reviewer`. Canonical prompts in `agents/`.
1. **`architect`:** Sole writer of `.cursor/plans/` (ROADMAP, plans, todos). Cannot edit app code/config.
2. **`coder`:** Implements one phase. Edits `src/`, `pom.xml`, `docker/Dockerfile`, `lab/`, etc. Cannot edit plans. Do not commit/push unless asked.
3. **`reviewer`:** Independent opinion. MUST be invoked after code changes by default.
- **Routing:** Main session MUST NOT apply app code edits directly; use `coder` (except for user-labeled docs-only one-offs).
- **"Review" request:** Always invoke `reviewer` subagent on the diff.

## What NOT to do
- NO Spring Boot conversions.
- NO committing real passwords.
- NO new deps/frameworks/DB changes without user approval.
- NO hard-coding FreeRADIUS file paths (use `config.properties`).
- NO `--trailer "Co-authored-by: Cursor <cursoragent@cursor.com>"` or any bot mentions in git commits.