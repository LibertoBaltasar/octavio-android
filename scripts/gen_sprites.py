#!/usr/bin/env python3
"""Generador de sprites de Octavio v2.

Gato negro pixel art, cabezón y redondo, con más detalle:
  - Cabeza circular (dibujada con elipse real, no un cuadrado)
  - Cuerpo oscuro casi negro con sombreado (luz arriba-izquierda)
  - Orejas triangulares con interior rosa
  - Bigotes claros
  - Ojos amarillos grandes y redondos con brillo
  - Cuerpecito y patas pequeñas (cabezón)

Este script es la FUENTE del arte. Genera:
  1. PNG del sprite sheet + frames individuales + GIF de parpadeo
  2. `sprites_kotlin.txt` con los grids en formato Kotlin listos para pegar
     en PetSprites.kt
"""

from PIL import Image
import math

W, H = 24, 24

# Paleta (R, G, B, A). Cuerpo muy oscuro, casi negro.
PAL = {
    '.': (0, 0, 0, 0),          # transparente
    'K': (3, 3, 7, 255),        # outline casi negro
    'B': (12, 11, 18, 255),     # cuerpo negro (más oscuro que antes)
    'H': (34, 31, 46, 255),     # highlight (sugiere redondez)
    'S': (7, 7, 12, 255),       # sombra profunda
    'Y': (255, 222, 74, 255),   # ojos amarillo brillante
    'y': (196, 150, 24, 255),   # amarillo oscuro (borde ojo)
    'W': (255, 255, 255, 255),  # brillo del ojo
    'P': (238, 132, 150, 255),  # rosa (orejas / nariz)
    'R': (150, 150, 165, 255),  # bigotes (gris claro)
    'Z': (150, 200, 235, 255),  # "Z" del sueño
}

def new_grid():
    return [['.'] * W for _ in range(H)]

def put(g, x, y, c):
    # int(x + 0.5) en vez de round(): round() usa "banker's rounding" y
    # redondea .5 hacia el par, lo que provoca asimetrías (oreja derecha
    # sin outline). Con +0.5 el redondeo es consistente y simétrico.
    x, y = int(x + 0.5), int(y + 0.5)
    if 0 <= x < W and 0 <= y < H:
        g[y][x] = c

def fill_ellipse(g, cx, cy, rx, ry, color):
    """Rellena una elipse (usando borde suavizado 1px)."""
    for y in range(H):
        for x in range(W):
            d = ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2
            if d <= 1.0:
                g[y][x] = color

def fill_circle(g, cx, cy, r, color):
    fill_ellipse(g, cx, cy, r, r, color)

def fill_triangle(g, ax, ay, bl, br, by, color):
    """Triángulo con vértice (ax, ay) y base de (bl, by) a (br, by)."""
    for y in range(int(ay + 0.5), int(by + 0.5) + 1):
        t = (y - ay) / (by - ay)          # 0 en vértice, 1 en base
        half = (br - bl) / 2.0 * t
        cx = (bl + br) / 2.0
        # +0.5 (no round) para que el ancho sea simétrico en ambos lados
        for x in range(int(cx - half + 0.5), int(cx + half + 0.5) + 1):
            put(g, x, y, color)

# ---------------------------------------------------------------------------
# Cabeza redonda + cuerpo base (común a todos los moods)
# ---------------------------------------------------------------------------
HEAD_CX, HEAD_CY, HEAD_R = 11.5, 12.5, 8.2

