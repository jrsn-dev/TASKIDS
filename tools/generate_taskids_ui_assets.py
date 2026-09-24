from PIL import Image, ImageDraw, ImageFont, ImageFilter
from pathlib import Path
import os

OUT = Path("app/src/main/assets/ui")
OUT.mkdir(parents=True, exist_ok=True)

emoji_font = "/usr/share/fonts/truetype/noto/NotoColorEmoji.ttf"
font_candidates = [
    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf",
]
font_path = next(p for p in font_candidates if os.path.exists(p))

def save_logo():
    W, H = 800, 220
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    font = ImageFont.truetype(font_path, 125)
    letters = "TASKIDS"
    colors = ["#2F6BFF", "#FFBE22", "#25C66F", "#2F9AF8", "#FF4F93", "#7C3FF2", "#7C3FF2"]
    widths = [draw.textlength(ch, font=font) for ch in letters]
    spacing = 4
    x = (W - (sum(widths) + spacing * (len(letters) - 1))) / 2
    y = 38
    for i, ch in enumerate(letters):
        draw.text((x + 2, y + 5), ch, font=font, fill=(28, 58, 115, 55))
        draw.text((x, y), ch, font=font, fill=colors[i], stroke_width=7, stroke_fill="white")
        x += widths[i] + spacing
    img.save(OUT / "taskids_logo.png", optimize=True, compress_level=9)

ICONS = {
    "mission_book": "📚",
    "mission_bath": "🛁",
    "mission_meal": "🍛",
    "mission_homework": "📝",
    "mission_tooth": "🦷",
    "mission_toys": "🧸",
    "mission_sport": "⚽",
    "reward_game": "🎮",
    "reward_movie": "🍿",
    "library_coloring": "🖍️",
    "library_education": "🧩",
    "library_memory": "🧠",
    "library_words": "🔎",
    "star_mascot": "⭐",
    "gift": "🎁",
    "crown": "👑",
}

BACKGROUNDS = {
    "mission_book": ("#FFF2C7", "#FFE29A"),
    "mission_bath": ("#DFF6FF", "#BDEBFF"),
    "mission_meal": ("#FFF0C7", "#FFE0A0"),
    "mission_homework": ("#F0DEFF", "#DCC0FF"),
    "mission_tooth": ("#DFF7FF", "#C3EEF4"),
    "mission_toys": ("#FFE4F0", "#FFCBE2"),
    "mission_sport": ("#E1F7DF", "#C6F0C3"),
    "reward_game": ("#FFC3DD", "#FF8FCA"),
    "reward_movie": ("#FFE7B7", "#FFD071"),
    "library_coloring": ("#FFF0C9", "#FFE19A"),
    "library_education": ("#CFF4FF", "#A9E9FF"),
    "library_memory": ("#E3D8FF", "#C8B4FF"),
    "library_words": ("#FFEBD3", "#FFD2A2"),
    "star_mascot": ("#FFF5BE", "#FFE16A"),
    "gift": ("#E6DBFF", "#CFBCFF"),
    "crown": ("#FFF3C4", "#FFD75A"),
}

def render_icon(name, emoji):
    size = 256
    image = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    blob = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(blob)
    c1, c2 = BACKGROUNDS[name]
    draw.rounded_rectangle((17, 17, 239, 239), radius=60, fill=c1)
    draw.ellipse((43, 35, 210, 202), fill=c2)
    blob = blob.filter(ImageFilter.GaussianBlur(0.7))
    image.alpha_composite(blob)

    d = ImageDraw.Draw(image)
    try:
        font = ImageFont.truetype(emoji_font, 109)
        bbox = d.textbbox((0, 0), emoji, font=font, embedded_color=True)
        w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
        temp = Image.new("RGBA", (max(w + 16, 1), max(h + 16, 1)), (0, 0, 0, 0))
        td = ImageDraw.Draw(temp)
        td.text((8 - bbox[0], 8 - bbox[1]), emoji, font=font, embedded_color=True)
        scale = min(150 / temp.width, 150 / temp.height)
        temp = temp.resize(
            (max(1, int(temp.width * scale)), max(1, int(temp.height * scale))),
            Image.Resampling.LANCZOS,
        )
        image.alpha_composite(temp, ((size - temp.width) // 2, (size - temp.height) // 2))
    except Exception:
        fallback = ImageFont.truetype(font_path, 80)
        d.text((size // 2, size // 2), "★", font=fallback, anchor="mm", fill="#FFBE22")

    image.save(OUT / f"{name}.png", optimize=True, compress_level=9)

save_logo()
for name, emoji in ICONS.items():
    render_icon(name, emoji)

print("Generated:")
for path in sorted(OUT.glob("*.png")):
    print(path, path.stat().st_size)
