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

info "db:reset — wiping volume"
(cd "$LAB_DIR" && docker compose down -v) >/dev/null 2>&1 \
    && record PASS "db-reset" "volume wiped" \
    || { record FAIL "db-reset" "docker compose down -v failed"; exit 1; }

info "compose:up — starting db + app + freeradius + radclient (detached, with build)"
(cd "$LAB_DIR" && docker compose --profile app up -d --build) >/dev/null 2>&1 \
    && record PASS "compose-up" "stack started" \
    || { record FAIL "compose-up" "docker compose up failed"; exit 1; }

info "waiting for freeradiusgui-app to become healthy (timeout ${APP_TIMEOUT}s)"
app_ready=false
for ((i = 1; i <= APP_TIMEOUT; i++)); do
    hs=$(docker inspect --format='{{.State.Health.Status}}' freeradiusgui-app 2>/dev/null || echo missing)
    if [[ "$hs" == healthy ]]; then
        app_ready=true
        record PASS "app-health" "healthy after ${i}s"
        break
    fi
    if [[ "$hs" == unhealthy ]]; then
        record FAIL "app-health" "unhealthy after ${i}s"
        docker logs --tail 50 freeradiusgui-app 2>&1 | sed 's/^/    /'
        exit 1
    fi
    sleep 1
done
$app_ready || { record FAIL "app-health" "timed out after ${APP_TIMEOUT}s"; exit 1; }

# ---------- assertion helpers ----------------------------------------------

# http_get <name> <path> <expected-status> [grep-pattern]
# Pattern (optional) must match at least once in the response body.
# Always asserts the page body contains no unresolved Thymeleaf expressions
# (e.g. the old ${session.*} / ${param.*} / ${#request ...} shapes that 3.1
# silently evaluates to null — catches template migration misses).
http_get() {
    local name="$1" path="$2" want="$3" pattern="${4:-}"
    local code
    code=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -L -o "$PAGE_DUMP" \
           -w '%{http_code}' "${BASE_URL}${path}" || echo 000)
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
rows=$(curl -sS -b "$COOKIEJAR" -c "$COOKIEJAR" -L -o "$PAGE_DUMP" \
       -w '%{http_code}' "${BASE_URL}/logs" >/dev/null \
       && grep -cE '<tr[> ]' "$PAGE_DUMP" || echo 0)
if (( rows >= 5 )); then
    record PASS "radclient-traffic" "$rows log rows rendered"
else
    record FAIL "radclient-traffic" "only $rows log rows — radclient loop not producing, or LogFileService failed to parse"
fi

# success — teardown happens in the EXIT trap
exit 0
