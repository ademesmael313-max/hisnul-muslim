#!/usr/bin/env bash
# Entry point for the instrumented Compose tests on the emulator started by
# reactivecircus/android-emulator-runner. It also copies the home-screen
# screenshot taken by AppSmokeTest out of the app's external files dir (with an
# adb screencap fallback) so it can be uploaded as a build artifact.
set -o pipefail

cd "${GITHUB_WORKSPACE:-$(pwd)}"

./gradlew --no-daemon connectedDebugAndroidTest --stacktrace 2>&1 | tee gradle-test.log
rc=$?

adb logcat -d > logcat.txt 2>/dev/null || true

out=app/build/outputs/ui-screenshot
mkdir -p "$out"
external=/sdcard/Android/data/com.hisnulmuslim.amharic.debug/files
adb pull "$external/screenshot-home.png" "$out/home.png" || true

if [ ! -s "$out/home.png" ]; then
  adb shell am force-stop com.hisnulmuslim.amharic.debug || true
  adb shell am start -W -n com.hisnulmuslim.amharic.debug/com.hisnulmuslim.amharic.MainActivity >/dev/null || true
  sleep 8
  adb exec-out screencap -p > "$out/home.png" || true
fi

ls -l "$out" || true
exit "$rc"
