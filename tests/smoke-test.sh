#!/usr/bin/env bash
# Full-lifecycle smoke test:
#   db:reset -> compose:up -> wait for health -> login + page walk
#   -> short PASS/FAIL report -> compose:down.
#
# Exits non-zero on the first failed assertion so it's CI-friendly.
# Stack is torn down by the EXIT trap regardless of outcome (override
# with SMOKE_KEEP_STACK=1 for debugging).
#
# Wired into mise as `mise run smoke`.

set -u -o pipefail

REPO_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
LAB_DIR="${REPO_ROOT}/lab"
BASE_URL="${SMOKE_BASE_URL:-http://localhost:8080}"
COOKIEJAR="$(mktemp -t freeradiusgui-smoke-cookies.XXXXXX)"
PAGE_DUMP="$(mktemp -t freeradiusgui-smoke-page.XXXXXX)"
APP_TIMEOUT="${SMOKE_APP_TIMEOUT:-180}" # seconds to wait for app health

# ---------- pretty output ---------------------------------------------------

if [[ -t 1 ]]; then
    C_RESET='\033[0m'; C_BOLD='\033[1m'
    C_GREEN='\033[32m'; C_RED='\033[31m'; C_YELLOW='\033[33m'; C_BLUE='\033[34m'
else
    C_RESET=''; C_BOLD=''; C_GREEN=''; C_RED=''; C_YELLOW=''; C_BLUE=''
fi

# ---------- report state ---------------------------------------------------

declare -a REPORT_ROWS=()
TOTAL=0
PASSED=0
FAILED=0
START_EPOCH=$(date +%s)

record() {
    # record <status> <name> <detail>
    local status="$1" name="$2" detail="${3:-}"
    TOTAL=$((TOTAL + 1))
    REPORT_ROWS+=("${status}|${name}|${detail}")
    if [[ "$status" == PASS ]]; then
        PASSED=$((PASSED + 1))
        printf "  ${C_GREEN}\xe2\x9c\x93${C_RESET} %s  %s\n" "$name" "$detail"
    else
        FAILED=$((FAILED + 1))
        printf "  ${C_RED}\xe2\x9c\x97${C_RESET} %s  %s\n" "$name" "$detail"
    fi
}

section() { printf "\n${C_BOLD}${C_BLUE}== %s ==${C_RESET}\n" "$1"; }
info()    { printf "  ${C_YELLOW}\xe2\x9a\xa0${C_RESET} %s\n" "$1"; }

die() {
    printf "\n${C_RED}${C_BOLD}FATAL:${C_RESET} %s\n" "$1" >&2
    emit_report
    exit 1
}

# ---------- teardown -------------------------------------------------------

cleanup() {
    local rc=$?
    rm -f "$COOKIEJAR" "$PAGE_DUMP" 2>/dev/null || true
    if [[ "${SMOKE_KEEP_STACK:-0}" == 1 ]]; then
        info "SMOKE_KEEP_STACK=1 — leaving stack running for inspection."
    else
        section "Teardown"
        (cd "$LAB_DIR" && docker compose --profile app down) >/dev/null 2>&1 \
            && record PASS "compose-down" "stack stopped" \
            || record FAIL "compose-down" "docker compose down failed"
    fi
    emit_report
    exit "$rc"
}
trap cleanup EXIT

# ---------- report printer --------------------------------------------------

emit_report() {
    local elapsed=$(( $(date +%s) - START_EPOCH ))
    printf "\n${C_BOLD}=== Smoke-test report ===${C_RESET}\n"
    printf "  Duration: ${elapsed}s  |  Total: %d  |  ${C_GREEN}Pass: %d${C_RESET}  |  ${C_RED}Fail: %d${C_RESET}\n\n" \
        "$TOTAL" "$PASSED" "$FAILED"
    if (( FAILED > 0 )); then
        printf "  ${C_RED}${C_BOLD}Failures:${C_RESET}\n"
        for row in "${REPORT_ROWS[@]}"; do
            IFS='|' read -r s n d <<<"$row"
            [[ "$s" == FAIL ]] && printf "    - %s  %s\n" "$n" "$d"
        done
        printf "\n"
    fi
    if (( FAILED == 0 )); then
        printf "  ${C_GREEN}${C_BOLD}ALL GREEN${C_RESET}\n\n"
    else
        printf "  ${C_RED}${C_BOLD}SMOKE TEST FAILED${C_RESET}\n\n"
    fi
}

