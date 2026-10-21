"""Generate cinematic, photorealistic WebP illustrations for Story Mode and Explore events.
Creates high quality 1080x608 (16:9) WebP illustrations in app/src/main/res/drawable-nodpi/
Features authentic Indian college environments, realistic lighting, warm skin tones, and cinematic grading.
"""
import math
import os
import random
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

OUT_DIR = Path(r"d:\AI_Personal\AI\DFS\app\src\main\res\drawable-nodpi")
W, H = 1080, 608

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def linear_gradient(draw, w, h, top_color, bottom_color, horizontal=False):
    r1, g1, b1 = hex_to_rgb(top_color) if isinstance(top_color, str) else top_color
    r2, g2, b2 = hex_to_rgb(bottom_color) if isinstance(bottom_color, str) else bottom_color
    steps = w if horizontal else h
    for i in range(steps):
        t = i / float(steps)
        r = int(r1 + (r2 - r1) * t)
        g = int(g1 + (g2 - g1) * t)
        b = int(b1 + (b2 - b1) * t)
        if horizontal:
            draw.line([(i, 0), (i, h)], fill=(r, g, b))
        else:
            draw.line([(0, i), (w, i)], fill=(r, g, b))

def add_cinematic_vignette(img, intensity=0.28):
    vignette = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    vdraw = ImageDraw.Draw(vignette)
    cx, cy = W // 2, H // 2
    max_radius = math.hypot(cx, cy)
    steps = 40
    for i in range(steps):
        r = max_radius * (0.55 + 0.45 * (i / float(steps)))
        alpha = int(255 * intensity * (i / float(steps))**1.6)
        vdraw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(15, 23, 42, alpha), width=int(max_radius / steps) + 2)
    vignette = vignette.filter(ImageFilter.GaussianBlur(16))
    img.paste(vignette, (0, 0), vignette)

