# Octavio Android — Especificación

Asistente personal en forma de Tamagotchi que vive en la pantalla de bloqueo.
Uso personal, sin Play Store. Dispositivo objetivo: Nothing Phone 2a
(Dimensity 7200 Pro, 8/12 GB RAM, Android 14→15).

## Objetivo

Tener un asistente de IA propio, con personalidad (Octavio), accesible desde el
atajo de siempre y con mecánicas de mascota virtual, que además conecta con
Hermes para acceder al ordenador y a todo el trabajo que hay en él.

## Decisiones arquitectónicas

1. **Live wallpaper, no widget.** `WallpaperService` + Canvas permite animar el
   bicho en la pantalla de bloqueo y recibir toques. Los widgets de lock screen
   de Android 16 (RemoteViews) no permiten animación.
2. **Estado derivado del tiempo.** Se persisten timestamps de último evento
   (comer/dormir/acariciar); el valor se calcula al renderizar. Sin proceso en
   background, sin gasto de batería.
3. **Kotlin nativo, no CMP.** No hay Compose en un wallpaper; no hay versión iOS
   de un live wallpaper interactivo.
4. **Voz local en el móvil (sherpa-onnx: Whisper + Piper), cerebro en Hermes.**
   El STT/TTS van en el dispositivo (privacidad, y el bridge queda simple:
   solo texto). El LLM viaja por Tailscale al perfil `tamagotchi` de Hermes.

## Fases

### Fase 1 — El bicho (MVP visual)
- Live wallpaper con sprite animado (parpadeo, estados).
- Estados: hambre/energía/felicidad derivados del tiempo.
- Toques: tap = alimentar, long-press = acariciar.
- Persistencia con SharedPreferences.
- **Hecho cuando:** el bicho se ve en la pantalla de bloqueo, reacciona a toques
  y conserva su estado entre sesiones.

### Fase 2 — La voz y el puente a Hermes
- STT/TTS local con sherpa-onnx (Whisper `es` + Piper `es_ES-davefx`).
- Bridge FastAPI en `libertoia-1:8000` → Hermes (perfil `tamagotchi`).
- Comunicación por Tailscale (`100.77.253.50`).
- Atajo: tocar el bicho = push-to-talk (el hotword "Hey Google" no es
  interceptable sin root).

### Fase 3 — Asistente por defecto
- `VoiceInteractionService` + rol de asistente.
- Nothing OS conserva el ajuste estándar (Ajustes → Apps → App de asistencia),
  así que el gesto de esquina debería disparar Octavio.

### Fase 4 — LLM local (opcional)
- llama.cpp con modelo 1-2B cuantizado (Qwen2.5-1.5B o Llama 3.2 1B) para
  respuestas offline (~10-15 tok/s en el Dimensity 7200 Pro).

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|--------|-----------|
| "Hey Google" no es interceptable | Push-to-talk (tocar el bicho) |
| Botón de encendido atado a Gemini en algunos OEM | Gesto de esquina / tocar el bicho |
| LLM local lento en gama media | Cerebro en Hermes; local solo como fallback |
| Dos nodos Tailscale del móvil (duplicado) | Limpiar el nodo viejo (`nothing-phone-2a`) |
