package com.liberto.octavio.pet

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Sprites de Octavio v3 (40x40) — gato negro pixel art, cabezón y redondo.
 *
 * Dirección de diseño (Liberto):
 *  - Boca en NEGRO, sutil e integrada en el pelaje (mismo tono del outline).
 *  - Nariz = un punto rosa.
 *  - Orejas simétricas y naturales.
 *  - Cuerpo con sombreado (luz arriba-izquierda, con dithering) para que la
 *    cabeza se lea como una esfera y no como un cuadrado plano.
 *
 * Estos grids se GENERAN con `scripts/gen_sprites.py` (la fuente del arte);
 * este archivo .kt se regenera íntegro con ese script. Un carácter por píxel;
 * [PALETA] traduce carácter → color.
 */
object PetSprites {

    private val IDLE_0 = arrayOf(
        "........................................",
        "........................................",
        "........................................",
        "..........K...................K.........",
        ".........KK..................KK.........",
        "........KKPK................KKPK........",
        "........KPPK................KPPK........",
        ".......KPPPPK..............KPPPPK.......",
        ".......KPPPPK..KKKKHKKKK...KPPPPK.......",
        "......KPPPPPPKHHHHHHHHHHHKKPPPPPPK......",
        "......KPPPPKHHHHHHHHHHHHHHBKPPPPPK......",
        ".....KPPPPKHHHHHHHHHHHHHHBHBKPPPPPK.....",
        ".....KKKKKHHHHHHHHHHHHHHBHBBBKKKKKK.....",
        "....KKKKKHHHHHHHHHHHHHHBHBBBBBKKKKKK....",
        ".......KHHHHHHHHHHHHHHBHBBBBBBSK........",
        ".......HHHHHHHHHHHHHHBHBBBBBBSBS........",
        "......KHHHHKKKKHHHHHBHBBKKKKSBSSK.......",
        "......KHHHKYYYYKHHHBHBBKYYYYKSSSK.......",
        "......HHHHKYNWYKHHBHBBBKYNWYKSSSS.......",
        ".....KHHHHYYWNYYHBHBBBBYYWNYYSSSSK......",
        ".....RRHHHYYNNYYBHBBBBBYYNNYYSSSSRR.....",
        ".....KHHHHKYNNYKHBBBBBBKYNNYKSSSSK......",
        "....RRHHHHKYYYYKBBBBBBSKYYYYKSSSSKRR....",
        "......HHHHHKKKKBBBBBBSBSKKKKSSSSS.......",
        ".....RRHHHHHBHBBBBBBSSSSSSSSSSSSKRR.....",
        "......KHHHHBHBBBBBBPPSSSSSSSSSSSK.......",
        ".......HHHBHBBBBSBSSSSSSSSSSSSSS........",
        ".......KHBHBBBBSBSKSSKSSSSSSSSSK........",
        "........KBBBBBSBSSSKKSSSSSSSSSK.........",
        ".........KBBBSBKKKKKKKKKSSSSSK..........",
        "..........KBSKKBBBBBBBBBKKSSK...........",
        "...........KKKBBSSSSSSSBBKKK............",
        "............KBBSSSSSSSSSBBK.............",
        "...........KKBBSSSSSSSSSBBKK............",
        "............KBBBSSSSSSSBBBK.............",
        "............KKBBBBBBBBBBBKK.............",
        ".............KKBBBBBBBBBKK..............",
        "...............KKKKKKKKK................",
        "...................K....................",
        "........................................",
    )

    private val IDLE_1 = arrayOf(
        "........................................",
        "........................................",
        "........................................",
        "..........K...................K.........",
        ".........KK..................KK.........",
        "........KKPK................KKPK........",
        "........KPPK................KPPK........",
        ".......KPPPPK..............KPPPPK.......",
        ".......KPPPPK..KKKKHKKKK...KPPPPK.......",
        "......KPPPPPPKHHHHHHHHHHHKKPPPPPPK......",
        "......KPPPPKHHHHHHHHHHHHHHBKPPPPPK......",
        ".....KPPPPKHHHHHHHHHHHHHHBHBKPPPPPK.....",
        ".....KKKKKHHHHHHHHHHHHHHBHBBBKKKKKK.....",
        "....KKKKKHHHHHHHHHHHHHHBHBBBBBKKKKKK....",
        ".......KHHHHHHHHHHHHHHBHBBBBBBSK........",
        ".......HHHHHHHHHHHHHHBHBBBBBBSBS........",
        "......KHHHHHHHHHHHHHBHBBBBBBSBSSK.......",
        "......KHHHHHHHHHHHHBHBBBBBBSBSSSK.......",
        "......HHHHHHHHHHHHBHBBBBBBSBSSSSS.......",
        ".....KHHHHHHHHHHHBHBBBBBBSBSSSSSSK......",
        ".....RRHHHHKKKKKBHBBBBBBKKKKKSSSSRR.....",
        ".....KHHHHHHHHHBHBBBBBBSBSSSSSSSSK......",
        "....RRHHHHHHHHBHBBBBBBSBSSSSSSSSSKRR....",
        "......HHHHHHHBHBBBBBBSBSSSSSSSSSS.......",
        ".....RRHHHHHBHBBBBBBSSSSSSSSSSSSKRR.....",
        "......KHHHHBHBBBBBBPPSSSSSSSSSSSK.......",
        ".......HHHBHBBBBSBSSSSSSSSSSSSSS........",
        ".......KHBHBBBBSBSKSSKSSSSSSSSSK........",
        "........KBBBBBSBSSSKKSSSSSSSSSK.........",
        ".........KBBBSBKKKKKKKKKSSSSSK..........",
        "..........KBSKKBBBBBBBBBKKSSK...........",
        "...........KKKBBSSSSSSSBBKKK............",
        "............KBBSSSSSSSSSBBK.............",
        "...........KKBBSSSSSSSSSBBKK............",
        "............KBBBSSSSSSSBBBK.............",
        "............KKBBBBBBBBBBBKK.............",
        ".............KKBBBBBBBBBKK..............",
        "...............KKKKKKKKK................",
        "...................K....................",
        "........................................",
    )