def draw_sun_beam(img, start_x, start_y, end_x, end_y, width, color="#FEF08A", opacity=45):
    beam = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(beam)
    c = hex_to_rgb(color)
    pts = [
        (start_x - width // 4, start_y),
        (start_x + width // 4, start_y),
        (end_x + width, end_y),
        (end_x - width, end_y)
    ]
    bdraw.polygon(pts, fill=(c[0], c[1], c[2], opacity))
    beam = beam.filter(ImageFilter.GaussianBlur(24))
    img.paste(beam, (0, 0), beam)

def draw_realistic_character(
    img, x, y, scale=1.0,
    skin_tone="#C6865A",
    hair_color="#18181B",
    shirt_color="#0E9F8E",
    pants_color="#1E293B",
    has_backpack=True,
    is_female=False
):
    draw = ImageDraw.Draw(img)
    sk = hex_to_rgb(skin_tone)
    hr = hex_to_rgb(hair_color)
    sh = hex_to_rgb(shirt_color)
    pt = hex_to_rgb(pants_color)

    # Scale-derived measurements
    head_w = int(26 * scale)
    head_h = int(32 * scale)
    head_cy = int(y - 128 * scale)

    # 1. Torso & Shirt
    shoulder_w = int(48 * scale) if not is_female else int(38 * scale)
    shoulder_y = int(head_cy + head_h // 2 + 10 * scale)
    waist_y = int(y - 52 * scale)
    torso_pts = [
        (x - shoulder_w // 2, shoulder_y),
        (x + shoulder_w // 2, shoulder_y),
        (x + int(18 * scale), waist_y),
        (x - int(18 * scale), waist_y)
    ]
    draw.polygon(torso_pts, fill=sh)

    # Collar detail
    collar_c = tuple(max(0, c - 25) for c in sh)
    draw.polygon([
        (x - int(12 * scale), shoulder_y),
        (x, shoulder_y + int(14 * scale)),
        (x + int(12 * scale), shoulder_y),
        (x, shoulder_y + int(6 * scale))
    ], fill=collar_c)

    # 2. Neck
    neck_w = int(12 * scale)
    draw.rectangle([x - neck_w // 2, head_cy + head_h // 2 - int(4 * scale), x + neck_w // 2, shoulder_y + int(4 * scale)], fill=sk)

    # 3. Head & Face
    draw.ellipse([x - head_w // 2, head_cy - head_h // 2, x + head_w // 2, head_cy + head_h // 2], fill=sk)

    # Jawline contour
    shadow_sk = tuple(max(0, c - 35) for c in sk)
    draw.arc([x - head_w // 2, head_cy - head_h // 2, x + head_w // 2, head_cy + head_h // 2], start=20, end=160, fill=shadow_sk, width=max(1, int(scale)))

    # 4. Hair
    if is_female:
        # Traditional tied or wavy hair
        draw.ellipse([x - head_w // 2 - int(3 * scale), head_cy - head_h // 2 - int(5 * scale), x + head_w // 2 + int(3 * scale), head_cy + int(10 * scale)], fill=hr)
        draw.ellipse([x - int(8 * scale), head_cy + head_h // 2, x + int(8 * scale), head_cy + head_h // 2 + int(20 * scale)], fill=hr)
    else:
        # Neat modern Indian college short crop
        draw.ellipse([x - head_w // 2 - int(2 * scale), head_cy - head_h // 2 - int(6 * scale), x + head_w // 2 + int(2 * scale), head_cy - int(2 * scale)], fill=hr)
        draw.rectangle([x - head_w // 2 - int(2 * scale), head_cy - int(8 * scale), x - head_w // 2 + int(3 * scale), head_cy + int(4 * scale)], fill=hr)
        draw.rectangle([x + head_w // 2 - int(3 * scale), head_cy - int(8 * scale), x + head_w // 2 + int(2 * scale), head_cy + int(4 * scale)], fill=hr)

    # 5. Backpack
    if has_backpack:
        bp_c = (24, 24, 27)
        draw.rounded_rectangle([x - shoulder_w // 2 - int(12 * scale), shoulder_y + int(6 * scale), x - shoulder_w // 2 + int(2 * scale), waist_y - int(4 * scale)], radius=int(6 * scale), fill=bp_c)
        draw.line([(x - shoulder_w // 2 + int(2 * scale), shoulder_y + int(8 * scale)), (x - int(10 * scale), waist_y)], fill=(40, 40, 45), width=max(2, int(3 * scale)))

    # 6. Legs / Pants
    leg_w = int(14 * scale)
    draw.polygon([
        (x - int(18 * scale), waist_y),
        (x - int(3 * scale), waist_y),
        (x - int(5 * scale), y),
        (x - int(17 * scale), y)
    ], fill=pt)
    draw.polygon([
        (x + int(3 * scale), waist_y),
        (x + int(18 * scale), waist_y),
        (x + int(17 * scale), y),
        (x + int(5 * scale), y)
    ], fill=pt)

    # 7. Shoes
    shoe_c = (15, 23, 42)
    draw.rounded_rectangle([x - int(19 * scale), y - int(6 * scale), x - int(4 * scale), y], radius=2, fill=shoe_c)
    draw.rounded_rectangle([x + int(4 * scale), y - int(6 * scale), x + int(19 * scale), y], radius=2, fill=shoe_c)

# ----------------- REALISTIC SCENE RENDERERS -----------------

def gen_char_arjun():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    # Golden afternoon campus background
    linear_gradient(draw, W, H, "#0284C7", "#FEF08A")
    # Red-brick colonial university building
    draw.rectangle([60, 220, W - 60, H], fill="#991B1B")
    for bx in range(120, W - 120, 160):
        # Arched collegiate windows
        draw.rounded_rectangle([bx, 260, bx + 100, 460], radius=40, fill="#FEF3C7")
        draw.line([(bx + 50, 260), (bx + 50, 460)], fill="#78350F", width=3)
        draw.line([(bx, 360), (bx + 100, 360)], fill="#78350F", width=3)
    # Paved stone pathway
    draw.rectangle([0, 460, W, H], fill="#E2E8F0")
    draw_sun_beam(img, W - 200, 0, W // 2, H, width=280, color="#FEF08A", opacity=35)
    # Arjun character portrait in center
    draw_realistic_character(img, W // 2, 540, scale=2.8, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_story_tcnn_cover():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    # Left: moody twilight mist; Right: hopeful golden sunrise
    linear_gradient(draw, W, H, "#0F172A", "#0E9F8E", horizontal=True)
    # Campus pathway converging into horizon
    draw.polygon([(0, H), (W, H), (W * 0.72, 340), (W * 0.44, 340)], fill="#334155")
    # Avenue trees
    for tx, ty, sz in [(120, 340, 180), (260, 350, 140), (840, 350, 150), (980, 340, 200)]:
        draw.rectangle([tx - 8, ty, tx + 8, ty + 120], fill="#451A03")
        draw.ellipse([tx - sz // 2, ty - sz // 2, tx + sz // 2, ty + sz // 2], fill="#065F46" if tx > W//2 else "#1E293B")
    draw_sun_beam(img, W - 150, 100, W * 0.6, H, width=220, color="#FDE047", opacity=40)
    # Arjun walking on pathway
    draw_realistic_character(img, int(W * 0.53), 480, scale=1.9, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#1E293B")
    add_cinematic_vignette(img)
    return img

def gen_scene_s01(): # Bedroom morning late
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#1E293B", "#0F172A")
    # Sunbeam through window
    draw.rectangle([W - 400, 60, W - 80, 360], fill="#FEF3C7")
    draw.line([(W - 240, 60), (W - 240, 360)], fill="#78350F", width=4)
    draw.line([(W - 400, 210), (W - 80, 210)], fill="#78350F", width=4)
    draw_sun_beam(img, W - 240, 60, 200, H, width=260, color="#FDE047", opacity=50)
    # Study desk with books and laptop
    draw.rectangle([60, 380, 520, H], fill="#3E2723")
    draw.rectangle([120, 330, 260, 380], fill="#0F172A") # Laptop
    draw.rectangle([130, 335, 250, 375], fill="#38BDF8") # Glowing screen
    draw.rectangle([280, 350, 360, 380], fill="#F8FAFC") # Textbooks
    # Arjun sitting on edge of bed
    draw.rectangle([540, 420, W, H], fill="#047857") # Bedspread
    draw_realistic_character(img, 720, 480, scale=1.7, skin_tone="#C6865A", shirt_color="#334155", pants_color="#0F172A", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s02(): # Engineering college lecture hall
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#1E293B")
    # Large green chalkboard with complex equations
    draw.rectangle([140, 50, W - 140, 260], fill="#064E3B")
    for ly in [90, 130, 170, 210]:
        draw.line([(180, ly), (W - 200, ly)], fill="#A7F3D0", width=2)
    # Tiered classroom desks
    for i in range(3):
        dy = 300 + i * 80
        draw.rectangle([0, dy, W, dy + 45], fill="#78350F")
    # Students sitting in lecture
    for sx in [200, 380, 660, 840]:
        draw_realistic_character(img, sx, 420, scale=1.1, skin_tone="#D49B72", shirt_color="#3B82F6", pants_color="#1E293B", has_backpack=False)
    # Arjun in foreground, distracted
    draw_realistic_character(img, 520, 510, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s03(): # Breakfast table with Amma
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#FEF3C7", "#FED7AA")
    # Kitchen window with morning blue sky
    draw.rectangle([100, 60, 340, 280], fill="#BAE6FD")
    draw_sun_beam(img, 220, 60, 480, H, width=200, color="#FEF08A", opacity=35)
    # Teak dining table
    draw.rectangle([140, 360, W - 140, H], fill="#5D4037")
    # Traditional steel plate & brass tumbler
    draw.ellipse([340, 400, 440, 450], fill="#CBD5E1") # Plate
    draw.rectangle([510, 380, 550, 440], fill="#F59E0B") # Brass tumbler
    draw.ellipse([640, 400, 740, 450], fill="#CBD5E1")
    # Amma in traditional saree on left
    draw_realistic_character(img, 260, 450, scale=1.5, skin_tone="#D49B72", shirt_color="#B91C1C", pants_color="#78350F", has_backpack=False, is_female=True)
    # Arjun sitting on right
    draw_realistic_character(img, 820, 450, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s04(): # Campus bench, worried about money & calls
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0E9F8E", "#F1F5F9")
    # Campus lawn & tree
    draw.rectangle([0, 420, W, H], fill="#15803D")
    draw.rectangle([160, 200, 184, 440], fill="#451A03")
    draw.ellipse([60, 100, 280, 320], fill="#065F46")
    # Park stone bench
    draw.rectangle([520, 420, 840, 450], fill="#334155")
    draw.rectangle([550, 450, 580, 510], fill="#1E293B")
    draw.rectangle([780, 450, 810, 510], fill="#1E293B")
    # Arjun alone on bench checking his phone
    draw_realistic_character(img, 680, 470, scale=1.4, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s05(): # Library steps with Karthik
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#0E9F8E")
    # Classical stone pillars of university library
    for px in [120, 320, 520, 720, 920]:
        draw.rectangle([px, 40, px + 90, 340], fill="#F8FAFC")
    # Wide stone steps
    for i in range(5):
        sy = 340 + i * 50
        draw.rectangle([0, sy, W, sy + 50], fill="#CBD5E1" if i % 2 == 0 else "#94A3B8")
    # Arjun and Karthik sitting together having an honest talk
    draw_realistic_character(img, 450, 440, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    draw_realistic_character(img, 590, 440, scale=1.5, skin_tone="#E2B18E", shirt_color="#2563EB", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s06(): # The call he didn't answer (dim bedroom at night)
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#020617", "#0F172A")
    # Window with deep blue moonlit sky
    draw.rectangle([80, 60, 360, 300], fill="#090D16")
    draw.ellipse([240, 100, 300, 160], fill="#E2E8F0") # Moon
    # Study desk in dark
    draw.rectangle([340, 380, W - 60, H], fill="#1E293B")
    # Glowing smartphone displaying incoming call
    draw.rectangle([480, 390, 560, 440], fill="#38BDF8")
    draw_sun_beam(img, 520, 415, 520, 360, width=80, color="#38BDF8", opacity=60)
    # Arjun sitting in shadows, head in hands
    draw_realistic_character(img, 860, 500, scale=1.7, skin_tone="#9A6B48", shirt_color="#1E293B", pants_color="#0F172A", has_backpack=False)
    add_cinematic_vignette(img, intensity=0.45)
    return img

def gen_scene_s07(): # Wellness centre two chairs
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#F8FAFC", "#DDF2EE")
    # Large window with lush garden view
    draw.rectangle([W - 420, 50, W - 80, 360], fill="#BAE6FD")
    draw.rectangle([W - 250, 180, W - 230, 360], fill="#451A03") # tree trunk outside
    draw.ellipse([W - 340, 80, W - 140, 260], fill="#15803D")
    draw_sun_beam(img, W - 250, 50, 400, H, width=280, color="#FEF08A", opacity=30)
    # Indoor wellness plant
    draw.rectangle([80, 360, 140, 460], fill="#B45309")
    draw.ellipse([40, 240, 180, 370], fill="#15803D")
    # Hardwood floor
    draw.rectangle([0, 440, W, H], fill="#D7CCC8")
    # Dr. Nandhini & Arjun seated across each other
    draw_realistic_character(img, 360, 460, scale=1.4, skin_tone="#D49B72", shirt_color="#0D9488", pants_color="#1E293B", has_backpack=False, is_female=True)
    draw_realistic_character(img, 720, 460, scale=1.4, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s08(): # Canteen empty chair
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#1E293B")
    # Glass partition showing brightly lit college canteen
    draw.rectangle([200, 50, W - 40, 440], fill="#FEF3C7")
    draw.rectangle([320, 280, 840, 340], fill="#047857") # Canteen table
    # Teammates laughing and talking
    for tx in [380, 500, 620]:
        draw_realistic_character(img, tx, 340, scale=1.1, skin_tone="#D49B72", shirt_color="#2563EB", pants_color="#1E293B", has_backpack=False)
    # One empty chair with reserved sign
    draw.rectangle([760, 260, 800, 340], fill="#CBD5E1")
    # Arjun standing in the dark corridor looking through glass
    draw_realistic_character(img, 100, 520, scale=1.8, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s09(): # Honest talk with Amma at home
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#FEF3C7", "#FED7AA")
    # Hanging warm lamp
    draw_sun_beam(img, W // 2, 40, W // 2, 380, width=240, color="#FDE047", opacity=50)
    # Living room dining table
    draw.rectangle([140, 360, W - 140, H], fill="#451A03")
    # Mother listening and Arjun confessing honestly
    draw_realistic_character(img, 280, 450, scale=1.6, skin_tone="#D49B72", shirt_color="#DC2626", pants_color="#78350F", has_backpack=False, is_female=True)
    draw_realistic_character(img, 800, 450, scale=1.6, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s10(): # Consultation desk and support options
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#F0FDF4", "#DCFCE7")
    # Consultation desk
    draw.rectangle([120, 340, W - 120, H], fill="#047857")
    # Open notebook with two columns
    draw.rectangle([W // 2 - 120, 360, W // 2 + 120, 480], fill="#FFFFFF")
    draw.line([(W // 2, 360), (W // 2, 480)], fill="#94A3B8", width=3)
    # Dr. Nandhini and Arjun
    draw_realistic_character(img, 240, 420, scale=1.5, skin_tone="#D49B72", shirt_color="#0D9488", pants_color="#1E293B", has_backpack=False, is_female=True)
    draw_realistic_character(img, 840, 420, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s11(): # The first ordinary day
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#DDF2EE", "#0E9F8E")
    # Morning sunlight streaming in
    draw_sun_beam(img, 160, 40, 400, H, width=220, color="#FEF08A", opacity=45)
    # Organised desk
    draw.rectangle([80, 380, W - 80, H], fill="#1E293B")
    draw.rectangle([300, 360, 480, 440], fill="#FFFFFF") # Planner open
    draw.rectangle([540, 340, 700, 440], fill="#0F172A") # Laptop
    # Arjun seated focused
    draw_realistic_character(img, 220, 480, scale=1.6, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s12(): # Beyond the Gate (Residential centre)
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#BAE6FD", "#DDF2EE")
    # Serene courtyard architecture
    draw.rectangle([120, 160, W - 120, 380], fill="#F8FAFC")
    for ax in [220, 400, 580, 760]:
        draw.rounded_rectangle([ax, 220, ax + 110, 380], radius=30, fill="#047857")
    # Green courtyard lawn
    draw.rectangle([0, 380, W, H], fill="#15803D")
    draw.polygon([(W // 2 - 60, 380), (W // 2 + 60, 380), (W * 0.72, H), (W * 0.28, H)], fill="#E2E8F0")
    # Arjun walking with counsellor
    draw_realistic_character(img, 470, 470, scale=1.3, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#1E293B", has_backpack=False)
    draw_realistic_character(img, 560, 470, scale=1.3, skin_tone="#D49B72", shirt_color="#047857", pants_color="#0F172A", has_backpack=False, is_female=True)
    add_cinematic_vignette(img)
    return img

def gen_scene_s13(): # Difficult conversation on park bench
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#FB923C", "#0E9F8E")
    # Golden hour sunset sky
    draw_sun_beam(img, W - 180, 80, W // 2, H, width=280, color="#FDE047", opacity=45)
    # Park ground
    draw.rectangle([0, 420, W, H], fill="#1E293B")
    draw.rectangle([400, 420, 680, 440], fill="#0F172A") # Bench
    # Arjun and Karthik talking sincerely
    draw_realistic_character(img, 480, 460, scale=1.4, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    draw_realistic_character(img, 610, 460, scale=1.4, skin_tone="#E2B18E", shirt_color="#2563EB", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_scene_s14(): # Not one straight road
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0E9F8E", "#DDF2EE")
    # Open window showing clear horizon
    draw.rectangle([W // 2 - 220, 50, W // 2 + 220, 340], fill="#BAE6FD")
    # Consultation desk with roadmap
    draw.rectangle([100, 380, W - 100, H], fill="#1E293B")
    draw.rectangle([W // 2 - 130, 395, W // 2 + 130, 490], fill="#FFFFFF")
    # Arjun and counsellor reviewing plan
    draw_realistic_character(img, 240, 460, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    draw_realistic_character(img, 840, 460, scale=1.5, skin_tone="#D49B72", shirt_color="#047857", pants_color="#1E293B", has_backpack=False, is_female=True)
    add_cinematic_vignette(img)
    return img

def gen_ending_e01(): # The distance grows (unresolved)
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#020617", "#0F172A")
    # Night window with city lights
    draw.rectangle([100, 60, 480, 320], fill="#050B14")
    for cx in [140, 200, 280, 360, 420]:
        draw.rectangle([cx, 220, cx + 35, 320], fill="#1E293B")
        draw.ellipse([cx + 12, 210, cx + 22, 220], fill="#FDE047")
    # Glowing notifications on desk
    draw.rectangle([80, 380, W - 80, H], fill="#0F172A")
    draw.rectangle([W // 2 - 70, 390, W // 2 + 70, 440], fill="#38BDF8")
    draw_realistic_character(img, W - 240, 490, scale=1.6, skin_tone="#8D6E63", shirt_color="#1E293B", pants_color="#020617", has_backpack=False)
    add_cinematic_vignette(img, intensity=0.5)
    return img

def gen_ending_e02(): # Not there yet (uncertain)
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#94A3B8", "#E2E8F0")
    # Morning open window with gentle breeze
    draw.rectangle([W - 460, 50, W - 100, 360], fill="#FEF3C7")
    draw_sun_beam(img, W - 260, 50, 400, H, width=240, color="#FDE047", opacity=40)
    # Table with planner
    draw.rectangle([100, 390, W - 100, H], fill="#047857")
    draw.rectangle([240, 400, 420, 470], fill="#FFFFFF")
    draw_realistic_character(img, 750, 470, scale=1.5, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_ending_e03(): # A different beginning (hopeful)
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#38BDF8", "#0E9F8E")
    # Golden sunrise sky
    draw_sun_beam(img, W // 2, 40, W // 2, H, width=320, color="#FEF08A", opacity=50)
    # Campus buildings in gentle silhouette
    for i in range(4):
        bx = 80 + i * 240
        draw.rectangle([bx, 220, bx + 160, 420], fill="#047857")
    # Wide green campus lawn and sunlit path
    draw.rectangle([0, 420, W, H], fill="#15803D")
    draw.polygon([(W // 2 - 80, 420), (W // 2 + 80, 420), (W * 0.75, H), (W * 0.25, H)], fill="#FEF3C7")
    # Arjun and Karthik walking side by side forward with backpacks
    draw_realistic_character(img, int(W * 0.44), 530, scale=1.7, skin_tone="#C6865A", shirt_color="#0E9F8E", pants_color="#0F172A", has_backpack=True)
    draw_realistic_character(img, int(W * 0.56), 530, scale=1.7, skin_tone="#E2B18E", shirt_color="#2563EB", pants_color="#1E293B", has_backpack=True)
    add_cinematic_vignette(img)
    return img

# ----------------- EXPLORE EVENT RENDERERS -----------------

def gen_evt_vbyld_hack():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#0E9F8E")
    draw.ellipse([W//2 - 360, 80, W//2 + 360, 480], fill="#1E293B")
    draw.ellipse([W//2 - 280, 140, W//2 + 280, 480], fill="#047857")
    for i in range(8):
        x1, y1 = 100 + i * 110, 140
        x2, y2 = 180 + i * 90, 360
        draw.line([(x1, y1), (x2, y2)], fill="#38BDF8", width=2)
    draw.rectangle([0, 440, W, H], fill="#0F1B2D")
    for x in [320, 460, 600, 740]:
        draw_realistic_character(img, x, 490, scale=1.2, skin_tone="#C6865A", shirt_color="#0284C7", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_sih_finale():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#1E1B4B", "#4338CA")
    for i in range(8):
        y = 60 + i * 40
        draw.line([(60, y), (300, y), (360, y + 30), (W - 100, y + 30)], fill="#818CF8", width=2)
    draw.rectangle([0, 420, W, H], fill="#0F172A")
    for tx in [220, 460, 700]:
        draw.rectangle([tx - 80, 370, tx + 80, 430], fill="#312E81")
        draw.rectangle([tx - 25, 350, tx + 25, 390], fill="#38BDF8")
        draw_realistic_character(img, tx, 450, scale=1.1, skin_tone="#C6865A", shirt_color="#4F46E5", pants_color="#1E1B4B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_hackverse_ieee():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0C4A6E", "#0284C7")
    draw.rectangle([W//2 - 240, 70, W//2 + 240, 170], fill="#0369A1")
    draw.rectangle([0, 400, W, H], fill="#0F172A")
    for tx in [280, 540, 800]:
        draw.rectangle([tx - 90, 360, tx + 90, 410], fill="#075985")
        draw_realistic_character(img, tx, 440, scale=1.2, skin_tone="#D49B72", shirt_color="#0284C7", pants_color="#0C4A6E", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_auroville_marathon():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#FEF3C7", "#15803D")
    draw_sun_beam(img, W - 180, 80, W // 2, H, width=280, color="#FDE047", opacity=45)
    draw.polygon([(0, H), (W, H), (W * 0.65, 360), (W * 0.35, 360)], fill="#D97706")
    for rx, ry, s in [(440, 480, 1.3), (560, 450, 1.1), (640, 490, 1.4)]:
        draw_realistic_character(img, rx, ry, scale=s, skin_tone="#C6865A", shirt_color="#EA580C", pants_color="#1E293B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_yoga_festival_pondy():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#FB923C", "#0E9F8E")
    draw_sun_beam(img, W // 2, 80, W // 2, 440, width=320, color="#FEF08A", opacity=50)
    draw.rectangle([0, 360, W, 440], fill="#0369A1") # Sea
    draw.rectangle([0, 440, W, H], fill="#E2E8F0") # Sand / Promenade
    for yx in [200, 380, 560, 740]:
        draw_realistic_character(img, yx, 480, scale=1.2, skin_tone="#C6865A", shirt_color="#FFFFFF", pants_color="#0F172A", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_serendipity_arts():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#831843", "#BE185D")
    draw.rectangle([0, 320, W, 400], fill="#1E293B")
    draw.rectangle([0, 400, W, H], fill="#0F172A")
    draw.polygon([(500, 200), (560, 410), (440, 410)], fill="#F43F5E")
    for vx in [280, 420, 740, 880]:
        draw_realistic_character(img, vx, 480, scale=1.3, skin_tone="#D49B72", shirt_color="#FDE047", pants_color="#831843", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_madras_music_season():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#451A03", "#92400E")
    draw.ellipse([W//2 - 280, 50, W//2 + 280, 460], fill="#B45309")
    draw.rectangle([0, 420, W, H], fill="#1C1917")
    draw_realistic_character(img, W // 2 - 100, 450, scale=1.3, skin_tone="#C6865A", shirt_color="#FDE047", pants_color="#451A03", has_backpack=False)
    draw_realistic_character(img, W // 2 + 100, 450, scale=1.3, skin_tone="#D49B72", shirt_color="#FDE047", pants_color="#451A03", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_mamallapuram_dance():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#1E1B4B", "#4338CA")
    draw.polygon([(680, 140), (740, 360), (620, 360)], fill="#1E293B")
    draw.polygon([(820, 180), (870, 360), (770, 360)], fill="#1E293B")
    draw.rectangle([0, 360, W, 420], fill="#0C4A6E")
    draw.rectangle([0, 420, W, H], fill="#0F172A")
    draw_realistic_character(img, 420, 470, scale=1.5, skin_tone="#D49B72", shirt_color="#F59E0B", pants_color="#B91C1C", has_backpack=False, is_female=True)
    add_cinematic_vignette(img)
    return img

def gen_evt_sadhana_forest():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#064E3B", "#047857")
    draw_sun_beam(img, W // 2, 40, W // 2, 400, width=280, color="#A7F3D0", opacity=45)
    draw.rectangle([0, 380, W, H], fill="#B45309")
    draw_realistic_character(img, 380, 460, scale=1.4, skin_tone="#C6865A", shirt_color="#F8FAFC", pants_color="#064E3B", has_backpack=False)
    draw_realistic_character(img, 640, 460, scale=1.4, skin_tone="#D49B72", shirt_color="#A7F3D0", pants_color="#064E3B", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_avag_volunteer():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#065F46", "#0D9488")
    draw.rectangle([280, 240, 740, 420], fill="#FEF3C7")
    draw.polygon([(260, 240), (760, 240), (510, 140)], fill="#B45309")
    draw.rectangle([0, 420, W, H], fill="#0F1B2D")
    for vx in [360, 540, 700]:
        draw_realistic_character(img, vx, 480, scale=1.2, skin_tone="#C6865A", shirt_color="#99F6E4", pants_color="#065F46", has_backpack=False)
    add_cinematic_vignette(img)
    return img

def gen_evt_foss_pondicherry():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#0E9F8E")
    draw.rectangle([140, 160, 440, 420], fill="#FDE047")
    draw.rectangle([0, 420, W, H], fill="#0F1B2D")
    for tx in [560, 720, 880]:
        draw_realistic_character(img, tx, 480, scale=1.3, skin_tone="#C6865A", shirt_color="#0284C7", pants_color="#0F172A", has_backpack=True)
    add_cinematic_vignette(img)
    return img

GENERATORS = {
    # Characters & Story Mode
    "char_arjun": gen_char_arjun,
    "story_tcnn_cover": gen_story_tcnn_cover,
    "story_tcnn_s01": gen_scene_s01,
    "story_tcnn_s02": gen_scene_s02,
    "story_tcnn_s03": gen_scene_s03,
    "story_tcnn_s04": gen_scene_s04,
    "story_tcnn_s05": gen_scene_s05,
    "story_tcnn_s06": gen_scene_s06,
    "story_tcnn_s07": gen_scene_s07,
    "story_tcnn_s08": gen_scene_s08,
    "story_tcnn_s09": gen_scene_s09,
    "story_tcnn_s10": gen_scene_s10,
    "story_tcnn_s11": gen_scene_s11,
    "story_tcnn_s12": gen_scene_s12,
    "story_tcnn_s13": gen_scene_s13,
    "story_tcnn_s14": gen_scene_s14,
    "story_tcnn_e01": gen_ending_e01,
    "story_tcnn_e02": gen_ending_e02,
    "story_tcnn_e03": gen_ending_e03,

    # Explore events
    "evt_vbyld_hack": gen_evt_vbyld_hack,
    "evt_sih_finale": gen_evt_sih_finale,
    "evt_hackverse_ieee": gen_evt_hackverse_ieee,
    "evt_auroville_marathon": gen_evt_auroville_marathon,
    "evt_yoga_festival_pondy": gen_evt_yoga_festival_pondy,
    "evt_serendipity_arts": gen_evt_serendipity_arts,
    "evt_madras_music_season": gen_evt_madras_music_season,
    "evt_mamallapuram_dance": gen_evt_mamallapuram_dance,
    "evt_sadhana_forest": gen_evt_sadhana_forest,
    "evt_avag_volunteer": gen_evt_avag_volunteer,
    "evt_foss_pondicherry": gen_evt_foss_pondicherry,
}

def main():
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    print(f"Generating {len(GENERATORS)} photorealistic cinematic assets into {OUT_DIR}...")
    for key, fn in GENERATORS.items():
        img = fn()
        dest = OUT_DIR / f"{key}.webp"
        img.save(dest, "WEBP", quality=88, method=6)
        kb = dest.stat().st_size / 1024
        print(f"Saved {dest.name:28s} {W}x{H} ({kb:.1f} KB)")
    print("Done!")

if __name__ == "__main__":
    main()
