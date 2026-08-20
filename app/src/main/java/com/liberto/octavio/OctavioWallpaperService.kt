package com.liberto.octavio

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import com.liberto.octavio.pet.PetMood
import com.liberto.octavio.pet.PetRepository
import com.liberto.octavio.pet.PetSimulation
import com.liberto.octavio.pet.PetSprites

/**
 * Live wallpaper de Octavio.
 *
 * Un [WallpaperService] dibuja directamente sobre el fondo de pantalla (y de
 * bloqueo) con Canvas. No hay Compose aquí: el bicho se anima con un bucle a
 * ~10 FPS (suficiente para pixel art y muy respetuoso con la batería).
 *
 * Interacción:
 *  - Tap corto          → alimentar (hambre a tope)
 *  - Pulsación larga    → acariciar (felicidad a tope)
 *
 * El estado (hambre/energía/felicidad) se deriva del tiempo y se persiste en
 * [PetRepository]; el dibujo simplemente lee el snapshot actual.
 */
class OctavioWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = OctavioEngine()

    private inner class OctavioEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private val repo = PetRepository(this@OctavioWallpaperService)

        // Pintura del sprite: filtro desactivado = escalado "nearest neighbor",
        // imprescindible para que el pixel art se vea nítido (no borroso).
        private val spritePaint = Paint().apply {
            isFilterBitmap = false
            isAntiAlias = false
            isDither = false
        }
        private val barraFondoPaint = Paint().apply { color = Color.rgb(40, 40, 56) }
        private val barraHambrePaint = Paint().apply { color = Color.rgb(255, 190, 60) }
        private val barraEnergiaPaint = Paint().apply { color = Color.rgb(120, 200, 250) }

        // Sprites pre-renderizados: mood → [frame0, frame1].
        private val bitmaps: Map<PetMood, Array<Bitmap>> =
            PetMood.entries.associateWith { mood ->
                arrayOf(
                    PetSprites.render(PetSprites.sprite(mood, 0)),
                    PetSprites.render(PetSprites.sprite(mood, 1)),
                )
            }

        private var downTime = 0L                 // inicio del toque (para distinguir tap/long-press)
        private var happyUntil = 0L               // feedback: mostrar cara contenta tras tocar
        private var visible = false

        private val drawRunnable = object : Runnable {
            override fun run() {
                draw()
                handler.postDelayed(this, FRAME_INTERVAL_MS)
            }
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            if (isVisible) {
                handler.removeCallbacks(drawRunnable)
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onSurfaceDestroyed(holder: android.view.SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
        }

        override fun onTouchEvent(event: MotionEvent) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> downTime = System.currentTimeMillis()

                MotionEvent.ACTION_UP -> {
                    val duracion = System.currentTimeMillis() - downTime
                    if (duracion < LONG_PRESS_MS) {
                        repo.alimentar()       // tap = comida
                    } else {
                        repo.acariciar()       // mantener = caricia
                    }
                    happyUntil = System.currentTimeMillis() + FEEDBACK_MS
                    draw()                      // refresco inmediato para que reaccione al toque
                }
            }
            super.onTouchEvent(event)
        }

        private fun draw() {
            val holder = surfaceHolder
            val canvas = try {
                holder.lockCanvas()
            } catch (_: Exception) {
                return
            } ?: return

            try {
                dibujar(canvas)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }

        private fun dibujar(canvas: Canvas) {
            val w = canvas.width
            val h = canvas.height
            val ahora = System.currentTimeMillis()

            // Fondo oscuro
            canvas.drawColor(Color.rgb(18, 18, 30))

            val snapshot = PetSimulation.snapshot(repo.load(), ahora)

            // Mood efectivo. Prioridad:
            //  1) Forzado manual desde la app (botones de prueba).
            //  2) Cara contenta si acabamos de tocar.
            //  3) Estado natural derivado del tiempo.
            val mood = repo.moodOverride()
                ?: if (ahora < happyUntil) PetMood.HAPPY
                else snapshot.mood

            // Parpadeo: dentro de cada ciclo de 4 s, cierra los ojos 150 ms.
            val frame = if (mood == PetMood.IDLE && (ahora % 4000) < 150) 1 else 0

            val bmp = bitmaps[mood]?.get(frame) ?: return

            // El bicho ocupa ~62% del lado menor de la pantalla (más grande aún).
            val target = (minOf(w, h) * 0.62f).toInt().coerceAtLeast(64)
            val left = (w - target) / 2f
            val top = (h - target) / 2f
            canvas.drawBitmap(
                bmp, null,
                RectF(left, top, left + target, top + target),
                spritePaint,
            )

            dibujarBarras(canvas, w, h, snapshot, top + target)
        }

        /** Barras de hambre y energía, discretas, debajo del bicho. */
        private fun dibujarBarras(
            canvas: Canvas,
            w: Int,
            h: Int,
            s: com.liberto.octavio.pet.PetSnapshot,
            baseY: Float,
        ) {
            val anchoBarra = (w * 0.28f).coerceAtMost(220f)
            val altoBarra = 8f
            val x0 = (w - anchoBarra) / 2f
            val y0 = baseY + 24f

            // Hambre
            canvas.drawRoundRect(RectF(x0, y0, x0 + anchoBarra, y0 + altoBarra), 4f, 4f, barraFondoPaint)
            canvas.drawRoundRect(
                RectF(x0, y0, x0 + anchoBarra * s.hambre / 100f, y0 + altoBarra), 4f, 4f, barraHambrePaint,
            )
            // Energía
            val y1 = y0 + altoBarra + 6f
            canvas.drawRoundRect(RectF(x0, y1, x0 + anchoBarra, y1 + altoBarra), 4f, 4f, barraFondoPaint)
            canvas.drawRoundRect(
                RectF(x0, y1, x0 + anchoBarra * s.energia / 100f, y1 + altoBarra), 4f, 4f, barraEnergiaPaint,
            )
        }

    }
}

// Constantes del motor, a nivel de archivo: una `inner class` no puede declarar
// un `companion object`, así que las sacamos fuera.
private const val FRAME_INTERVAL_MS = 100L   // ~10 FPS
private const val LONG_PRESS_MS = 400L
private const val FEEDBACK_MS = 1500L
