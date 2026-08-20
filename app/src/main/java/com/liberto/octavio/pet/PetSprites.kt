package com.liberto.octavio.pet

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Sprites de Octavio en pixel art (gato negro, ojos amarillos brillantes).
 *
 * Cada sprite es una matriz de strings: un carácter por píxel. El mapa [PALETA]
 * traduce carácter → color. Generados originalmente con `scripts/gen_sprites.py`
 * (mismo arte que el prototipo); aquí están portados a constantes Kotlin para no
 * depender de recursos PNG y poder escalar con "nearest neighbor" sin perder el
 * pixel nítido a cualquier tamaño.
 */
object PetSprites {

    private val IDLE_0 = arrayOf(
        "....KK....KK....",
        "...KKK....KKK...",
        "...KPK....KPK...",
        "...KKKKKKKKKK...",
        "..KBBBBBBBBBBK..",
        "..KBYYYBBYYYBK..",
        "..KBWYYBBYYWBK..",
        "..KBYYYBBYYYBK..",
        "..KBYYYBBYYYBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBPPPPBBBK..",
        "..KBBBBBBBBBBK..",
        "..KKKKKKKKKKKK..",
        "...KBBBBBBBBK...",
        "...KBBBBBBBBK...",
        "..KKKKKKKKKKKK..",
    )

    private val IDLE_1 = arrayOf(
        "....KK....KK....",
        "...KKK....KKK...",
        "...KPK....KPK...",
        "...KKKKKKKKKK...",
        "..KBBBBBBBBBBK..",
        "..KBYYYBBYYYBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBPPPPBBBK..",
        "..KBBBBBBBBBBK..",
        "..KKKKKKKKKKKK..",
        "...KBBBBBBBBK...",
        "...KBBBBBBBBK...",
        "..KKKKKKKKKKKK..",
    )

    private val HAPPY = arrayOf(
        "....KK....KK....",
        "...KKK....KKK...",
        "...KPK....KPK...",
        "...KKKKKKKKKK...",
        "..KBBBBBBBBBBK..",
        "..KBYYYBBYYYBK..",
        "..KBBYBBBBYBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBPPPPBBBK..",
        "..KBBBBPPBBBBK..",
        "..KKKKKKKKKKKK..",
        "...KBBBBBBBBK...",
        "...KBBBBBBBBK...",
        "..KKKKKKKKKKKK..",
    )

    private val HUNGRY = arrayOf(
        "....KK....KK....",
        "...KKK....KKK...",
        "...KPK....KPK...",
        "...KKKKKKKKKK...",
        "..KBBBBBBBBBBK..",
        "..KBBYYBBYYBBK..",
        "..KBBYYBBYYBBK..",
        "..KBBYYBBYYBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBPPPPPPBBK..",
        "..KBBBBBBBBBBK..",
        "..KKKKKKKKKKKK..",
        "...KBBBBBBBBK...",
        "..KKKKKKKKKKKK..",
    )

    private val SLEEP = arrayOf(
        "....KK....KK.ZZZ",
        "...KKK....KKK..Z",
        "...KPK....KPKZZZ",
        "...KKKKKKKKKK...",
        "..KBBBBBBBBBBK..",
        "..KBYYYBBYYYBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBBBBBBBBK..",
        "..KBBBPPPPBBBK..",
        "..KBBBBBBBBBBK..",
        "..KKKKKKKKKKKK..",
        "...KBBBBBBBBK...",
        "...KBBBBBBBBK...",
        "..KKKKKKKKKKKK..",
    )

    /** Carácter → color ARGB. '.' es transparente. */
    private val PALETA: Map<Char, Int> = mapOf(
        '.' to Color.TRANSPARENT,
        'K' to Color.rgb(6, 6, 10),        // outline
        'B' to Color.rgb(30, 28, 38),      // cuerpo negro
        'D' to Color.rgb(18, 16, 24),      // sombra
        'Y' to Color.rgb(255, 214, 64),    // ojos amarillo
        'y' to Color.rgb(210, 160, 20),    // amarillo oscuro
        'W' to Color.rgb(255, 255, 255),   // brillo ojo
        'P' to Color.rgb(240, 140, 155),   // rosa (nariz/orejas)
        'Z' to Color.rgb(150, 200, 235),   // "Z" del sueño
    )

    /** Devuelve el sprite (array de filas) para un mood y un frame. */
    fun sprite(mood: PetMood, frame: Int): Array<String> = when (mood) {
        PetMood.IDLE -> if (frame % 2 == 0) IDLE_0 else IDLE_1
        PetMood.HAPPY -> HAPPY
        PetMood.HUNGRY -> HUNGRY
        PetMood.SLEEP -> SLEEP
    }

    /** Renderiza un sprite a un Bitmap 1:1 (un píxel lógico = un píxel real). */
    fun render(rows: Array<String>): Bitmap {
        val h = rows.size
        val w = rows[0].length
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        for (y in rows.indices) {
            val row = rows[y]
            for (x in row.indices) {
                bmp.setPixel(x, y, PALETA[row[x]] ?: Color.TRANSPARENT)
            }
        }
        return bmp
    }
}
