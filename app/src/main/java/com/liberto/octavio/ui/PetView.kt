package com.liberto.octavio.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.liberto.octavio.pet.PetMood
import com.liberto.octavio.pet.PetSprites

/**
 * Vista de preview que renderiza y anima a Octavio dentro de la app.
 *
 * Replica la lógica de dibujo del wallpaper ([OctavioWallpaperService]), pero
 * pensada para un [View] normal: permite cambiar el mood en caliente con
 * [setMood] para ver las distintas animaciones sin tener que mirar el fondo.
 */
class PetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    // Pintura con filtro desactivado = escalado "nearest neighbor" (pixel nítido).
    private val spritePaint = Paint().apply {
        isFilterBitmap = false
        isAntiAlias = false
        isDither = false
    }

    // Sprites pre-renderizados: mood → [frame0, frame1].
    private val bitmaps: Map<PetMood, Array<Bitmap>> =
        PetMood.entries.associateWith { mood ->
            arrayOf(
                PetSprites.render(PetSprites.sprite(mood, 0)),
                PetSprites.render(PetSprites.sprite(mood, 1)),
            )
        }

    private var mood: PetMood = PetMood.IDLE

    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            invalidate()                     // redibuja -> onDraw lee el mood actual
            handler.postDelayed(this, FRAME_INTERVAL_MS)
        }
    }

    /** Cambia el mood mostrado y fuerza un repintado inmediato. */
    fun setMood(nuevo: PetMood) {
        mood = nuevo
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.post(tick)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(tick)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(18, 18, 30))

        // Parpadeo solo en idle: dentro de cada ciclo de 4 s cierra los ojos 150 ms.
        val frame = if (mood == PetMood.IDLE && (System.currentTimeMillis() % 4000) < 150) 1 else 0
        val bmp = bitmaps[mood]?.get(frame) ?: return

        val target = minOf(width, height)
        val left = (width - target) / 2f
        val top = (height - target) / 2f
        canvas.drawBitmap(bmp, null, RectF(left, top, left + target, top + target), spritePaint)
    }

    private companion object {
        const val FRAME_INTERVAL_MS = 100L   // ~10 FPS
    }
}
