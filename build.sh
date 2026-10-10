#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_HOME:?Set ANDROID_HOME to your Android SDK directory}"
tools="$ANDROID_HOME/build-tools/35.0.0"
platform="$ANDROID_HOME/platforms/android-35/android.jar"
mkdir -p build/generated build/classes build/dex
"$tools/aapt" package -f -m -J build/generated -M AndroidManifest.xml -S res -I "$platform"
find src build/generated -name '*.java' > build/sources.txt
javac -encoding UTF-8 -source 8 -target 8 -classpath "$platform" -d build/classes @build/sources.txt
jar cf build/classes.jar -C build/classes .
"$tools/d8" --lib "$platform" --min-api 26 --output build/dex build/classes.jar
"$tools/aapt" package -f -M AndroidManifest.xml -S res -I "$platform" -F build/unsigned.apk
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$tools/zipalign" -f 4 build/unsigned.apk build/aligned.apk
if [[ ! -f development.keystore ]]; then
 keytool -genkeypair -keystore development.keystore -storepass android -keypass android -alias decimal-clock -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Decimal Clock Personal Build"
fi
"$tools/apksigner" sign --ks development.keystore --ks-key-alias decimal-clock --ks-pass pass:android --key-pass pass:android --out ../Decimal-Clock.apk build/aligned.apk
"$tools/apksigner" verify --verbose ../Decimal-Clock.apk
