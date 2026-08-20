package com.liberto.octavio.ui

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.liberto.octavio.OctavioWallpaperService
import com.liberto.octavio.pet.PetRepository
import com.liberto.octavio.pet.PetSimulation

/**
 * Actividad principal: muestra el estado actual de Octavio y permite
 * establecerlo como fondo de pantalla. UI programática (sin appcompat/material)
 * para mantener la app mínima en la Fase 1.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = PetRepository(this)
        val estado = PetSimulation.snapshot(repo.load())

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }

        val titulo = TextView(this).apply {
            text = "Octavio"
            textSize = 30f
            gravity = Gravity.CENTER
        }

        val descripcion = TextView(this).apply {
            text = buildString {
                append("Tu bicho vive en la pantalla de bloqueo.\n\n")
                append("Estado: ${estado.mood}\n")
                append("Hambre: ${estado.hambre}%\n")
                append("Energía: ${estado.energia}%\n")
                append("Felicidad: ${estado.felicidad}%\n\n")
                append("Tócalo para alimentarlo; mantén pulsado para acariciarlo.")
            }
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }

        val boton = Button(this).apply {
            text = "Establecer como fondo de pantalla"
            setOnClickListener { abrirSelectorWallpaper() }
        }

        root.addView(titulo)
        root.addView(descripcion)
        root.addView(boton)
        setContentView(root)
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
}
