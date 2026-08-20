# Octavio — Asistente Tamagotchi para Android

Un asistente personal con forma de bicho virtual (pixel art, gato negro de ojos
amarillos) que **vive en la pantalla de bloqueo** de un Nothing Phone 2a.

Es, a la vez:

- **Un Tamagotchi** — tiene hambre, sueño y felicidad; los tocas para cuidarlo.
- **Un asistente de IA** — habla contigo y, vía Hermes, tiene acceso a tu ordenador
  (Task Hub, Anytype, notas de voz, terminal…).
- **100 % tuyo y local** — sin Play Store, sin dependencias de Google.

## Estado del proyecto

| Fase | Qué | Estado |
|------|-----|--------|
| 1 | Bicho pixel art en live wallpaper (animación, toques, persistencia) | ✅ en curso |
| 2 | Voz local (STT/TTS) + puente a Hermes vía Tailscale | ⏳ planificada |
| 3 | Asistente por defecto (gesto de esquina) | ⏳ planificada |
| 4 | LLM local (llama.cpp, 1-2B) como fallback offline | ⏳ opcional |

## Arquitectura

```
─────────────── Nothing 2a (local) ───────────────
 Bicho (live wallpaper)   → Canvas, animación, toques
 PetSimulation            → estado derivado de tiempo
 PetRepository            → persistencia (SharedPreferences)
        │
        │  (Fase 2) texto JSON vía Tailscale
        ▼
─────────────── libertoia-1 (Linux) ───────────────
 Bridge FastAPI :8000     → /api/chat {texto}
 Hermes (perfil tamagotchi) → DeepSeek via Nous
        ├── terminal, Task Hub, Anytype, notas voz…
        └── todo el contexto de trabajo
```

### Decisiones de diseño clave

- **Live wallpaper (no widget):** un `WallpaperService` dibuja con Canvas y se
  muestra en pantalla de bloqueo, anima a 60 fps y recibe toques. Los widgets de
  lock screen (Android 16) usan `RemoteViews` y no pueden animar un bicho.
- **Estado derivado del tiempo:** igual que el ESP32 de Octavio, no se guarda el
  valor actual sino el *timestamp* del último evento; el valor se calcula al mirar.
  Así el bicho "vive" entre toques sin gastar batería ni necesitar background.
- **Kotlin nativo (no Compose Multiplatform):** un live wallpaper es un servicio
  Android con Canvas; no hay Compose en un wallpaper y no existe versión iOS de
  un live wallpaper interactivo de terceros.

## Compilar e instalar

```bash
# Compilar APK de debug
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/app-debug.apk

# Instalar en el Nothing 2a (con ADB)
~/Android/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk

# Tras instalar: abre la app y pulsa "Establecer como fondo de pantalla".
```

## Estructura

```
app/src/main/java/com/liberto/octavio/
├── OctavioWallpaperService.kt   # WallpaperService + engine (dibujo y bucle)
├── pet/
│   ├── PetState.kt              # PetMood, PetSnapshot, PetState, PetSimulation
│   ├── PetRepository.kt         # persistencia (SharedPreferences)
│   └── PetSprites.kt            # sprites pixel art (portados del generador)
└── ui/
    └── MainActivity.kt          # lanzar la app y establecer el wallpaper

scripts/
└── gen_sprites.py               # generador de sprites (fuente del arte pixel)
```

## Interacción (Fase 1)

- **Tap corto** → alimentar (hambre a 100 %)
- **Pulsación larga** → acariciar (felicidad a 100 %)
- El bicho parpadea, se duerme, tiene hambre y se pone contento según el tiempo.
