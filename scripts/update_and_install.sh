#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
LOG_FILE="${ROOT_DIR}/version-log.txt"
GRADLE_FILE="${ROOT_DIR}/uSBeaconWriterFullVersion/build.gradle"
BASE_VERSION="1.2a"
APP_ID="com.THLight.BLE.USBeacon.Writer.Simple.dev"
APK_PATH="${ROOT_DIR}/uSBeaconWriterFullVersion/build/outputs/apk/debug/uSBeaconWriterFullVersion-debug.apk"
ADB="/Users/cj/Library/Android/sdk/platform-tools/adb"

if [ ! -f "${LOG_FILE}" ]; then
  echo "# version | createdAt | reason" > "${LOG_FILE}"
fi

read -r -p "修改目的: " REASON
if [ -z "${REASON}" ]; then
  echo "修改目的不得為空"
  exit 1
fi

LAST_LINE="$(awk 'NF && $1 !~ /^#/{line=$0} END{print line}' "${LOG_FILE}")"
SEQ=0
if [ -n "${LAST_LINE}" ]; then
  LAST_VERSION="$(echo "${LAST_LINE}" | awk -F'|' '{gsub(/^[ \t]+|[ \t]+$/,"",$1); print $1}')"
  LAST_SEQ="${LAST_VERSION##*.}"
  if [[ "${LAST_SEQ}" =~ ^[0-9]{4}$ ]]; then
    SEQ=$((10#${LAST_SEQ}))
  fi
fi

SEQ=$((SEQ + 1))
SEQ_PAD="$(printf "%04d" "${SEQ}")"
VERSION="${BASE_VERSION}.${SEQ_PAD}"
TS="$(date +"%Y/%m/%d %H:%M")"

echo "${VERSION} | ${TS} | ${REASON}" >> "${LOG_FILE}"

/usr/bin/sed -i '' "s/versionName \".*\"/versionName \"${VERSION}\"/" "${GRADLE_FILE}"

export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH="${JAVA_HOME}/bin:${PATH}"

cd "${ROOT_DIR}"
./gradlew assembleDebug

if [ ! -x "${ADB}" ]; then
  echo "adb not found: ${ADB}"
  exit 1
fi

"${ADB}" install -r "${APK_PATH}"
"${ADB}" shell am force-stop "${APP_ID}" || true
"${ADB}" shell monkey -p "${APP_ID}" -c android.intent.category.LAUNCHER 1

echo "Installed ${VERSION}"
