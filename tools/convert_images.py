"""Convert approved generated JPGs into app WebP drawables (master prompt §5).

Usage: python tools/convert_images.py <source_dir>
Writes app/src/main/res/drawable-nodpi/<key>.webp and prints size budgets.
"""
import sys
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "app/src/main/res/drawable-nodpi"

# key -> (source file prefix, width, height, budget KB)
MANIFEST = {
    "welcome_hero": ("welcome_hero_1791976803352", 1080, 1440, 250),
    "hero_challenge": ("hero_challenge_1791976841682", 720, 720, 150),
    "help_hero": ("help_hero_1791977155606", 600, 450, 150),
    "farewell_s1": ("farewell_s1_1791976938029", 1080, 720, 150),
    "farewell_s2a": ("farewell_s2a_1791977030257", 1080, 720, 150),
    "farewell_s2b": ("farewell_s2b_1791976961411", 1080, 720, 150),
    "farewell_s2r": ("farewell_s2r_1791977069833", 1080, 720, 150),
    "farewell_s2c": ("farewell_s2c_1791977091485", 1080, 720, 150),
    "farewell_s3": ("farewell_s3_1791977135119", 1080, 720, 150),
    "story_farewell_cover": ("story_farewell_cover_1791977186512", 720, 480, 150),
    "story_group_chat_cover": ("story_group_chat_cover_1791977238964", 720, 480, 150),
    "story_hostel_cover": ("story_hostel_cover_1791977259209", 720, 480, 150),
    "story_after_match_cover": ("story_after_match_cover_1791977282699", 720, 480, 150),
    "story_wedding_cover": ("story_wedding_cover_1791977330075", 720, 480, 150),
    "story_senior_cover": ("story_senior_cover_1791977351285", 720, 480, 150),
    "story_exam_cover": ("story_exam_cover_1791977370807", 720, 480, 150),
}


def cover_crop(img: Image.Image, w: int, h: int) -> Image.Image:
    sw, sh = img.size
    scale = max(w / sw, h / sh)
    img = img.resize((round(sw * scale), round(sh * scale)), Image.LANCZOS)
    left = (img.width - w) // 2
    top = (img.height - h) // 2
    return img.crop((left, top, left + w, top + h))


def main(src: Path) -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    failed = 0
    for key, (prefix, w, h, budget) in MANIFEST.items():
        f = src / f"{prefix}.jpg"
        if not f.exists():
            print(f"MISSING {key}: {f.name}")
            failed += 1
            continue
        img = cover_crop(Image.open(f).convert("RGB"), w, h)
        dest = OUT / f"{key}.webp"
        q = 80
        while True:
            img.save(dest, "WEBP", quality=q, method=6)
            kb = dest.stat().st_size / 1024
            if kb <= budget or q <= 50:
                break
            q -= 5
        status = "OK " if kb <= budget else "OVER"
        print(f"{status} {key:32s} {w}x{h} q={q} {kb:6.1f} KB (budget {budget})")
    return failed


if __name__ == "__main__":
    sys.exit(main(Path(sys.argv[1])))
