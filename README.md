# HomeArcade Android

Native Android client for the [HomeArcade](https://github.com/GlerschNersch/token) Home Assistant addon.

Browse your shared ROM library, download games on demand, and play them with native libretro cores — all tied to your HomeArcade server for per-user save states and HA integration.

## Architecture

```
app/
├── data/
│   ├── api/          # Retrofit interface + response models
│   ├── local/        # DataStore preferences (server URL, HA token)
│   └── repository/   # Repository pattern over the API
├── di/               # Hilt modules
├── emulation/        # LibretroDroid wrapper + core mapping
└── ui/
    ├── navigation/   # NavHost + Screen sealed class
    ├── screens/      # home / library / player / settings / setup
    └── theme/        # Material 3 dark theme (orange accent)
```

## Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| DI | Hilt |
| Network | Retrofit 2 + OkHttp |
| Images | Coil |
| Preferences | DataStore |
| Emulation | LibretroDroid (libretro cores) |

## Setup

1. Open in Android Studio Hedgehog or later
2. Run on a device or emulator (API 26+)
3. Enter your HomeArcade server URL on first launch
4. Browse systems → pick a game → it downloads and launches natively

## Emulation cores

Cores are not bundled — add the compiled `.so` files to `app/src/main/jniLibs/arm64-v8a/`.  
See [LibretroDroid](https://github.com/Swordfish90/libretrodroid) for build instructions.

Supported systems: NES · SNES · N64 · GBA/GB/GBC · Genesis · PS1 · PS2 · PSP · NDS · Dreamcast · Arcade
