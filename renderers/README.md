# ColaLauncher Renderer Integration

This directory tracks graphics backend integration for ColaLauncher on Android.

## Components

| Component | Role | Status |
|---|---|---|
| LWJGL | Minecraft Java graphics, input and windowing layer | Not integrated |
| ANGLE | OpenGL ES translation layer | Not integrated |
| Zink | OpenGL implementation over Vulkan through Mesa | Not integrated |
| Adreno | Qualcomm GPU family; requires device-specific compatibility handling | Detection/integration pending |
| GL4ES | OpenGL compatibility layer over OpenGL ES | Not integrated |

## Integration rules

- Do not mark a backend as working until it has been built and tested on Android.
- Check each upstream project's license before bundling binaries or source.
- Keep architecture-specific libraries separate, such as arm64-v8a and armeabi-v7a.
- Select a backend only when its required libraries and device capabilities are available.
- Preserve the original Minecraft version and loader compatibility.

## Current goal

Establish the integration structure first. Native libraries, build configuration,
runtime selection, and device testing are separate tasks.
