#!/usr/bin/env python3
"""Genera un preview del live wallpaper a resolución real del Nothing 2a (1080x2412)."""
from PIL import Image, ImageDraw

PAL = {
    '.': (0, 0, 0, 0),
    'K': (6, 6, 10, 255),
    'B': (30, 28, 38, 255),
    'D': (18, 16, 24, 255),
    'Y': (255, 214, 64, 255),
    'y': (210, 160, 20, 255),
    'W': (255, 255, 255, 255),
    'P': (240, 140, 155, 255),
    'Z': (150, 200, 235, 255),
}

IDLE_0 = [
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
]

def render_sprite(rows, scale):
    h, w = len(rows), len(rows[0])
    img = Image.new("RGBA", (w * scale, h * scale), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        for x, c in enumerate(row):
            col = PAL[c]
            for dy in range(scale):
                for dx in range(scale):
                    px[x * scale + dx, y * scale + dy] = col
    return img

W, H = 1080, 2412
img = Image.new("RGBA", (W, H), (18, 18, 30, 255))
draw = ImageDraw.Draw(img)

# Bicho centrado (~40% del lado menor = 1080*0.4)
target = int(min(W, H) * 0.4)
sprite = render_sprite(IDLE_0, target // 16)  # 16 px lógicos
left = (W - sprite.width) // 2
top = (H - sprite.height) // 2
img.paste(sprite, (left, top), sprite)

# Barras de estado (hambre y energía) debajo del bicho
base_y = top + sprite.height + 24
bar_w = int(W * 0.28)
bar_h = 20
x0 = (W - bar_w) // 2
draw.rounded_rectangle([x0, base_y, x0 + bar_w, base_y + bar_h], 8, fill=(40, 40, 56, 255))
draw.rounded_rectangle([x0, base_y, x0 + int(bar_w * 0.9), base_y + bar_h], 8, fill=(255, 190, 60, 255))
y1 = base_y + bar_h + 12
draw.rounded_rectangle([x0, y1, x0 + bar_w, y1 + bar_h], 8, fill=(40, 40, 56, 255))
draw.rounded_rectangle([x0, y1, x0 + int(bar_w * 0.7), y1 + bar_h], 8, fill=(120, 200, 250, 255))

out = "/home/liberto/Downloads/octavio_preview.png"
img.save(out)
print("Preview guardado en", out, "->", img.size)
