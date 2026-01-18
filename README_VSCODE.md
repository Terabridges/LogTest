# VS Code setup (Windows) for this FTC/Android Gradle project

This project builds with the Android Gradle Plugin **8.7.0** and Gradle **8.9**. That combination expects **JDK 17**.

The easiest way to match Android Studio is to point VS Code at the *same Gradle JDK* Android Studio uses.

## 1) Find the exact JDK path Android Studio uses

In Android Studio:

1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. Look at **Gradle JDK**
3. Copy the path.

Common default on Windows is Android Studio’s embedded JBR:

- `C:\Program Files\Android\Android Studio\jbr`

(If you installed Android Studio elsewhere, the path will differ.)

## 2) Set JAVA_HOME (recommended)

Set `JAVA_HOME` to the JDK *folder* (not the `bin` folder).

PowerShell (current session only):

```powershell
$env:JAVA_HOME = "C:\\Program Files\\Android\\Android Studio\\jbr"
$env:Path = "$env:JAVA_HOME\\bin;" + $env:Path
& "$env:JAVA_HOME\\bin\\java.exe" -version
```

Persist it (new terminals / VS Code windows will pick it up):

```powershell
setx JAVA_HOME "C:\\Program Files\\Android\\Android Studio\\jbr"
```

Then restart VS Code.

## 3) Open the folder in VS Code

Open the repository root folder (the one containing `gradlew.bat`).

The repo includes:

- `.vscode/settings.json` → points Java/Gradle at `${env:JAVA_HOME}`
- `.vscode/tasks.json` → build tasks using `gradlew.bat`

## 4) Build from VS Code

Run:

- **Terminal → Run Task… → Gradle: TeamCode assembleDebug**

If you get `JAVA_HOME is not set`, VS Code was started before `JAVA_HOME` was set — restart VS Code.

## 5) Deploy to a Control Hub / Robot Controller over ADB (recommended CLI flow)

This repo is set up so the **app APK** is produced by the `:TeamCode` module (the `:FtcRobotController` module is an Android **library**).

### A) Verify ADB connection

Typical Control Hub endpoint (Wi-Fi ADB):

```powershell
adb connect 192.168.43.1:5555
adb devices -l
```

### B) Build the debug APK

```powershell
cd <repoRoot>
./gradlew.bat :TeamCode:assembleDebug --no-daemon
```

### C) Install the APK

```powershell
adb install -r .\TeamCode\build\outputs\apk\debug\TeamCode-debug.apk
```

### D) Restart the Robot Controller app (optional but helps)

```powershell
adb shell am force-stop com.qualcomm.ftcrobotcontroller
adb shell am start -n com.qualcomm.ftcrobotcontroller/org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
```

### E) Sanity-check that the install actually changed the app

```powershell
$p = (adb shell pm path com.qualcomm.ftcrobotcontroller).Trim() -replace '^package:'
adb shell ls -l "$p"
```

### Common pitfall

- `./gradlew.bat :FtcRobotController:installDebug` installs **only** the `androidTest` APK in this repo layout and will *not* update the running Robot Controller app.

## Notes

- Android SDK location is already configured in `local.properties` (`sdk.dir=...`).
- This is primarily a *build* setup. Debugging on-phone typically still works best via Android Studio.