    private val HAPPY = arrayOf(
        "........................................",
        "........................................",
        "........................................",
        "..........K...................K.........",
        ".........KK..................KK.........",
        "........KKPK................KKPK........",
        "........KPPK................KPPK........",
        ".......KPPPPK..............KPPPPK.......",
        ".......KPPPPK..KKKKHKKKK...KPPPPK.......",
        "......KPPPPPPKHHHHHHHHHHHKKPPPPPPK......",
        "......KPPPPKHHHHHHHHHHHHHHBKPPPPPK......",
        ".....KPPPPKHHHHHHHHHHHHHHBHBKPPPPPK.....",
        ".....KKKKKHHHHHHHHHHHHHHBHBBBKKKKKK.....",
        "....KKKKKHHHHHHHHHHHHHHBHBBBBBKKKKKK....",
        ".......KHHHHHHHHHHHHHHBHBBBBBBSK........",
        ".......HHHHHHHHHHHHHHBHBBBBBBSBS........",
        "......KHHHHHHHHHHHHHBHBBBBBBSBSSK.......",
        "......KHHHHHHHHHHHHBHBBBBBBSBSSSK.......",
        "......HHHHHHHHHHHHBHBBBBBBSBSSSSS.......",
        ".....KHHHHHHHHHHHBHBBBBBBSBSSSSSSK......",
        ".....RRHHHHHHKHHBHBBBBBBSBKSSSSSSRR.....",
        ".....KHHHHHHKHKBHBBBBBBSBKSKSSSSSK......",
        "....RRHHHHHHHHBHBBBBBBSBSSSSSSSSSKRR....",
        "......HHHHHHHBHBBBBBBSBSSSSSSSSSS.......",
        ".....RRHHHHHBHBBBBBBSSSSSSSSSSSSKRR.....",
        "......KHHHHBHBBBBBBPPSSSSSSSSSSSK.......",
        ".......HHHBHBBBBSBSSSSSSSSSSSSSS........",
        ".......KHBHBBBBSBSKSSKSSSSSSSSSK........",
        "........KBBBBBSBSSSKKSSSSSSSSSK.........",
        ".........KBBBSBKKKKKKKKKSSSSSK..........",
        "..........KBSKKBBBBBBBBBKKSSK...........",
        "...........KKKBBSSSSSSSBBKKK............",
        "............KBBSSSSSSSSSBBK.............",
        "...........KKBBSSSSSSSSSBBKK............",
        "............KBBBSSSSSSSBBBK.............",
        "............KKBBBBBBBBBBBKK.............",
        ".............KKBBBBBBBBBKK..............",
        "...............KKKKKKKKK................",
        "...................K....................",
        "........................................",
    )