# ---------- pre-flight ------------------------------------------------------

section "Pre-flight"
for bin in docker curl grep wc; do
    command -v "$bin" >/dev/null 2>&1 || die "missing required tool: $bin"
done
docker compose version >/dev/null 2>&1 || die "docker compose v2 required"
[[ -f "$LAB_DIR/compose.yaml" ]] || die "lab/compose.yaml not found (looked in $LAB_DIR)"
record PASS "pre-flight" "tools ok"

# Static template guard — catches regressions where a view still uses
# Thymeleaf-2 idioms that evaluate silently to null under 3.1 (no
# runtime exception, just quietly-missing UI — so HTTP-level probes
# below won't catch it). Cheaper and 100% accurate vs dynamic checks.
views_dir="${REPO_ROOT}/src/main/webapp/WEB-INF/views"
banned_pattern='([^a-zA-Z_])session\.|([^a-zA-Z_])param\.|#(request|session|response|servletContext)'
if bad_hits=$(grep -rEn "$banned_pattern" "$views_dir" 2>/dev/null); then
    record FAIL "template-hygiene" "Thymeleaf-2 idioms leaked into views (removed in 3.1):"
    printf '%s\n' "$bad_hits" | sed 's/^/      /'
    exit 1
fi
record PASS "template-hygiene" "no banned \$\{session.*\} / \$\{param.*\} / #request|#session tokens"

# ---------- lifecycle: reset + up ------------------------------------------

section "Stack lifecycle"

info "db:reset — wiping volumes"
# --profile app is required: without it `down` leaves the app/freeradius/
# radclient containers running, so their named db-data/logs volumes survive
# `-v`. A stale config/DB then gets re-imported on the next startup
# (StartupListener.reloadFromConfig), resurrecting prior-run devices/switches
# and breaking the add probes with duplicate-validation 200s.
(cd "$LAB_DIR" && docker compose --profile app down -v) >/dev/null 2>&1 \
    && record PASS "db-reset" "volumes wiped" \
    || { record FAIL "db-reset" "docker compose down -v failed"; exit 1; }

# radius-config is now a host bind mount (not a named volume), so `down -v`
# does not reset it and the app mutates it on "Apply changes". Re-seed a
# pristine copy from the tracked example so each run starts clean.
info "radius-config — reset bind mount from lab/radius-config.example"
if rm -rf "$LAB_DIR/radius-config" \
    && mkdir -p "$LAB_DIR/radius-config" \
    && cp "$LAB_DIR/radius-config.example/clients.conf" "$LAB_DIR/radius-config/clients.conf" \
    && cp "$LAB_DIR/radius-config.example/users" "$LAB_DIR/radius-config/users"; then
    record PASS "radius-config-reset" "seeded lab/radius-config from example"
else
    record FAIL "radius-config-reset" "could not seed lab/radius-config"; exit 1
fi

info "compose:up — starting db + app + freeradius + radclient (detached, with build)"
(cd "$LAB_DIR" && docker compose --profile app up -d --build) >/dev/null 2>&1 \
    && record PASS "compose-up" "stack started" \
    || { record FAIL "compose-up" "docker compose up failed"; exit 1; }

info "waiting for app to become healthy (timeout ${APP_TIMEOUT}s)"
app_ready=false
for ((i = 1; i <= APP_TIMEOUT; i++)); do
    hs=$(docker inspect --format='{{.State.Health.Status}}' app 2>/dev/null || echo missing)
    if [[ "$hs" == healthy ]]; then
        app_ready=true
        record PASS "app-health" "healthy after ${i}s"
        break
    fi
    if [[ "$hs" == unhealthy ]]; then
        record FAIL "app-health" "unhealthy after ${i}s"
        docker logs --tail 50 app 2>&1 | sed 's/^/    /'
        exit 1
    fi
    sleep 1
done
$app_ready || { record FAIL "app-health" "timed out after ${APP_TIMEOUT}s"; exit 1; }

# ---------- assertion helpers ----------------------------------------------

# curl_get <path> [extra-curl-args...] — authenticated GET with session cookies.
# Writes body to $PAGE_DUMP. Usage: curl_get "/device/list"
curl_get() {
    curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -L -o "$PAGE_DUMP" "${BASE_URL}$@"
}

# curl_get_code <path> — like curl_get but returns the HTTP status code.
curl_get_code() {
    curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -L -o "$PAGE_DUMP" \
         -w '%{http_code}' "${BASE_URL}$1"
}

