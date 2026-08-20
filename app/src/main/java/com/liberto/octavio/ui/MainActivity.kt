package com.liberto.octavio.ui

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.liberto.octavio.OctavioWallpaperService
import com.liberto.octavio.pet.PetMood
import com.liberto.octavio.pet.PetRepository

/**
 * Actividad principal: preview en vivo de Octavio + botones para disparar cada
 * animación. Cada botón además fuerza el mood en el wallpaper, para poder verlo
 * también en la pantalla de bloqueo sin depender del toque (poco fiable en
 * lock screens).
 */
class MainActivity : Activity() {

    private lateinit var petView: PetView
    private lateinit var repo: PetRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = PetRepository(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32, 48, 32, 48)
        }

        val titulo = TextView(this).apply {
            text = "Octavio"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        petView = PetView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(300),
            )
        }

        val instrucciones = TextView(this).apply {
            text = "Pulsa un estado para ver su animación en directo.\nEl mismo estado se aplica al fondo de pantalla."
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
        }

        root.addView(titulo)
        root.addView(petView)
        root.addView(instrucciones)
        root.addView(filaMoods())
        root.addView(botonNatural())
        root.addView(botonWallpaper())

        setContentView(root)
    }

    /** Fila de botones: uno por cada estado de ánimo. */
    private fun filaMoods(): View {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        fila.addView(botonMood("Idle", PetMood.IDLE))
        fila.addView(botonMood("Contento", PetMood.HAPPY))
        fila.addView(botonMood("Hambre", PetMood.HUNGRY))
        fila.addView(botonMood("Dormido", PetMood.SLEEP))
        return fila
    }

    /** Botón de estado: cambia el preview Y fuerza el mood en el wallpaper. */
    private fun botonMood(texto: String, mood: PetMood): Button =
        Button(this).apply {
            text = texto
            setOnClickListener {
                petView.setMood(mood)
                repo.setMoodOverride(mood)
            }
        }

    /** Vuelve al estado natural (derivado del tiempo) y quita el forzado. */
    private fun botonNatural(): Button =
        Button(this).apply {
            text = "Estado natural (según el tiempo)"
            setOnClickListener {
                repo.setMoodOverride(null)
                petView.setMood(PetMood.IDLE)
            }
        }

    private fun botonWallpaper(): Button =
        Button(this).apply {
            text = "Establecer como fondo de pantalla"
            setOnClickListener { abrirSelectorWallpaper() }
        }

    /** Abre el selector de live wallpapers apuntando directamente a Octavio. */
    private fun abrirSelectorWallpaper() {
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@MainActivity, OctavioWallpaperService::class.java),
            )
        }
        startActivity(intent)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density + 0.5f).toInt()
}
