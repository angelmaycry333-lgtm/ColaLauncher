# ColaLauncher

Android Java starter project for a Minecraft Java launcher.

## Current state
- Android app shell with ColaLauncher branding
- Initial profile shown in UI: Minecraft Java 1.8.9 / Vanilla
- Basic settings placeholders for memory, JVM arguments and renderer
- GitHub Actions workflow to build a debug APK

## Important
This is a starter scaffold, **not yet a functioning Minecraft launcher**. It does not currently download Minecraft, bundle a Java Runtime, authenticate accounts, or launch the game. Those components need to be implemented and tested separately.

## Build locally
Open this folder in Android Studio and let Gradle sync. Use JDK 17 for the Android Gradle Plugin.
Then build `:app:assembleDebug`.

## Build with GitHub Actions
Push this project to a GitHub repository. The workflow in `.github/workflows/android-build.yml` builds a debug APK and uploads it as an Actions artifact.

## Planned milestones
1. Version manifest + downloader with SHA-1 validation
2. Android-compatible Java Runtime and native libraries
3. Minecraft 1.8.9 launch pipeline and input/rendering integration
4. Profiles, JVM settings and performance diagnostics
5. Fabric/Forge/NeoForge/Quilt, OptiFine and modpack import, each tested for compatibility
