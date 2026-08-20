#!/usr/bin/env python3
"""Generador de sprites de Octavio v3.1 (40x40) — versión final.

Gato negro pixel art, cabezón y redondo, con:
  - Boca en NEGRO (mismo tono oscuro del fondo/outline), sutil e integrada.
  - Nariz = un punto rosa.
  - Orejas simétricas y naturales (sin la sombra rara que había antes).
  - Sombreado con degradado + dithering → sensación de esfera.
  - 5 estados: idle, parpadeo, contento, hambre, dormido.

Genera:
  1. PNG del sprite sheet + frames individuales + GIF de parpadeo.
  2. `PetSprites.kt` completo (grids + paleta + render) listo para compilar.
"""

from PIL import Image

W, H = 40, 40

PAL = {
    '.': (0, 0, 0, 0),          # transparente
    'K': (8, 7, 12, 255),       # outline / boca (casi negro)
    'B': (24, 22, 32, 255),     # cuerpo negro (base)
    'H': (56, 52, 70, 255),     # highlight (luz arriba-izquierda)
    'S': (10, 9, 15, 255),      # sombra profunda
    'Y': (255, 222, 74, 255),   # ojos amarillo brillante
    'W': (255, 255, 255, 255),  # brillo del ojo
    'N': (20, 18, 28, 255),     # pupila (casi negro)
    'P': (240, 138, 156, 255),  # rosa (nariz / interior oreja)
    'R': (170, 168, 182, 255),  # bigotes (gris claro)
    'Z': (150, 200, 235, 255),  # "Z" del sueño
}

def new_grid():
    return [['.'] * W for _ in range(H)]

def put(g, x, y, c):
    x, y = int(x + 0.5), int(y + 0.5)
    if 0 <= x < W and 0 <= y < H:
        g[y][x] = c

def fill_ellipse(g, cx, cy, rx, ry, color):
    for y in range(H):
        for x in range(W):
            d = ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2
            if d <= 1.0:
                g[y][x] = color

def fill_triangle(g, ax, ay, bl, br, by, color):
    """Triángulo con vértice (ax, ay) y base de (bl, by) a (br, by)."""
    for y in range(int(ay + 0.5), int(by + 0.5) + 1):
        t = (y - ay) / (by - ay)
        half = (br - bl) / 2.0 * t
        cx = (bl + br) / 2.0
        for x in range(int(cx - half + 0.5), int(cx + half + 0.5) + 1):
            put(g, x, y, color)

# ---------------------------------------------------------------------------
# Geometría
# ---------------------------------------------------------------------------
HEAD_CX, HEAD_CY = 19.5, 21.0
HEAD_RX, HEAD_RY = 13.5, 12.5

def sombreado(nx, ny, x, y):
    """Color de sombreado (luz arriba-izquierda) con dithering en transiciones."""
    dot = nx * (-0.7071) + ny * (-0.7071)
    checker = (x + y) % 2
    if dot >= 0.25:
        return 'H'
    elif dot >= 0.10:
        return 'H' if checker else 'B'
    elif dot >= -0.15:
        return 'B'
    elif dot >= -0.30:
        return 'B' if checker else 'S'
    else:
        return 'S'

def orejas(g):
    """Orejas simétricas: triángulo oscuro + triángulo rosa interior limpio."""
    # Izquierda
    fill_triangle(g, 9.0, 3.0, 4.0, 15.0, 13.0, 'K')
    fill_triangle(g, 9.0, 5.0, 6.0, 13.0, 11.0, 'P')
    # Derecha (espejo de la izquierda respecto a x=19.5)
    fill_triangle(g, 30.0, 3.0, 24.0, 35.0, 13.0, 'K')
    fill_triangle(g, 30.0, 5.0, 26.0, 33.0, 11.0, 'P')