# http_get <name> <path> <expected-status> [grep-pattern]
# Pattern (optional) must match at least once in the response body.
# Always asserts the page body contains no unresolved Thymeleaf expressions
# (e.g. the old ${session.*} / ${param.*} / ${#request ...} shapes that 3.1
# silently evaluates to null — catches template migration misses).
http_get() {
    local name="$1" path="$2" want="$3" pattern="${4:-}"
    local code
    code=$(curl_get_code "$path" || echo 000)
    if [[ "$code" != "$want" ]]; then
        record FAIL "$name" "GET $path → HTTP $code (wanted $want)"
        return 1
    fi
    if [[ -n "$pattern" ]]; then
        if ! grep -qE "$pattern" "$PAGE_DUMP"; then
            record FAIL "$name" "GET $path → HTTP $code but missing: $pattern"
            return 1
        fi
    fi
    # Unresolved Thymeleaf expressions would leak raw ${...} or Spring EL
    # tokens into the HTML; any such leak is a regression. Skip inline
    # JavaScript template literals by restricting to th: / ${...} leftovers.
    if grep -qE '\$\{[^}]*\}' "$PAGE_DUMP"; then
        local sample
        sample=$(grep -oE '\$\{[^}]*\}' "$PAGE_DUMP" | head -1)
        record FAIL "$name" "GET $path → unresolved Thymeleaf expression leaked: $sample"
        return 1
    fi
    local size
    size=$(wc -c <"$PAGE_DUMP")
    record PASS "$name" "GET $path → HTTP $code, ${size}B"
    return 0
}

# header_badges_rendered — asserts PAGE_DUMP contains at least one Bootstrap
# label-* span, which proves the SessionVariablesInterceptor model attrs
# reached the Thymeleaf 3.1 header fragment.
header_badges_rendered() {
    local name="$1"
    local n
    n=$(grep -cE 'label-(success|warning|info)' "$PAGE_DUMP" || true)
    if (( n > 0 )); then
        record PASS "$name" "$n header badge(s) rendered"
    else
        record FAIL "$name" "no header badges in response"
        return 1
    fi
}

# ---------- probes: anonymous -----------------------------------------------

section "Anonymous probes"
http_get "login-page"     "/login"          200 'Please Sign In'
http_get "login-error"    "/login?error=1"  200 'Sign in error'

# ---------- login ----------------------------------------------------------

section "Login"

# Fresh cookie jar for the authenticated session.
: >"$COOKIEJAR"

csrf=$(curl -sS -c "$COOKIEJAR" -b "$COOKIEJAR" "${BASE_URL}/login" \
       | grep -oE 'name="_csrf"[^>]*value="[^"]+"' | head -1 \
       | sed -E 's/.*value="([^"]+)".*/\1/')
if [[ -z "${csrf:-}" ]]; then
    record FAIL "csrf-token" "could not extract _csrf from /login"
    exit 1
fi
record PASS "csrf-token" "extracted (${csrf:0:8}...)"

login_code=$(curl -sS -c "$COOKIEJAR" -b "$COOKIEJAR" -o /dev/null \
             -w '%{http_code}' -X POST \
             -d "j_username=admin" -d "j_password=123456" -d "_csrf=${csrf}" \
             "${BASE_URL}/j_spring_security_check")
if [[ "$login_code" != 302 ]]; then
    record FAIL "login-submit" "POST /j_spring_security_check → HTTP $login_code (wanted 302)"
    exit 1
fi
record PASS "login-submit" "POST → HTTP 302"

# ---------- probes: authenticated ------------------------------------------

section "Authenticated page walk"

http_get "logs"           "/logs"           200 '<title>Radius logs'     && header_badges_rendered "logs-header"
http_get "device-list"    "/device/list"    200 '<title>Devices'         && header_badges_rendered "device-list-header"
http_get "switch-list"    "/switch/list"    200 '<title>Switches'        && header_badges_rendered "switch-list-header"
http_get "server"         "/server"         200 '<title>Server'          && header_badges_rendered "server-header"
http_get "account-view"   "/account/1"      200 '<title>Account: admin'  && header_badges_rendered "account-view-header"
http_get "account-add"    "/account/add"    200 '<title>New account'     && header_badges_rendered "account-add-header"
http_get "admin"          "/admin"          200 '<title>Administration'  && header_badges_rendered "admin-header"

