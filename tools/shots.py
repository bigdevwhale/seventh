"""Turns the screen renders into the README screenshots.

    ./gradlew testDebugUnitTest -Prender --tests '*Screens*'
    python tools/shots.py app/build/art/screens
picks one screen of each kind across the three lands and writes docs/screenshots/<lang>/<n>-<name>.jpg at phone width.
"""
import sys
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
PICK = [("1-home", "rus-1-home"), ("2-event", "eu-2-event"), ("3-bag", "edo-3-bag"), ("4-result", "rus-4-result"),
        ("5-tree", "eu-5-tree"), ("6-intro", "edo-0-intro"), ("7-lands", "rus-lands"), ("8-settings", "rus-9-settings")]


def main(raw: str):
    for lang in ("en", "ru"):
        out = ROOT / f"docs/screenshots/{lang}"
        out.mkdir(parents=True, exist_ok=True)
        for name, src in PICK:
            land, rest = src.split("-", 1)
            f = Path(raw) / (f"{land}-{rest}.png" if lang == "ru" else f"{land}-en-{rest}.png")
            img = Image.open(f).convert("RGB")
            if img.width > 1080:
                img = img.resize((1080, round(img.height * 1080 / img.width)), Image.LANCZOS)
            img.save(out / f"{name}.jpg", quality=86, optimize=True)


if __name__ == "__main__":
    main(sys.argv[1])
