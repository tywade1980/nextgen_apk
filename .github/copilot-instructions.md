# NextGen APK Development Instructions

NextGen APK is an Android application project. This repository currently contains minimal setup and is ready for Android development initialization.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

- **CRITICAL Environment Setup**:
  - Android SDK is pre-installed at `/usr/local/lib/android/sdk`
  - Java 17 is available at `/usr/bin/java`
  - Gradle 9.0.0 is available at `/usr/bin/gradle`
  - Kotlin 2.2.10 is available at `/usr/bin/kotlin`

- **Always set these environment variables before working**:
  ```bash
  export ANDROID_HOME=/usr/local/lib/android/sdk
  export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/35.0.0
  ```

- **Initialize new Android project structure**:
  ```bash
  mkdir -p src/main/java/com/example/nextgen
  mkdir -p src/main/res/layout
  mkdir -p src/main/res/values
  mkdir -p src/test/java
  mkdir -p src/androidTest/java
  ```

- **Build and Development Workflow**:
  - **NEVER CANCEL**: Network access is restricted - Gradle builds that require internet will fail
  - Build times: Manual compilation using Android SDK tools takes 2-5 minutes. NEVER CANCEL. Set timeout to 10+ minutes.
  - Resource compilation: `aapt2 compile --dir src/main/res -o compiled_res.zip` -- takes less than 1 second for basic resources
  - Use offline Android SDK tools for compilation and resource processing
  - Full Gradle builds with network dependencies will fail due to firewall restrictions

- **Available Android SDK Components**:
  - Build Tools: 34.0.0, 35.0.0, 35.0.1, 36.0.0
  - Platforms: android-33-ext4, android-33-ext5, android-34, android-34-ext8/10/11/12, android-35, android-35-ext14/15, android-36, android-36-ext18/19
  - NDK versions: 26.3.11579264, 27.3.13750724, 28.2.13676358
  - CMake 3.31.5
  - Platform Tools including ADB

## Testing and Validation

- **Unit Testing**: Create JUnit tests in `src/test/java` - can be compiled with `javac` and run offline
- **Resource Validation**: Always run `aapt2 compile --dir src/main/res -o compiled_res.zip` to validate resources
- **Device Testing**: Use `adb devices` to check connected devices (will show empty list in development environment)
- **Manual Validation**: Test basic Android SDK functionality by running resource compilation

## Environment Limitations

- **No network access**: Gradle builds requiring remote dependencies will fail
- **No Android Studio**: Use command-line tools only
- **No Flutter**: Only native Android development available
- **No emulator**: Physical device connection required for app testing

## Development Commands

**Basic resource compilation**:
```bash
export ANDROID_HOME=/usr/local/lib/android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/35.0.0
aapt2 compile --dir src/main/res -o compiled_res.zip
```

**Check available SDK components**:
```bash
export ANDROID_HOME=/usr/local/lib/android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin
sdkmanager --list_installed
```

**Device management**:
```bash
export PATH=$PATH:$ANDROID_HOME/platform-tools
adb devices
adb version
```

**Java compilation for Android**:
```bash
mkdir -p build/classes
javac -cp $ANDROID_HOME/platforms/android-35/android.jar -d build/classes src/main/java/**/*.java
```

## Project Structure

The repository currently contains:
```
/
├── README.md
└── .github/
    └── copilot-instructions.md
```

**Expected Android project structure after initialization**:
```
/
├── src/
│   ├── main/
│   │   ├── java/com/example/nextgen/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   ├── test/java/
│   └── androidTest/java/
├── build.gradle
├── settings.gradle
└── README.md
```

## Common Tasks

**Initialize Android project**:
1. Create project structure: `mkdir -p src/main/java/com/example/nextgen src/main/res/layout src/main/res/values`
2. Create basic `build.gradle` with buildscript dependencies (note: will fail to build due to network restrictions)
3. Create `AndroidManifest.xml`
4. Create initial Activity and layout files
5. Test resource compilation with `aapt2`

**Validate changes**:
1. Always run resource compilation after modifying resources
2. Compile Java sources against Android platform JAR
3. Check project structure matches Android conventions
4. Verify AndroidManifest.xml syntax

## Important Notes

- **NEVER CANCEL**: All builds and compilations should be allowed to complete - set timeouts to 10+ minutes minimum
- **Offline Development**: This environment is designed for offline Android development using pre-installed SDK tools
- **Manual Build Process**: Due to network restrictions, use manual compilation steps rather than full Gradle builds
- **Resource Focus**: Emphasis should be on resource management, layout design, and core Android application structure
- **Testing Approach**: Focus on resource validation and basic compilation rather than runtime testing

## Frequently Used Command Outputs

### Repository Root Contents
```bash
$ ls -la /home/runner/work/nextgen_apk/nextgen_apk
total 16
drwxr-xr-x 3 runner runner 4096 Sep 11 14:34 .
drwxr-xr-x 3 runner runner 4096 Sep 11 14:34 ..
drwxrwxr-x 7 runner runner 4096 Sep 11 14:34 .git
-rw-rw-r-- 1 runner runner   26 Sep 11 14:34 README.md
```

### Android SDK Structure
```bash
$ ls -la $ANDROID_HOME
build-tools/  cmake/  cmdline-tools/  extras/  licenses/  ndk/  platform-tools/  platforms/
```

### Available Build Tools
```bash
$ ls $ANDROID_HOME/build-tools/
34.0.0  35.0.0  35.0.1  36.0.0
```