# ---------- probe: radclient traffic reached the GUI -----------------------

section "Radclient traffic"

# /logs body was just captured above. Count table rows — radclient has
# been emitting packets since the stack came up, so we expect at least a
# handful.
rows=$(curl_get "/logs" && grep -cE '<tr[> ]' "$PAGE_DUMP" || echo 0)
if (( rows >= 5 )); then
    record PASS "radclient-traffic" "$rows log rows rendered"
else
    record FAIL "radclient-traffic" "only $rows log rows — radclient loop not producing, or LogFileService failed to parse"
fi

# ---------- lifecycle: add via UI -> apply -> verify ------------------------
#
# Drives the full write path purely through the web UI: add a device/switch,
# click "Apply changes" (which rewrites the shared radius-config volume and
# restarts freeradius over the Docker socket), then prove the change is live
# in the daemon. Runs last so the earlier radclient-traffic probe sees the
# pristine seeded config first.

RADIUS_CONTAINER="freeradius"
SEED_ACCEPT_MAC="001122334455" # seeded Accept entry, present before and after writes

# get_csrf <path> — pull a fresh CSRF token from an authenticated form page.
# The token rotates on login, so the one scraped from /login is already stale;
# the device/switch forms carry an auto-injected _csrf hidden field.
get_csrf() {
    curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" "${BASE_URL}$1" \
        | grep -oE 'name="_csrf"[^>]*value="[^"]+"' | head -1 \
        | sed -E 's/.*value="([^"]+)".*/\1/'
}

# get_delete_id <list-path> <entity> <name> — return the numeric id from the
# /<entity>/delete/<id> link in the table row whose text contains <name>. Rows
# span multiple lines, so newlines are collapsed and the page is split on
# </tr> before matching, to avoid grabbing another row's delete link.
get_delete_id() {
    curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" "${BASE_URL}$1" \
        | tr '\n' ' ' \
        | awk -v RS='</tr>' -v name="$3" -v ent="$2" \
            '$0 ~ name {
                 if (match($0, "/" ent "/delete/[0-9]+")) {
                     s = substr($0, RSTART, RLENGTH); gsub(/[^0-9]/, "", s);
                     print s; exit
                 }
             }'
}

# radclient_accepts <mac> — true if freeradius returns Access-Accept for the
# MAC over localhost (the always-present localhost client, secret testing123).
# Retries to absorb the brief window while freeradius reloads after a restart.
radclient_accepts() {
    local mac="$1" i out
    for ((i = 1; i <= 15; i++)); do
        out=$(docker exec "$RADIUS_CONTAINER" sh -c \
              "echo 'User-Name = \"${mac}\"' | radclient -c 1 -r 1 -t 2 127.0.0.1:1812 auth testing123" \
              2>/dev/null || true)
        grep -q 'Access-Accept' <<<"$out" && return 0
        sleep 1
    done
    return 1
}

section "Lifecycle: add user via UI -> restart -> verify"

NEW_DEVICE_MAC="0123456789ab"
NEW_DEVICE_NAME="smoke-new-device"

csrf=$(get_csrf "/device/add") # also seeds the session-scoped form object
if [[ -z "${csrf:-}" ]]; then
    record FAIL "device-add-csrf" "no _csrf on /device/add"
else
    record PASS "device-add-csrf" "token extracted"

    code=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -o /dev/null -w '%{http_code}' -X POST \
           -d "id=" -d "mac=${NEW_DEVICE_MAC}" -d "name=${NEW_DEVICE_NAME}" \
           -d "description=added by smoke test" -d "access=1" -d "type=Computer" \
           -d "_csrf=${csrf}" "${BASE_URL}/device/submit")
    if [[ "$code" == 302 ]]; then
        record PASS "device-add" "POST /device/submit -> HTTP 302"
    else
        record FAIL "device-add" "POST /device/submit -> HTTP $code (wanted 302)"
    fi

    # Prove the device was stored: the delete-id helper must find it on the list page.
    dev_id=$(get_delete_id "/device/list" "device" "$NEW_DEVICE_NAME")
    if [[ -n "${dev_id:-}" ]]; then
        record PASS "device-in-db" "$NEW_DEVICE_NAME stored as id $dev_id"
    else
        record FAIL "device-in-db" "$NEW_DEVICE_NAME missing from /device/list"
    fi

    # Apply: writes the users file from the DB and restarts freeradius.
    # The hard gate is that the file write itself succeeded ("Error writing"
    # would mean the read-only-mount regression is back). Whether the app
    # could *confirm* the restart in time is timing-sensitive, so the actual
    # restart outcome is gated by device-verify (radclient) below.
    curl_get "/admin/writeUsers"
    if grep -q 'Error writing' "$PAGE_DUMP"; then
        record FAIL "device-apply" "writeUsers could not write the users file"
    elif grep -q 'Successfully applied' "$PAGE_DUMP"; then
        record PASS "device-apply" "users file written + restart confirmed"
    else
        record PASS "device-apply" "users file written (restart confirmation pending; see device-verify)"
    fi

    if radclient_accepts "$NEW_DEVICE_MAC"; then
        record PASS "device-verify" "Access-Accept for $NEW_DEVICE_MAC from live daemon"
    else
        record FAIL "device-verify" "no Access-Accept for $NEW_DEVICE_MAC after restart"
    fi
