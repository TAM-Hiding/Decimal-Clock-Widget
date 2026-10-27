# Decimal Clock 0.3

A small native Android app and home-screen widget for a decimal clock.
No dependencies, ads, accounts, network access, or data collection.

## Install
Download [Decimal-Clock.apk](https://github.com/TAM-Hiding/Decimal-Clock-Widget/raw/refs/heads/main/downloads/Decimal-Clock.apk) on your Android phone and tap it. If prompted, allow
the app opening the file to install unknown apps, then complete installation.
Open Decimal Clock. Tap Enable live widget, then Add home-screen widget.
If widget pinning is unavailable, long-press an empty home-screen area, select
Widgets, and choose Decimal Clock. The widget resizes and opens the clock when tapped.
Normal time is hidden by default. Enable Show normal time (app + widget) to
display it under the decimal clock. Live widgets have no tick-status label.
Install the new APK over the existing app to update while preserving settings.
The app fills the screen with its dark background and centers the contents in
the available height, retaining scrolling on smaller screens.

## Time
Uses local wall-clock time, divided by a nominal 24-hour day.
H.MMSS T is displayed without spaces as H.MMSST: 10 hours, 100 minutes per
hour, 100 seconds per minute, 10 ticks per second. Each tick is 0.0864 real
seconds. Noon is 5.00000 and 9 PM is 8.75000. DST changes follow the local clock
(the display can jump when the local wall clock jumps). This intentionally
matches the original Pydroid clock; it does not normalize 23/25-hour DST dates.

The open app samples every 43 ms. The normal widget updates at decimal-second
boundaries and fixes the final digit at zero. Optional
fast mode updates every 216 ms, samples genuine tick values, and skips some
digits naturally. Launcher redraw timing may vary; no high-frequency animation
is guaranteed. All displayed values are recalculated from time, never accumulated.

## Live widget
Requires a user-enabled foreground service with a low-priority notification.
Updates pause while the display is off; no wake locks or exact alarms are used.
Android may stop the service, especially with restricted battery settings. Open
the app and enable live again after reboot, force-stop, or stopping it in Android.
If it repeatedly freezes, check Settings → Apps → Decimal Clock → App battery
usage and allow background usage. Fast widget mode uses more battery.
Pause via the app or the notification's Stop button.
If the OS abruptly kills the process, the last widget frame may remain until
the process restarts or the next periodic widget refresh.

## Build
Requires Java 17 and Android SDK platform 35 / build tools 35.0.0.
Set ANDROID_HOME, then run bash build.sh. Tests:

    mkdir -p build/test
    javac -d build/test src/io/tam/decimalclock/DecimalTime.java TimeTest.java
    java -cp build/test TimeTest

This personal sideload build uses a development signing key. The signing key
is kept outside the public repository. Running build.sh without a key creates
your own development.keystore (password android). Keep that key for updates to
your own builds. A build with a different key cannot update the downloadable
APK in place; uninstalling first removes app settings.
It is a development key, not a production publishing identity. Do not use it
for publishing apps. Package: io.tam.decimalclock; minimum Android 8.

## Validation
Compiled against Android 35, dexed, aligned, and APK signatures verified.
Time conversion and update timing boundary tests passed. No Android emulator
or physical phone test has been performed; installation and launcher behavior
need confirmation on the phone.
