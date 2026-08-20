#!/usr/bin/env python3
"""Sprite sheet de Octavio — gato negro pixel art con ojos amarillos (estilo Botamon)."""
from PIL import Image

PAL = {
    '.': (0, 0, 0, 0),
    'K': (6, 6, 10, 255),      # outline casi negro
    'B': (30, 28, 38, 255),    # cuerpo negro (legible sobre el outline)
    'D': (18, 16, 24, 255),    # sombra cuerpo
    'Y': (255, 214, 64, 255),  # ojos amarillo brillante
    'y': (210, 160, 20, 255),  # amarillo oscuro (sombra ojo)
    'W': (255, 255, 255, 255), # brillo ojo
    'P': (240, 140, 155, 255), # rosa nariz/orejas
    'Z': (150, 200, 235, 255), # Z del sueño
}

def parse(rows):
    w = len(rows[0])
    for r in rows:
        assert len(r) == w, f"Fila de ancho {len(r)} != {w}: {r}"
    return [list(r) for r in rows]

def render(grid, scale):
    h, w = len(grid), len(grid[0])
    img = Image.new("RGBA", (w * scale, h * scale), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(grid):
        for x, c in enumerate(row):
            col = PAL[c]
            for dy in range(scale):
                for dx in range(scale):
                    px[x * scale + dx, y * scale + dy] = col
    return img

IDLE = parse([
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
])

BLINK = parse([
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
])

HAPPY = parse([
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
])

HUNGRY = parse([
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
])

SLEEP = parse([
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
])

FRAMES = [IDLE, BLINK, HAPPY, HUNGRY, SLEEP]
NAMES = ["idle0", "idle1", "happy", "hungry", "sleep"]

SCALE = 12
CELL = 16 * SCALE
BG = (16, 16, 24, 255)

# Sprite sheet
sheet = Image.new("RGBA", (CELL * len(FRAMES), CELL), BG)
for i, grid in enumerate(FRAMES):
    sheet.paste(render(grid, SCALE), (i * CELL, 0))
sheet.save("/home/liberto/Downloads/octavio_sprites.png")

# Frames individuales (con fondo, para ver bien)
for name, grid in zip(NAMES, FRAMES):
    img = Image.new("RGBA", (CELL, CELL), BG)
    img.paste(render(grid, SCALE), (0, 0))
    img.save(f"/home/liberto/Downloads/octavio_{name}.png")

# GIF de animación idle (parpadeo): idle0 -> idle1 -> idle0
gif_frames = []
for name in ["idle0", "idle1", "idle0"]:
    gif_frames.append(Image.open(f"/home/liberto/Downloads/octavio_{name}.png").convert("RGB"))
gif_frames[0].save("/home/liberto/Downloads/octavio_idle.gif", save_all=True,
                   append_images=gif_frames[1:], duration=600, loop=0)

print(f"OK: {len(FRAMES)} frames, celda {CELL}px, sheet {sheet.size}")
print("Guardado en /home/liberto/Downloads/: sprites.png + frames + idle.gif")
