#!/usr/bin/env bash
# Runs the instrumented Compose tests on the emulator started by
# reactivecircus/android-emulator-runner, keeps the logs for the CI artifact
# and captures a screenshot of the home screen.
set -o pipefail

./gradlew --no-daemon connectedDebugAndroidTest --stacktrace 2>&1 | tee gradle-test.log
rc=$?

adb logcat -d > logcat.txt 2>/dev/null || true

# Screenshot produced by AppSmokeTest.homeScreenRendersBook lives in the app's
# external files dir; pull it plus an adb screencap fallback.
mkdir -p docs/screenshots
external=/sdcard/Android/data/com.hisnulmuslim.amharic.debug/files
adb pull "$external/screenshot-home.png" docs/screenshots/home.png || true

if [ ! -s docs/screenshots/home.png ]; then
  adb shell am force-stop com.hisnulmuslim.amharic.debug || true
  adb shell am start -W -n com.hisnulmuslim.amharic.debug/com.hisnulmuslim.amharic.MainActivity >/dev/null || true
  sleep 8
  adb exec-out screencap -p > docs/screenshots/home.png || true
fi

{
  echo "adb devices:"; adb devices
  echo "host files:"; ls -l docs/screenshots
  echo "device files:"; adb shell ls -l "$external" 2>&1 | head -20
} > docs/screenshots/status.txt || true

exit "$rc"
