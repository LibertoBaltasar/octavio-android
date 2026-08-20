package com.liberto.octavio.pet

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistencia del estado del bicho.
 *
 * Para la Fase 1 usamos SharedPreferences (simple y robusto). Si más adelante
 * hace falta algo más rico (historial, estadísticas), se migra a Room/DataStore.
 * La API de esta clase no cambia: el resto del código solo ve "load" y las
 * acciones (alimentar/dormir/acariciar).
 */
class PetRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("octavio_pet", Context.MODE_PRIVATE)

    /** Carga el estado. Si es la primera vez, inicializa los timestamps "ahora". */
    fun load(): PetState {
        val ahora = System.currentTimeMillis()
        return PetState(
            ultimaComida = prefs.getLong(KEY_COMIDA, ahora),
            ultimoSueno = prefs.getLong(KEY_SUENO, ahora),
            ultimoCarino = prefs.getLong(KEY_CARINO, ahora),
        )
    }

    /** Alimentar: resetea el hambre a "recién comido". */
    fun alimentar() = marcar(KEY_COMIDA)

    /** Dormir: resetea la energía a "recién descansado". */
    fun dormir() = marcar(KEY_SUENO)

    /** Acariciar: resetea la felicidad a "recién mimado". */
    fun acariciar() = marcar(KEY_CARINO)

    /** Fuerza un mood para pruebas desde la app (null = comportamiento natural). */
    fun setMoodOverride(mood: PetMood?) {
        prefs.edit().putString(KEY_MOOD_OVERRIDE, mood?.name ?: "").apply()
    }

    /** Devuelve el mood forzado manualmente, o null si el estado es natural. */
    fun moodOverride(): PetMood? =
        prefs.getString(KEY_MOOD_OVERRIDE, null)
            ?.let { runCatching { PetMood.valueOf(it) }.getOrNull() }

    private fun marcar(clave: String) {
        prefs.edit().putLong(clave, System.currentTimeMillis()).apply()
    }

    private companion object {
        const val KEY_COMIDA = "ultimaComida"
        const val KEY_SUENO = "ultimoSueno"
        const val KEY_CARINO = "ultimoCarino"
        const val KEY_MOOD_OVERRIDE = "moodOverride"
    }
}
