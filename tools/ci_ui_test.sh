#!/usr/bin/env bash
# Runs the instrumented Compose tests on the emulator started by
# reactivecircus/android-emulator-runner, keeps the logs for the CI artifact
# and captures a screenshot of the home screen.
set -o pipefail

./gradlew --no-daemon connectedDebugAndroidTest --stacktrace 2>&1 | tee gradle-test.log
rc=$?

adb logcat -d > logcat.txt 2>/dev/null || true

mkdir -p docs/screenshots
adb shell am force-stop com.hisnulmuslim.amharic.debug || true
adb shell am start -W -n com.hisnulmuslim.amharic.debug/com.hisnulmuslim.amharic.MainActivity >/dev/null || true
sleep 8
adb exec-out screencap -p > docs/screenshots/home.png || true
ls -l docs/screenshots || true

exit "$rc"
