"""Regenerate every launcher icon from tools/icon_master.png.

Requires Pillow:  pip install pillow
Run from the repository root:  python3 tools/make_icons.py
"""
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "tools" / "icon_master.png"
RES = ROOT / "app" / "src" / "main" / "res"

LEGACY = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
FOREGROUND = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}


def rounded(img: Image.Image, radius_ratio: float = 0.22) -> Image.Image:
    w, h = img.size
    mask = Image.new("L", (w * 4, h * 4), 0)
    ImageDraw.Draw(mask).rounded_rectangle(
        [0, 0, w * 4 - 1, h * 4 - 1], radius=int(w * radius_ratio * 4), fill=255
    )
    out = img.copy()
    out.putalpha(mask.resize((w, h), Image.LANCZOS))
    return out


def main() -> None:
    src = Image.open(SRC).convert("RGBA")

    for dpi, size in LEGACY.items():
        folder = RES / f"mipmap-{dpi}"
        folder.mkdir(parents=True, exist_ok=True)
        icon = src.resize((size, size), Image.LANCZOS)
        icon.save(folder / "ic_launcher.png")
        rounded(icon).save(folder / "ic_launcher_round.png")
        src.resize((FOREGROUND[dpi], FOREGROUND[dpi]), Image.LANCZOS).save(
            folder / "ic_launcher_foreground.png"
        )

    (ROOT / "docs").mkdir(exist_ok=True)
    src.resize((512, 512), Image.LANCZOS).save(ROOT / "docs" / "icon-512.png")
    print("launcher icons regenerated")


if __name__ == "__main__":
    main()
