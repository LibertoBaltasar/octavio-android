#!/usr/bin/env python3
"""Genera un preview del live wallpaper a resolución real del Nothing 2a (1080x2412).

Carga el sprite idle ya generado por gen_sprites.py (octavio_v2_idle0.png) y lo
escala con nearest-neighbor para simular exactamente lo que verá el wallpaper.
"""

from PIL import Image, ImageDraw

W, H = 1080, 2412
BG = (18, 18, 30, 255)

img = Image.new("RGBA", (W, H), BG)
draw = ImageDraw.Draw(img)

# Cargar sprite idle (24x24 lógico, renderizado a 14x = 336px) y escalarlo
sprite_src = Image.open("/home/liberto/Downloads/octavio_v2_idle0.png").convert("RGBA")
target = int(min(W, H) * 0.48)                       # ~48% del lado menor
sprite = sprite_src.resize((target, target), Image.Resampling.NEAREST)

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
