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

## Notes

- Android SDK location is already configured in `local.properties` (`sdk.dir=...`).
- This is primarily a *build* setup. Debugging on-phone typically still works best via Android Studio.