fi

section "Lifecycle: add client via UI -> restart -> verify"

NEW_SWITCH_IP="10.0.0.99"
NEW_SWITCH_SECRET="smokesecret123"
NEW_SWITCH_NAME="smoke-new-switch"

csrf=$(get_csrf "/switch/add")
if [[ -z "${csrf:-}" ]]; then
    record FAIL "switch-add-csrf" "no _csrf on /switch/add"
else
    record PASS "switch-add-csrf" "token extracted"

    code=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -o /dev/null -w '%{http_code}' -X POST \
           -d "id=" -d "name=${NEW_SWITCH_NAME}" -d "ip=${NEW_SWITCH_IP}" -d "mac=" \
           -d "secret=${NEW_SWITCH_SECRET}" -d "description=added by smoke test" \
           -d "_csrf=${csrf}" "${BASE_URL}/switch/submit")
    if [[ "$code" == 302 ]]; then
        record PASS "switch-add" "POST /switch/submit -> HTTP 302"
    else
        record FAIL "switch-add" "POST /switch/submit -> HTTP $code (wanted 302)"
    fi

    # Prove the switch was stored: the delete-id helper must find it on the list page.
    sw_id=$(get_delete_id "/switch/list" "switch" "$NEW_SWITCH_NAME")
    if [[ -n "${sw_id:-}" ]]; then
        record PASS "switch-in-db" "$NEW_SWITCH_NAME stored as id $sw_id"
    else
        record FAIL "switch-in-db" "$NEW_SWITCH_NAME missing from /switch/list"
    fi

    # Apply: writes clients.conf from the DB and restarts freeradius. As with
    # the user flow, the hard gate is the file write; the restart outcome is
    # gated by switch-verify-config / switch-verify-daemon below.
    curl_get "/admin/writeClients"
    if grep -q 'Error writing' "$PAGE_DUMP"; then
        record FAIL "switch-apply" "writeClients could not write clients.conf"
    elif grep -q 'Successfully applied' "$PAGE_DUMP"; then
        record PASS "switch-apply" "clients.conf written + restart confirmed"
    else
        record PASS "switch-apply" "clients.conf written (restart confirmation pending; see switch-verify)"
    fi

    # The new client must be present in the clients.conf the daemon reloaded.
    live_clients=$(docker exec "$RADIUS_CONTAINER" cat /etc/raddb/clients.conf 2>/dev/null || true)
    if grep -q "client ${NEW_SWITCH_IP} " <<<"$live_clients" \
       && grep -q "secret = ${NEW_SWITCH_SECRET}" <<<"$live_clients"; then
        record PASS "switch-verify-config" "client ${NEW_SWITCH_IP} live in clients.conf"
    else
        record FAIL "switch-verify-config" "client ${NEW_SWITCH_IP} not in live clients.conf"
    fi

    # A malformed clients.conf would crash freeradius on restart; confirm the
    # daemon still answers auth requests afterwards.
    if radclient_accepts "$SEED_ACCEPT_MAC"; then
        record PASS "switch-verify-daemon" "freeradius answering after clients.conf restart"
    else
        record FAIL "switch-verify-daemon" "freeradius not answering after clients.conf restart"
    fi
fi

# ---------- lifecycle: delete via UI -> apply -> verify --------------------
#
# Deletes the device/switch added above purely through the web UI, then proves
# the deletion is durable: the "Apply changes" button must surface (delete has
# to set the db-changes flag, same as add — otherwise the removal can never be
# pushed to the config files), the apply must scrub the entry from the live
# config the daemon reads, and a subsequent "Reload from file" must NOT
# resurrect the record (the file is the source of truth after apply).