def dibujar_base(g):
    """Dibuja cabeza (outline + fill + sombreado), orejas, bigotes, cuerpo y patas."""
    # --- Cabeza: primero outline, luego relleno ---
    fill_circle(g, HEAD_CX, HEAD_CY, HEAD_R + 0.8, 'K')   # anillo de outline
    fill_circle(g, HEAD_CX, HEAD_CY, HEAD_R - 0.2, 'B')   # relleno cuerpo

    # --- Sombreado: luz arriba-izquierda, sombra abajo-derecha (redondez) ---
    for y in range(H):
        for x in range(W):
            dx = x - HEAD_CX
            dy = y - HEAD_CY
            if dx * dx + dy * dy <= (HEAD_R - 0.2) ** 2:
                if dx + dy < -3.0:
                    g[y][x] = 'H'     # highlight
                elif dx + dy > 3.0 and dy > 1.0:
                    g[y][x] = 'S'     # sombra profunda

    # --- Orejas (triángulos con interior rosa), por encima de la cabeza ---
    fill_triangle(g, 6.0, 1.0, 3.0, 9.0, 5.0, 'K')    # oreja izq (outline)
    fill_triangle(g, 6.0, 2.0, 4.2, 7.8, 4.5, 'P')    # interior rosa
    fill_triangle(g, 17.0, 1.0, 14.0, 20.0, 5.0, 'K') # oreja der (outline)
    fill_triangle(g, 17.0, 2.0, 15.2, 18.8, 4.5, 'P') # interior rosa

    # --- Bigotes (gris claro), saliendo de las mejillas ---
    for (mx, my) in [(3, 14), (2, 15), (3, 16), (20, 14), (21, 15), (20, 16)]:
        put(g, mx, my, 'R')

    # --- Cuerpecito y patas (cabezón = cuerpo pequeño) ---
    fill_ellipse(g, 11.5, 21.0, 5.0, 2.2, 'K')
    fill_ellipse(g, 11.5, 21.0, 4.0, 1.4, 'B')
    # patas
    for fx in (8, 11, 14):
        put(g, fx, 22, 'K')

# ---------------------------------------------------------------------------
# Ojos y boca según el mood
# ---------------------------------------------------------------------------
def ojos_abiertos(g):
    fill_ellipse(g, 8.0, 11.5, 2.1, 2.6, 'K')   # contorno
    fill_ellipse(g, 8.0, 11.5, 1.6, 2.1, 'Y')   # amarillo
    fill_ellipse(g, 15.0, 11.5, 2.1, 2.6, 'K')
    fill_ellipse(g, 15.0, 11.5, 1.6, 2.1, 'Y')
    put(g, 7, 10, 'W')   # brillo
    put(g, 14, 10, 'W')

def ojos_cerrados(g):
    for x in range(6, 10):
        put(g, x, 11, 'K')
    for x in range(13, 17):
        put(g, x, 11, 'K')

def ojos_felices(g):
    # Ojos felices en arco ^ ^ (cerrados y curvados hacia arriba), inconfundibles
    # frente al parpadeo (línea recta) y al idle (ojos redondos abiertos).
    for (x, y) in [(6, 12), (7, 11), (8, 11), (9, 12)]:
        put(g, x, y, 'K')                       # ojo izq ∧
    for (x, y) in [(14, 12), (15, 11), (16, 11), (17, 12)]:
        put(g, x, y, 'K')                       # ojo der ∧

def ojos_tristes(g):
    # Ojos entrecerrados (cansado/hambre): amarillo con párpado caído cubriendo
    # la mitad superior + comisura exterior caída. Distinto de "abierto" y "cerrado".
    for ex in (7, 14):
        for yy in (12, 13):
            put(g, ex, yy, 'Y'); put(g, ex + 1, yy, 'Y'); put(g, ex + 2, yy, 'Y')
        for xx in (ex, ex + 1, ex + 2):
            put(g, xx, 11, 'K')      # párpado
        put(g, ex + 2, 10, 'K')      # comisura exterior caída

def boca_normal(g):
    put(g, 11, 15, 'P')   # nariz
    put(g, 12, 15, 'P')
    put(g, 11, 17, 'P')   # boquita neutra (rosa: contraste sobre negro)
    put(g, 12, 17, 'P')

