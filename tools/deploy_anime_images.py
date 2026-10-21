import os
from PIL import Image

brain_dir = r"C:\Users\mailh\.gemini\antigravity-ide\brain\8f5591cc-2428-45a9-a6c4-d2e999b0af35"
dest_dir = r"d:\AI_Personal\AI\DFS\app\src\main\res\drawable-nodpi"

mappings = {
    "story_tcnn_cover_1792486933907.jpg": ["story_tcnn_cover.webp"],
    "char_arjun_1792486959966.jpg": ["char_arjun.webp"],
    "story_tcnn_s01_1792486982245.jpg": ["story_tcnn_s01.webp", "story_tcnn_s11.webp"],
    "story_tcnn_s02_1792487004730.jpg": ["story_tcnn_s02.webp", "story_tcnn_s08.webp"],
    "story_tcnn_s04_1792487030173.jpg": ["story_tcnn_s04.webp", "story_tcnn_s06.webp", "story_tcnn_e01.webp"],
    "story_tcnn_s05_1792488111501.jpg": ["story_tcnn_s05.webp", "story_tcnn_s13.webp"],
    "story_tcnn_s07_1792488237007.jpg": ["story_tcnn_s07.webp", "story_tcnn_s03.webp", "story_tcnn_s09.webp", "story_tcnn_s10.webp", "story_tcnn_s12.webp", "story_tcnn_s14.webp", "story_tcnn_e02.webp", "story_tcnn_e03.webp"],
    "story_hostel_cover_1792487159911.jpg": ["story_hostel_cover.webp"],
    "story_farewell_cover_1792487196012.jpg": ["story_farewell_cover.webp", "farewell_s1.webp"],
    "farewell_s2r_1792487223587.jpg": ["farewell_s2r.webp", "farewell_s2b.webp"],
    "farewell_s3_1792487262808.jpg": ["farewell_s3.webp", "farewell_s2a.webp", "farewell_s2c.webp"],
    "story_after_match_cover_1792487421037.jpg": ["story_after_match_cover.webp"],
    "story_exam_cover_1792487459744.jpg": ["story_exam_cover.webp"],
    "story_group_chat_cover_1792487578304.jpg": ["story_group_chat_cover.webp"],
    "story_wedding_cover_1792487636494.jpg": ["story_wedding_cover.webp"],
    "story_senior_cover_1792487687625.jpg": ["story_senior_cover.webp"],
}

for src_name, targets in mappings.items():
    src_path = os.path.join(brain_dir, src_name)
    if not os.path.exists(src_path):
        print(f"Warning: {src_path} not found!")
        continue
    with Image.open(src_path) as im:
        rgb = im.convert("RGB")
        for target in targets:
            out_path = os.path.join(dest_dir, target)
            rgb.save(out_path, "WEBP", quality=90)
            print(f"Converted & saved -> {target}")

print("All story mode anime illustrations successfully deployed!")