section "Lifecycle: delete user via UI -> apply -> verify"

dev_id=$(get_delete_id "/device/list" "device" "$NEW_DEVICE_NAME")
if [[ -z "${dev_id:-}" ]]; then
    record FAIL "device-delete-id" "could not resolve delete id for $NEW_DEVICE_NAME"
else
    record PASS "device-delete-id" "$NEW_DEVICE_NAME has id $dev_id"

    code=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -o /dev/null -w '%{http_code}' \
           "${BASE_URL}/device/delete/${dev_id}")
    if [[ "$code" == 302 ]]; then
        record PASS "device-delete" "GET /device/delete/${dev_id} -> HTTP 302"
    else
        record FAIL "device-delete" "GET /device/delete/${dev_id} -> HTTP $code (wanted 302)"
    fi

    # List should no longer show the device, and the "Apply changes" button
    # (links to /admin/writeUsers) must appear — proof that delete set the
    # db-changes flag, the regression this guards against.
    curl_get "/device/list"
    if grep '<tr[> ]' "$PAGE_DUMP" | grep -q "$NEW_DEVICE_NAME"; then
        record FAIL "device-delete-gone" "$NEW_DEVICE_NAME still on /device/list after delete"
    else
        record PASS "device-delete-gone" "$NEW_DEVICE_NAME removed from /device/list"
    fi
    if grep -q '/admin/writeUsers' "$PAGE_DUMP"; then
        record PASS "device-delete-flag" "Apply-changes button present (delete set dbChangesFlag)"
    else
        record FAIL "device-delete-flag" "no Apply-changes button — delete did not set dbChangesFlag"
    fi

    # Apply: rewrites the users file from the (now smaller) DB and restarts.
    curl_get "/admin/writeUsers"
    if grep -q 'Error writing' "$PAGE_DUMP"; then
        record FAIL "device-delete-apply" "writeUsers could not write the users file"
    else
        record PASS "device-delete-apply" "users file rewritten after delete"
    fi

    # The deleted MAC must be gone from the live users file the daemon reads.
    live_users=$(docker exec "$RADIUS_CONTAINER" cat /data/radius-config/users 2>/dev/null || true)
    if grep -q "$NEW_DEVICE_MAC" <<<"$live_users"; then
        record FAIL "device-delete-verify" "$NEW_DEVICE_MAC still in live users file after apply"
    else
        record PASS "device-delete-verify" "$NEW_DEVICE_MAC scrubbed from live users file"
    fi

    # Reload re-imports the file into the DB; it must not bring the device back.
    curl_get "/device/reload" >/dev/null
    curl_get "/device/list"
    if grep '<tr[> ]' "$PAGE_DUMP" | grep -q "$NEW_DEVICE_NAME"; then
        record FAIL "device-delete-reload" "$NEW_DEVICE_NAME reappeared after Reload"
    else
        record PASS "device-delete-reload" "$NEW_DEVICE_NAME stayed deleted after Reload"
    fi
fi

section "Lifecycle: delete client via UI -> apply -> verify"

sw_id=$(get_delete_id "/switch/list" "switch" "$NEW_SWITCH_NAME")
if [[ -z "${sw_id:-}" ]]; then
    record FAIL "switch-delete-id" "could not resolve delete id for $NEW_SWITCH_NAME"