def bigotes(g):
    # 3 por lado, saliendo de las mejillas
    for (mx, my) in [(6, 20), (5, 22), (6, 24)]:
        put(g, mx, my, 'R'); put(g, mx - 1, my, 'R')
    for (mx, my) in [(33, 20), (34, 22), (33, 24)]:
        put(g, mx, my, 'R'); put(g, mx + 1, my, 'R')

def pecho(g):
    fill_ellipse(g, 19.5, 33.5, 8.0, 5.0, 'K')
    fill_ellipse(g, 19.5, 33.5, 6.8, 3.8, 'B')
    fill_ellipse(g, 19.5, 33.0, 5.0, 2.2, 'S')

def dibujar_base(g):
    orejas(g)
    # Cabeza: anillo de outline + relleno sombreado
    fill_ellipse(g, HEAD_CX, HEAD_CY, HEAD_RX + 0.7, HEAD_RY + 0.7, 'K')
    for y in range(H):
        for x in range(W):
            nx = (x + 0.5 - HEAD_CX) / HEAD_RX
            ny = (y + 0.5 - HEAD_CY) / HEAD_RY
            if nx * nx + ny * ny <= 1.0:
                g[y][x] = sombreado(nx, ny, x, y)
    bigotes(g)
    pecho(g)

# ---------------------------------------------------------------------------
# Rasgos faciales
# ---------------------------------------------------------------------------
def nariz(g):
    """Punto rosa (2x2), tal como pide Liberto."""
    put(g, 19, 25, 'P'); put(g, 20, 25, 'P')

def boca(g):
    """Boca omega sutil en NEGRO (mismo tono del outline), integrada en el pelaje."""
    put(g, 18, 27, 'K'); put(g, 21, 27, 'K')
    put(g, 19, 28, 'K'); put(g, 20, 28, 'K')

def boca_hambre(g):
    """Boca abierta en negro (más grande), para el estado de hambre."""
    fill_ellipse(g, 19.5, 27.5, 1.9, 1.5, 'K')

def ojos_abiertos(g):
    for cx in (13.0, 26.0):
        fill_ellipse(g, cx, 20.0, 3.4, 4.0, 'K')
        fill_ellipse(g, cx, 20.0, 2.7, 3.3, 'Y')
        fill_ellipse(g, cx, 20.0, 1.3, 2.2, 'N')
        put(g, cx - 1, 19, 'W')
        put(g, cx, 18, 'W')

def ojos_cerrados(g):
    for cx in (13.0, 26.0):
        for dx in (-2, -1, 0, 1, 2):
            put(g, cx + dx, 20, 'K')

def ojos_felices(g):
    # "^^" — carets: pico arriba, patas hacia fuera-abajo
    put(g, 13, 20, 'K')                     # pico ojo izq
    put(g, 12, 21, 'K'); put(g, 14, 21, 'K')
    put(g, 26, 20, 'K')                     # pico ojo der
    put(g, 25, 21, 'K'); put(g, 27, 21, 'K')

def ojos_tristes(g):
    # Entrecerrados: amarillo más pequeño y bajo + párpado oscuro encima
    for cx in (13.0, 26.0):
        fill_ellipse(g, cx, 21.0, 2.7, 2.4, 'K')
        fill_ellipse(g, cx, 21.0, 2.0, 1.8, 'Y')
        for dx in (-2, -1, 0, 1, 2):
            put(g, cx + dx, 19, 'K')        # párpado

def zzz(g):
    # "Z" en la esquina superior derecha (x=36-38), FUERA de la oreja (que llega
    # hasta x~35) para que no se superponga con ella.
    put(g, 36, 2, 'Z'); put(g, 37, 2, 'Z'); put(g, 38, 2, 'Z')
    put(g, 37, 3, 'Z')
    put(g, 36, 4, 'Z'); put(g, 37, 4, 'Z'); put(g, 38, 4, 'Z')

# ---------------------------------------------------------------------------
# Construir frames
# ---------------------------------------------------------------------------
def build(ojos, boca_fn=boca, extra=None):
    g = new_grid()
    dibujar_base(g)
    ojos(g)
    nariz(g)
    boca_fn(g)
    if extra:
        extra(g)
    return g

