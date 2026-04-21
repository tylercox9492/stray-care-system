#!/bin/sh
set -eu

cat > /usr/share/nginx/html/config.js <<EOF
window.APP_CONFIG = {
    GOOGLE_MAPS_API_KEY: "${GOOGLE_MAPS_API_KEY:-}",
    API_BASE_URL: "${API_BASE_URL:-/api}"
};
EOF
