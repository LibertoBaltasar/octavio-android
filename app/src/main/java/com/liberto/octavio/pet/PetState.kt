package com.liberto.octavio.pet

/**
 * Modelo de estado de Octavio.
 *
 * Principio clave (heredado del Tamagotchi ESP32): ninguna necesidad se guarda
 * como un número "actual", sino como un TIMESTAMP del último evento
 * (comer, dormir, acariciar). El valor actual se DERIVA del tiempo transcurrido.
 * Así el bicho "vive" entre toques sin necesitar un proceso en background:
 * cuando lo miras, calculamos cómo está.
 */

/** Estado de ánimo que determina qué sprite se dibuja. */
enum class PetMood { IDLE, HAPPY, HUNGRY, SLEEP }

/** Instantánea calculada del bicho en un momento dado (valores 0..100). */
data class PetSnapshot(
    val hambre: Int,     // 100 = saciado, 0 = famélico
    val energia: Int,    // 100 = descansado, 0 = agotado
    val felicidad: Int,  // 100 = feliz, 0 = triste
) {
    val mood: PetMood get() = when {
        energia < 20 -> PetMood.SLEEP
        hambre < 25 -> PetMood.HUNGRY
        felicidad > 70 -> PetMood.HAPPY
        else -> PetMood.IDLE
    }
}

/** Estado persistido: solo timestamps del último evento de cada tipo. */
data class PetState(
    val ultimaComida: Long,   // epoch millis
    val ultimoSueno: Long,
    val ultimoCarino: Long,
)

/** Lógica pura: convierte (estado + hora actual) en una instantánea. */
object PetSimulation {

    // Cuánto tarda cada necesidad en agotarse por completo (en milisegundos).
    const val TIEMPO_HAMBRE_MS = 6L * 60 * 60 * 1000       // 6 h hasta hambre total
    const val TIEMPO_SUENO_MS = 10L * 60 * 60 * 1000       // 10 h hasta agotarse
    const val TIEMPO_FELICIDAD_MS = 24L * 60 * 60 * 1000   // 24 h hasta tristeza total

    fun snapshot(state: PetState, ahora: Long = System.currentTimeMillis()): PetSnapshot {
        return PetSnapshot(
            hambre = necesidad(state.ultimaComida, ahora, TIEMPO_HAMBRE_MS),
            energia = necesidad(state.ultimoSueno, ahora, TIEMPO_SUENO_MS),
            felicidad = necesidad(state.ultimoCarino, ahora, TIEMPO_FELICIDAD_MS),
        )
    }

    /** 100 recién satisfecha → 0 al agotarse, lineal con el tiempo. */
    private fun necesidad(ultimo: Long, ahora: Long, tiempoAgotarMs: Long): Int {
        val transcurrido = ahora - ultimo
        val valor = 100L - (transcurrido * 100L / tiempoAgotarMs)
        return valor.coerceIn(0L, 100L).toInt()
    }
}