FRAMES = {
    'IDLE_0': build(ojos_abiertos),
    'IDLE_1': build(ojos_cerrados),
    'HAPPY':  build(ojos_felices),
    'HUNGRY': build(ojos_tristes, boca_hambre),
    'SLEEP':  build(ojos_cerrados, boca, zzz),
}
ORDER = ['IDLE_0', 'IDLE_1', 'HAPPY', 'HUNGRY', 'SLEEP']

# ---------------------------------------------------------------------------
# Render PNGs
# ---------------------------------------------------------------------------
def render(grid, scale):
    img = Image.new("RGBA", (W * scale, H * scale), (0, 0, 0, 0))
    px = img.load()
    for y in range(H):
        for x in range(W):
            col = PAL[grid[y][x]]
            for dy in range(scale):
                for dx in range(scale):
                    px[x * scale + dx, y * scale + dy] = col
    return img

SCALE = 12
BG = (16, 16, 24, 255)

sheet = Image.new("RGBA", (W * SCALE * len(ORDER), H * SCALE), BG)
for i, name in enumerate(ORDER):
    sheet.paste(render(FRAMES[name], SCALE), (i * W * SCALE, 0))
sheet.save("/home/liberto/Downloads/octavio_sprites_v3.png")

lower = {'IDLE_0': 'idle0', 'IDLE_1': 'idle1', 'HAPPY': 'happy',
         'HUNGRY': 'hungry', 'SLEEP': 'sleep'}
for name in ORDER:
    img = Image.new("RGBA", (W * SCALE, H * SCALE), BG)
    img.paste(render(FRAMES[name], SCALE), (0, 0))
    img.save(f"/home/liberto/Downloads/octavio_v3_{lower[name]}.png")

gif = [Image.open(f"/home/liberto/Downloads/octavio_v3_{n}.png").convert("RGB")
       for n in ['idle0', 'idle1', 'idle0']]
gif[0].save("/home/liberto/Downloads/octavio_v3_idle.gif", save_all=True,
            append_images=gif[1:], duration=600, loop=0)

# ---------------------------------------------------------------------------
# Emitir PetSprites.kt completo
# ---------------------------------------------------------------------------
def kotlin_grid(name, grid):
    lines = [f"    private val {name} = arrayOf("]
    for row in grid:
        lines.append(f"        \"{''.join(row)}\",")
    lines.append("    )")
    return "\n".join(lines)

grids = "\n\n".join(kotlin_grid(n, FRAMES[n]) for n in ORDER)

kotlin = f'''package com.liberto.octavio.pet

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
object PetSprites {{

{grids}

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
    fun sprite(mood: PetMood, frame: Int): Array<String> = when (mood) {{
        PetMood.IDLE -> if (frame % 2 == 0) IDLE_0 else IDLE_1
        PetMood.HAPPY -> HAPPY
        PetMood.HUNGRY -> HUNGRY
        PetMood.SLEEP -> SLEEP
    }}

    /** Renderiza un sprite a un Bitmap 1:1 (un píxel lógico = un píxel real). */
    fun render(rows: Array<String>): Bitmap {{
        val h = rows.size
        val w = rows[0].length
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        for (y in rows.indices) {{
            val row = rows[y]
            for (x in row.indices) {{
                bmp.setPixel(x, y, PALETA[row[x]] ?: Color.TRANSPARENT)
            }}
        }}
        return bmp
    }}
}}
'''

out_kt = "/home/liberto/octavio-android/app/src/main/java/com/liberto/octavio/pet/PetSprites.kt"
with open(out_kt, "w") as f:
    f.write(kotlin)

print("OK: 5 frames a 40x40. Sheet, PNGs, GIF y PetSprites.kt generados.")
print("idle0 (primeras filas) para verificar simetría de orejas:")
for row in FRAMES['IDLE_0'][:8]:
    print("  " + "".join(row))
