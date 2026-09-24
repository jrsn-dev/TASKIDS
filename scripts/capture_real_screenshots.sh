#!/usr/bin/env bash
set -euo pipefail

mkdir -p real-screenshots

wait_for_device() {
  adb wait-for-device
  until [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
    sleep 2
  done
}

dump_ui() {
  adb shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
  adb pull /sdcard/window.xml /tmp/window.xml >/dev/null 2>&1 || true
}

find_text_center() {
  local target="$1"
  dump_ui
  python3 - "$target" <<'PY'
import re, sys, xml.etree.ElementTree as ET
target=sys.argv[1]
try:
    root=ET.parse("/tmp/window.xml").getroot()
except Exception:
    sys.exit(2)
root_bounds=root.attrib.get("bounds","[0,0][1920,1080]")
rm=re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", root_bounds)
if rm:
    rx1,ry1,rx2,ry2=map(int,rm.groups())
    cx,cy=(rx1+rx2)//2,(ry1+ry2)//2
else:
    cx,cy=960,540
matches=[]
for node in root.iter("node"):
    text=node.attrib.get("text","")
    desc=node.attrib.get("content-desc","")
    if text == target or desc == target:
        b=node.attrib.get("bounds","")
        m=re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", b)
        if m:
            x1,y1,x2,y2=map(int,m.groups())
            x,y=(x1+x2)//2,(y1+y2)//2
            matches.append(((x-cx)**2+(y-cy)**2,x,y))
if matches:
    if target.isdigit():
        matches.sort(key=lambda point: -point[2])
        _,x,y=matches[0]
        print(f"{x} {y}")
        sys.exit(0)
    _,x,y=min(matches)
    print(f"{x} {y}")
    sys.exit(0)
sys.exit(1)
PY
}

tap_text() {
  local target="$1"
  local attempts="${2:-10}"
  local i pos x y
  for i in $(seq 1 "$attempts"); do
    if pos="$(find_text_center "$target" 2>/dev/null)"; then
      read -r x y <<<"$pos"
      echo "tap '$target' at $x,$y"
      adb shell input tap "$x" "$y"
      sleep 1
      return 0
    fi
    sleep 1
  done
  echo "Could not find text: $target" >&2
  return 1
}

tap_text_optional() {
  local target="$1"
  local pos x y
  if pos="$(find_text_center "$target" 2>/dev/null)"; then
    read -r x y <<<"$pos"
    echo "optional tap '$target' at $x,$y"
    adb shell input tap "$x" "$y"
    sleep 2
  fi
}

capture() {
  local name="$1"
  sleep 2
  tap_text_optional "Wait"
  adb exec-out screencap -p > "real-screenshots/$name"
  echo "captured $name"
}

wait_for_device
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat -c
adb shell am force-stop com.aistudio.kidstasks.pqwxzt
adb shell am start -W -n com.aistudio.kidstasks.pqwxzt/com.example.MainActivity | tee real-screenshots/am-start.txt
sleep 8
adb shell pidof com.aistudio.kidstasks.pqwxzt > real-screenshots/pid.txt || true
adb shell dumpsys window windows | grep -E "mCurrentFocus|mFocusedApp" > real-screenshots/focus.txt || true
adb logcat -d > real-screenshots/logcat.txt || true

tap_text_optional "Wait"
capture "01-home-game.png"

tap_text "Missões" 10
capture "01a-child-missions.png"
tap_text "Início" 10
tap_text "Recompensas" 10
capture "01b-child-rewards.png"
tap_text "Início" 10

tap_text "PAIS" 15
sleep 2
capture "02-parent-pin.png"

tap_text "0" 10
tap_text "0" 10
tap_text "0" 10
tap_text "0" 10
sleep 5
tap_text_optional "Wait"
capture "03-parent-dashboard.png"

tap_text "Mais" 10
tap_text "Missões" 10
sleep 2
capture "04-parent-missions.png"

tap_text "Mais" 10
sleep 2
if ! tap_text "Biblioteca" 6; then
  adb shell input swipe 900 220 120 220 500 || true
  sleep 2
  tap_text "Biblioteca" 6
fi
sleep 2
capture "05-parent-library.png"

echo "Real screenshots captured:"
ls -lh real-screenshots