def boca_sonrisa(g):
    put(g, 11, 15, 'P')   # nariz
    put(g, 12, 15, 'P')
    # Sonrisa ancha y gruesa (2 filas) en ROSA: alta visibilidad sobre el pelaje negro.
    for x in (9, 14):
        put(g, x, 15, 'P')              # comisuras arriba
    for x in (9, 10, 11, 12, 13, 14):
        put(g, x, 16, 'P')              # línea ancha de la sonrisa
    for x in (10, 13):
        put(g, x, 17, 'P')              # borde inferior (sonrisa abierta)

def boca_hambre(g):
    put(g, 11, 15, 'P')   # nariz
    put(g, 12, 15, 'P')
    # Boca abierta ROSA (visible sobre el pelaje negro): el gato maúlla pidiendo comida.
    fill_ellipse(g, 11.5, 17.0, 2.4, 1.6, 'P')

def rubor(g):
    put(g, 6, 14, 'P')
    put(g, 17, 14, 'P')

def zzz(g):
    # Una sola "Z" limpia (3x3) en la esquina superior derecha, lejos de la
    # oreja y de la cabeza para que se lea claramente como sueño.
    # Diagonal en el CENTRO (x=21) para que sea una "Z" canónica, no una "C".
    put(g, 20, 2, 'Z'); put(g, 21, 2, 'Z'); put(g, 22, 2, 'Z')
    put(g, 21, 3, 'Z')
    put(g, 20, 4, 'Z'); put(g, 21, 4, 'Z'); put(g, 22, 4, 'Z')

# ---------------------------------------------------------------------------
# Construir cada frame
# ---------------------------------------------------------------------------
def build(ojos, boca, extra=None):
    g = new_grid()
    dibujar_base(g)
    ojos(g)
    boca(g)
    if extra:
        extra(g)
    return g

FRAMES = {
    'idle0':  build(ojos_abiertos, boca_normal),
    'idle1':  build(ojos_cerrados, boca_normal),     # parpadeo
    'happy':  build(ojos_felices, boca_sonrisa, rubor),   # ojos ^^ + sonrisa ancha + rubor
    'hungry': build(ojos_tristes, boca_hambre),      # entrecerrado + boca abierta
    'sleep':  build(ojos_cerrados, boca_normal, zzz),
}

ORDER = ['idle0', 'idle1', 'happy', 'hungry', 'sleep']

# ---------------------------------------------------------------------------
# Render a PNG
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

SCALE = 14
BG = (16, 16, 24, 255)

sheet = Image.new("RGBA", (W * SCALE * len(ORDER), H * SCALE), BG)
for i, name in enumerate(ORDER):
    sheet.paste(render(FRAMES[name], SCALE), (i * W * SCALE, 0))
sheet.save("/home/liberto/Downloads/octavio_sprites_v2.png")

for name in ORDER:
    img = Image.new("RGBA", (W * SCALE, H * SCALE), BG)
    img.paste(render(FRAMES[name], SCALE), (0, 0))
    img.save(f"/home/liberto/Downloads/octavio_v2_{name}.png")

# GIF parpadeo
gif = [Image.open(f"/home/liberto/Downloads/octavio_v2_{n}.png").convert("RGB")
       for n in ['idle0', 'idle1', 'idle0']]
gif[0].save("/home/liberto/Downloads/octavio_v2_idle.gif", save_all=True,
            append_images=gif[1:], duration=600, loop=0)

# ---------------------------------------------------------------------------
# Emitir grids en formato Kotlin (para PetSprites.kt)
# ---------------------------------------------------------------------------
def kotlin_grid(name, grid):
    lines = [f"    private val {name.upper()} = arrayOf("]
    for row in grid:
        lines.append(f"        \"{''.join(row)}\",")
    lines.append("    )")
    return "\n".join(lines)

with open("/home/liberto/octavio-android/scripts/sprites_kotlin.txt", "w") as f:
    for name in ORDER:
        f.write(kotlin_grid(name, FRAMES[name]) + "\n\n")

print(f"OK: {len(ORDER)} frames a {W}x{H}. Sheet, PNGs y GIF en ~/Downloads/.")
print("Kotlin listo en scripts/sprites_kotlin.txt")