else
    record PASS "switch-delete-id" "$NEW_SWITCH_NAME has id $sw_id"

    code=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -o /dev/null -w '%{http_code}' \
           "${BASE_URL}/switch/delete/${sw_id}")
    if [[ "$code" == 302 ]]; then
        record PASS "switch-delete" "GET /switch/delete/${sw_id} -> HTTP 302"
    else
        record FAIL "switch-delete" "GET /switch/delete/${sw_id} -> HTTP $code (wanted 302)"
    fi

    curl_get "/switch/list"
    if grep '<tr[> ]' "$PAGE_DUMP" | grep -q "$NEW_SWITCH_NAME"; then
        record FAIL "switch-delete-gone" "$NEW_SWITCH_NAME still on /switch/list after delete"
    else
        record PASS "switch-delete-gone" "$NEW_SWITCH_NAME removed from /switch/list"
    fi
    if grep -q '/admin/writeClients' "$PAGE_DUMP"; then
        record PASS "switch-delete-flag" "Apply-changes button present (delete set dbChangesFlag)"
    else
        record FAIL "switch-delete-flag" "no Apply-changes button — delete did not set dbChangesFlag"
    fi

    curl_get "/admin/writeClients"
    if grep -q 'Error writing' "$PAGE_DUMP"; then
        record FAIL "switch-delete-apply" "writeClients could not write clients.conf"
    else
        record PASS "switch-delete-apply" "clients.conf rewritten after delete"
    fi

    # The deleted client must be gone from the clients.conf the daemon reloaded.
    live_clients=$(docker exec "$RADIUS_CONTAINER" cat /etc/raddb/clients.conf 2>/dev/null || true)
    if grep -q "client ${NEW_SWITCH_IP} " <<<"$live_clients"; then
        record FAIL "switch-delete-verify" "client ${NEW_SWITCH_IP} still in live clients.conf after apply"
    else
        record PASS "switch-delete-verify" "client ${NEW_SWITCH_IP} scrubbed from live clients.conf"
    fi

    # The seeded client must survive (a botched rewrite would drop everything);
    # confirm the daemon still answers after the restart.
    if radclient_accepts "$SEED_ACCEPT_MAC"; then
        record PASS "switch-delete-daemon" "freeradius answering after clients.conf restart"
    else
        record FAIL "switch-delete-daemon" "freeradius not answering after clients.conf restart"
    fi

    curl_get "/switch/reload" >/dev/null
    curl_get "/switch/list"
    if grep '<tr[> ]' "$PAGE_DUMP" | grep -q "$NEW_SWITCH_NAME"; then
        record FAIL "switch-delete-reload" "$NEW_SWITCH_NAME reappeared after Reload"
    else
        record PASS "switch-delete-reload" "$NEW_SWITCH_NAME stayed deleted after Reload"
    fi
fi

# ---------- force a log reload --------------------------------------------
#
# The localhost radclient probes above (device-verify / switch-verify-daemon)
# authenticate over 127.0.0.1, which FreeRADIUS logs as "Switch IP: 127.0.0.1"
# — an address that is the localhost RADIUS *client* but not a Switch row in
# the DB. Force the app to re-ingest today's auth-detail file now, while those
# records exist, so any failure to persist them surfaces in the log scan below
# (rather than only when the 5-minute scheduled reload happens to run).

section "Force log reload"

refresh_code=$(curl_get_code "/logs/refresh/$(date +%d%m%Y)")
if [[ "$refresh_code" == 200 ]]; then
    record PASS "logs-refresh" "re-ingested today's auth-detail file (HTTP 200)"
else
    record FAIL "logs-refresh" "GET /logs/refresh -> HTTP $refresh_code (wanted 200)"
fi

# ---------- catalina / app error-log scan ----------------------------------
#
# Last gate: scan Tomcat's catalina logs and the container console (catalina
# `run` stdout, which also carries the app's logback STDOUT appender) for
# ERROR/SEVERE-level lines. Catches failures that never surface as a non-200
# response — scheduled/async DB writes, startup wiring, the freeradius-restart
# plumbing, uncaught handler exceptions, etc. Runs after every other probe so
# the whole lifecycle's output is in scope.
#
# Only level-marker header lines are matched (logback '%-5level' -> ' ERROR ',
# juli -> 'SEVERE:'); stack-trace continuation lines carry no level token, so
# each failure is counted once. No allowlist — every error must be a clean run
# or a tracked bug; the scan surfaces all of them.

section "Catalina / app error-log scan"

app_logs=$(
    {
        docker logs "${APP_CONTAINER:-app}" 2>&1
        docker exec "${APP_CONTAINER:-app}" \
            sh -c 'cat /usr/local/tomcat/logs/catalina*.log 2>/dev/null'
    } 2>/dev/null
)

err_hits=$(grep -aE '(\bERROR\b|\bSEVERE\b)' <<<"$app_logs" || true)

if [[ -z "$err_hits" ]]; then
    record PASS "catalina-errors" "no ERROR/SEVERE lines in catalina/app logs"
else
    n=$(printf '%s\n' "$err_hits" | grep -c .)
    record FAIL "catalina-errors" "$n error line(s) in catalina/app logs:"
    printf '%s\n' "$err_hits" | head -10 | sed 's/^/      /'
fi

# success — teardown happens in the EXIT trap
exit 0