    private val HUNGRY = arrayOf(
        "........................................",
        "........................................",
        "........................................",
        "..........K...................K.........",
        ".........KK..................KK.........",
        "........KKPK................KKPK........",
        "........KPPK................KPPK........",
        ".......KPPPPK..............KPPPPK.......",
        ".......KPPPPK..KKKKHKKKK...KPPPPK.......",
        "......KPPPPPPKHHHHHHHHHHHKKPPPPPPK......",
        "......KPPPPKHHHHHHHHHHHHHHBKPPPPPK......",
        ".....KPPPPKHHHHHHHHHHHHHHBHBKPPPPPK.....",
        ".....KKKKKHHHHHHHHHHHHHHBHBBBKKKKKK.....",
        "....KKKKKHHHHHHHHHHHHHHBHBBBBBKKKKKK....",
        ".......KHHHHHHHHHHHHHHBHBBBBBBSK........",
        ".......HHHHHHHHHHHHHHBHBBBBBBSBS........",
        "......KHHHHHHHHHHHHHBHBBBBBBSBSSK.......",
        "......KHHHHHHHHHHHHBHBBBBBBSBSSSK.......",
        "......HHHHHHHHHHHHBHBBBBBBSBSSSSS.......",
        ".....KHHHHHKKKKKHBHBBBBBKKKKKSSSSK......",
        ".....RRHHHKYYYYKBHBBBBBKYYYYKSSSSRR.....",
        ".....KHHHHKYYYYKHBBBBBBKYYYYKSSSSK......",
        "....RRHHHHHKYYKHBBBBBBSBKYYKSSSSSKRR....",
        "......HHHHHHHBHBBBBBBSBSSSSSSSSSS.......",
        ".....RRHHHHHBHBBBBBBSSSSSSSSSSSSKRR.....",
        "......KHHHHBHBBBBBBPPSSSSSSSSSSSK.......",
        ".......HHHBHBBBBSBKKKSSSSSSSSSSS........",
        ".......KHBHBBBBSBSKKKSSSSSSSSSSK........",
        "........KBBBBBSBSSKKKSSSSSSSSSK.........",
        ".........KBBBSBKKKKKKKKKSSSSSK..........",
        "..........KBSKKBBBBBBBBBKKSSK...........",
        "...........KKKBBSSSSSSSBBKKK............",
        "............KBBSSSSSSSSSBBK.............",
        "...........KKBBSSSSSSSSSBBKK............",
        "............KBBBSSSSSSSBBBK.............",
        "............KKBBBBBBBBBBBKK.............",
        ".............KKBBBBBBBBBKK..............",
        "...............KKKKKKKKK................",
        "...................K....................",
        "........................................",
    )

    private val SLEEP = arrayOf(
        "........................................",
        "........................................",
        "....................................ZZZ.",
        "..........K...................K......Z..",
        ".........KK..................KK.....ZZZ.",
        "........KKPK................KKPK........",
        "........KPPK................KPPK........",
        ".......KPPPPK..............KPPPPK.......",
        ".......KPPPPK..KKKKHKKKK...KPPPPK.......",
        "......KPPPPPPKHHHHHHHHHHHKKPPPPPPK......",
        "......KPPPPKHHHHHHHHHHHHHHBKPPPPPK......",
        ".....KPPPPKHHHHHHHHHHHHHHBHBKPPPPPK.....",
        ".....KKKKKHHHHHHHHHHHHHHBHBBBKKKKKK.....",
        "....KKKKKHHHHHHHHHHHHHHBHBBBBBKKKKKK....",
        ".......KHHHHHHHHHHHHHHBHBBBBBBSK........",
        ".......HHHHHHHHHHHHHHBHBBBBBBSBS........",
        "......KHHHHHHHHHHHHHBHBBBBBBSBSSK.......",
        "......KHHHHHHHHHHHHBHBBBBBBSBSSSK.......",
        "......HHHHHHHHHHHHBHBBBBBBSBSSSSS.......",
        ".....KHHHHHHHHHHHBHBBBBBBSBSSSSSSK......",
        ".....RRHHHHKKKKKBHBBBBBBKKKKKSSSSRR.....",
        ".....KHHHHHHHHHBHBBBBBBSBSSSSSSSSK......",
        "....RRHHHHHHHHBHBBBBBBSBSSSSSSSSSKRR....",
        "......HHHHHHHBHBBBBBBSBSSSSSSSSSS.......",
        ".....RRHHHHHBHBBBBBBSSSSSSSSSSSSKRR.....",
        "......KHHHHBHBBBBBBPPSSSSSSSSSSSK.......",
        ".......HHHBHBBBBSBSSSSSSSSSSSSSS........",
        ".......KHBHBBBBSBSKSSKSSSSSSSSSK........",
        "........KBBBBBSBSSSKKSSSSSSSSSK.........",
        ".........KBBBSBKKKKKKKKKSSSSSK..........",
        "..........KBSKKBBBBBBBBBKKSSK...........",
        "...........KKKBBSSSSSSSBBKKK............",
        "............KBBSSSSSSSSSBBK.............",
        "...........KKBBSSSSSSSSSBBKK............",
        "............KBBBSSSSSSSBBBK.............",
        "............KKBBBBBBBBBBBKK.............",
        ".............KKBBBBBBBBBKK..............",
        "...............KKKKKKKKK................",
        "...................K....................",
        "........................................",
    )

    /** Carácter → color ARGB. '.' es transparente. */
    private val PALETA: Map<Char, Int> = mapOf(
        '.' to Color.TRANSPARENT,
        'K' to Color.rgb(8, 7, 12),        // outline / boca (casi negro)
        'B' to Color.rgb(24, 22, 32),      // cuerpo negro (base)
        'H' to Color.rgb(56, 52, 70),      // highlight (redondez)
        'S' to Color.rgb(10, 9, 15),       // sombra profunda
        'Y' to Color.rgb(255, 222, 74),    // ojos amarillo brillante
        'W' to Color.rgb(255, 255, 255),   // brillo del ojo
        'N' to Color.rgb(20, 18, 28),      // pupila
        'P' to Color.rgb(240, 138, 156),   // rosa (nariz / interior oreja)
        'R' to Color.rgb(170, 168, 182),   // bigotes
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
