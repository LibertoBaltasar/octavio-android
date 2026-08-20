#!/usr/bin/env python3
"""Preview a resolución real del Nothing 2a (1080x2412) usando el sprite v3.

Escala el sprite idle con nearest-neighbor y lo centra grande (~62% del lado
menor) para mostrar cómo se vería en la pantalla de bloqueo.
"""

from PIL import Image, ImageDraw

src = "/home/liberto/Downloads/octavio_v3_idle0.png"

W, H = 1080, 2412
BG = (18, 18, 30, 255)

img = Image.new("RGBA", (W, H), BG)
draw = ImageDraw.Draw(img)

sprite_src = Image.open(src).convert("RGBA")
target = int(min(W, H) * 0.62)                       # ~62% del lado menor
sprite = sprite_src.resize((target, target), Image.Resampling.NEAREST)

left = (W - sprite.width) // 2
top = (H - sprite.height) // 2
img.paste(sprite, (left, top), sprite)

out = "/home/liberto/Downloads/octavio_preview.png"
img.save(out)
print(f"Preview guardado en {out} -> {img.size}")
