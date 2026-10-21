"""Generate attractive, vibrant poster-style illustrations for Explore Events and Activities.
Creates high-contrast, visually stunning 1080x608 (16:9) WebP posters in app/src/main/res/drawable-nodpi/
Features authentic themes: hackathons, athletic running, coastal yoga, art exhibitions,
classical concerts, temple dances, reforestation seva, tech community, and food drives.
"""
import math
import os
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont

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

def add_poster_vignette(img, intensity=0.32):
    vignette = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    vdraw = ImageDraw.Draw(vignette)
    cx, cy = W // 2, H // 2
    max_radius = math.hypot(cx, cy)
    steps = 40
    for i in range(steps):
        r = max_radius * (0.50 + 0.50 * (i / float(steps)))
        alpha = int(255 * intensity * (i / float(steps))**1.5)
        vdraw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(10, 15, 30, alpha), width=int(max_radius / steps) + 2)
    vignette = vignette.filter(ImageFilter.GaussianBlur(16))
    img.paste(vignette, (0, 0), vignette)

def add_badge(img, x, y, text, bg_color="#F59E0B", text_color="#FFFFFF"):
    badge = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(badge)
    # pill
    pad_h = 16
    pad_v = 8
    font = None
    try:
        font = ImageFont.truetype("arial.ttf", 26)
    except:
        font = ImageFont.load_default()
    
    bbox = bdraw.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    
    b_bg = hex_to_rgb(bg_color)
    bdraw.rounded_rectangle([x, y, x + tw + pad_h * 2, y + th + pad_v * 2], radius=16, fill=(b_bg[0], b_bg[1], b_bg[2], 230))
    t_c = hex_to_rgb(text_color)
    bdraw.text((x + pad_h, y + pad_v), text, fill=(t_c[0], t_c[1], t_c[2], 255), font=font)
    img.paste(badge, (0, 0), badge)

def draw_poster_heading(img, x, y, title, subtitle="", tag="OFFICIAL EVENT", accent="#38BDF8"):
    overlay = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    
    # Bottom gradient overlay for readability
    for i in range(240):
        alpha = int((i / 240.0)**1.5 * 210)
        draw.line([(0, H - 240 + i), (W, H - 240 + i)], fill=(15, 23, 42, alpha))
        
    font_title = None
    font_sub = None
    font_tag = None
    try:
        font_title = ImageFont.truetype("arialbd.ttf", 46)
        font_sub = ImageFont.truetype("arial.ttf", 26)
        font_tag = ImageFont.truetype("arialbd.ttf", 20)
    except:
        font_title = ImageFont.load_default()
        font_sub = ImageFont.load_default()
        font_tag = ImageFont.load_default()

    # Tag chip
    if tag:
        acc = hex_to_rgb(accent)
        draw.rounded_rectangle([x, y - 40, x + 160, y - 10], radius=8, fill=(acc[0], acc[1], acc[2], 220))
        draw.text((x + 12, y - 36), tag, fill=(15, 23, 42, 255), font=font_tag)

    # Title & Subtitle
    draw.text((x, y), title, fill=(255, 255, 255, 255), font=font_title)
    if subtitle:
        draw.text((x, y + 54), subtitle, fill=(203, 213, 225, 235), font=font_sub)
        
    img.paste(overlay, (0, 0), overlay)

def draw_circuit_lines(draw, start_x, start_y, count=6, color="#38BDF8"):
    c = hex_to_rgb(color)
    for i in range(count):
        y = start_y + i * 40
        draw.line([(start_x, y), (start_x + 120 + i * 30, y)], fill=c, width=3)
        draw.line([(start_x + 120 + i * 30, y), (start_x + 180 + i * 30, y + 40)], fill=c, width=3)
        draw.ellipse([start_x + 175 + i * 30, y + 35, start_x + 185 + i * 30, y + 45], fill=c)

# 1. Viksit Bharat Young Leaders Dialogue / Hack for Social Cause
def gen_evt_vbyld_hack():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0B192C", "#1E3E62")
    # glowing saffron / electric blue accents
    draw_circuit_lines(draw, 60, 60, count=7, color="#FF9933")
    draw_circuit_lines(draw, 750, 40, count=6, color="#00C49F")
    
    # Mandapam stylized dome
    draw.arc([W//2 - 220, 80, W//2 + 220, 460], 180, 360, fill="#FF9933", width=4)
    draw.line([(W//2, 70), (W//2, 110)], fill="#FF9933", width=4)
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Viksit Bharat Young Leaders 2027",
        subtitle="National Hack for Social Cause · Bharat Mandapam, New Delhi",
        tag="HACKATHON",
        accent="#FF9933"
    )
    add_poster_vignette(img)
    return img

# 2. Smart India Hackathon
def gen_evt_sih_finale():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#1E1B4B", "#312E81")
    
    # Glowing digital nodes & India innovation grid
    for x in range(100, W, 140):
        for y in range(60, 380, 80):
            draw.ellipse([x - 4, y - 4, x + 4, y + 4], fill="#6366F1")
            if x + 140 < W:
                draw.line([(x, y), (x + 140, y + 40)], fill=(99, 102, 241, 60), width=1)
    
    # Stylized trophy / bulb icon
    draw.ellipse([W//2 - 90, 80, W//2 + 90, 260], outline="#FDE047", width=5)
    draw.polygon([(W//2 - 40, 260), (W//2 + 40, 260), (W//2 + 20, 320), (W//2 - 20, 320)], fill="#FDE047")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Smart India Hackathon 2026",
        subtitle="Grand Finale · 36-Hour National Innovation Marathon · AICTE",
        tag="GRAND FINALE",
        accent="#FDE047"
    )
    add_poster_vignette(img)
    return img

# 3. HackVerse IEEE
def gen_evt_hackverse_ieee():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#042F2E", "#115E59")
    
    # IEEE cyber diamond
    draw.polygon([(W//2, 60), (W//2 + 180, 220), (W//2, 380), (W//2 - 180, 220)], outline="#2DD4BF", width=4)
    draw.polygon([(W//2, 90), (W//2 + 140, 220), (W//2, 350), (W//2 - 140, 220)], outline="#14B8A6", width=2)
    draw_circuit_lines(draw, 50, 100, count=5, color="#2DD4BF")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="HackVerse: IEEE Challenge",
        subtitle="Intercollegiate 2-Day Hackathon · Mentorship & Coding",
        tag="IEEE TECH",
        accent="#2DD4BF"
    )
    add_poster_vignette(img)
    return img

# 4. Auroville Marathon
def gen_evt_auroville_marathon():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#064E3B", "#15803D")
    
    # Trails and sunrise rays
    draw.ellipse([W//2 - 120, 40, W//2 + 120, 280], fill="#FEF08A")
    # Forest silhouettes
    draw.polygon([(100, 380), (220, 180), (340, 380)], fill="#064E3B")
    draw.polygon([(300, 380), (450, 160), (600, 380)], fill="#043D2F")
    draw.polygon([(560, 380), (700, 190), (840, 380)], fill="#064E3B")
    draw.polygon([(780, 380), (920, 170), (1040, 380)], fill="#043D2F")
    
    # Road curve
    draw.arc([-200, 320, W + 200, 700], 190, 350, fill="#E2E8F0", width=16)
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Auroville Marathon 2027",
        subtitle="10K · Half · Full Marathon · Run for the Joy of Running",
        tag="42K RUN",
        accent="#4ADE80"
    )
    add_poster_vignette(img)
    return img

# 5. International Yoga Festival Puducherry
def gen_evt_yoga_festival_pondy():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#7C2D12", "#EA580C")
    
    # Rising sun over ocean horizon
    draw.ellipse([W//2 - 160, 100, W//2 + 160, 420], fill="#FEF08A")
    draw.rectangle([0, 320, W, 400], fill="#0284C7")
    draw.rectangle([0, 400, W, H], fill="#0369A1")
    
    # Lotus petals geometry
    draw.arc([W//2 - 80, 180, W//2 + 80, 340], 0, 180, fill="#FFFFFF", width=4)
    draw.arc([W//2 - 130, 200, W//2 + 130, 340], 0, 180, fill="#FFFFFF", width=3)
    
    draw_poster_heading(
        img, 60, H - 150,
        title="International Yoga Festival",
        subtitle="Puducherry Promenade Beach · 32nd Edition · Yogasana & Wellness",
        tag="WELLNESS",
        accent="#FEF08A"
    )
    add_poster_vignette(img)
    return img

# 6. Serendipity Arts Festival Panaji
def gen_evt_serendipity_arts():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#4C0519", "#9F1239")
    
    # Expressive multi-color geometric art shapes
    draw.polygon([(200, 80), (380, 40), (320, 280)], fill="#F43F5E")
    draw.polygon([(460, 60), (620, 180), (510, 320)], fill="#FB7185")
    draw.ellipse([700, 90, 880, 270], fill="#FDE047")
    draw.polygon([(140, 240), (280, 340), (160, 380)], fill="#38BDF8")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Serendipity Arts Festival",
        subtitle="Panaji, Goa · Visual Arts, Theatre, Crafts & Music Across Heritage Spaces",
        tag="ARTS & CULTURE",
        accent="#FB7185"
    )
    add_poster_vignette(img)
    return img

# 7. Madras Music Season
def gen_evt_madras_music_season():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#3B0764", "#6B21A8")
    
    # Classical Veena & Tambura curves
    draw.ellipse([W//2 - 100, 180, W//2 + 100, 380], fill="#D97706")
    draw.rectangle([W//2 - 12, 40, W//2 + 12, 280], fill="#B45309")
    for sy in [60, 90, 120]:
        draw.line([(W//2 - 40, sy), (W//2 + 40, sy)], fill="#FDE047", width=4)
        
    draw_poster_heading(
        img, 60, H - 150,
        title="Madras Music Season",
        subtitle="The Music Academy, Madras · Prestigious December Carnatic Concerts",
        tag="CARNATIC MUSIC",
        accent="#FDE047"
    )
    add_poster_vignette(img)
    return img

# 8. Mamallapuram Dance Festival
def gen_evt_mamallapuram_dance():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#1E1B4B", "#3730A3")
    
    # Shore Temple monolithic stone silhouette
    draw.polygon([(680, 80), (740, 340), (620, 340)], fill="#1E293B")
    draw.polygon([(820, 140), (870, 340), (770, 340)], fill="#0F172A")
    draw.rectangle([580, 340, 940, 390], fill="#1E293B")
    
    # Waves hitting shore
    draw.rectangle([0, 380, W, H], fill="#0F172A")
    for wy in [400, 420, 440]:
        draw.line([(0, wy), (W, wy)], fill=(56, 189, 248, 60), width=2)
        
    draw_poster_heading(
        img, 60, H - 150,
        title="Indian Dance Festival",
        subtitle="Shore Temple, Mamallapuram · Classical Bharatanatyam, Kathak & Odissi",
        tag="HERITAGE DANCE",
        accent="#F59E0B"
    )
    add_poster_vignette(img)
    return img

# 9. Sadhana Forest Volunteering
def gen_evt_sadhana_forest():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#064E3B", "#059669")
    
    # Canopy and water conservation swales
    draw.ellipse([W//2 - 240, -40, W//2 + 240, 320], fill="#047857")
    draw.ellipse([W//2 - 180, 40, W//2 + 180, 360], fill="#10B981")
    draw.polygon([(W//2 - 30, 220), (W//2 + 30, 220), (W//2 + 40, 440), (W//2 - 40, 440)], fill="#78350F")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Sadhana Forest Community Seva",
        subtitle="Auroville · Indigenous Reforestation, Swales & Vegan Substance-Free Living",
        tag="ECO VOLUNTEER",
        accent="#6EE7B7"
    )
    add_poster_vignette(img)
    return img

# 10. Auroville Village Action Group (AVAG)
def gen_evt_avag_volunteer():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0C4A6E", "#0284C7")
    
    # Community hands & rural upliftment geometric sun
    draw.ellipse([W//2 - 140, 60, W//2 + 140, 340], fill="#FBBF24")
    for i in range(12):
        angle = i * (math.pi / 6)
        x1 = W//2 + int(160 * math.cos(angle))
        y1 = 200 + int(160 * math.sin(angle))
        x2 = W//2 + int(210 * math.cos(angle))
        y2 = 200 + int(210 * math.sin(angle))
        draw.line([(x1, y1), (x2, y2)], fill="#FDE68A", width=4)
        
    draw_poster_heading(
        img, 60, H - 150,
        title="AVAG Village Action Group",
        subtitle="Auroville & Bio-region · Grassroots Youth Empowerment & Social Impact",
        tag="VOLUNTEERING",
        accent="#FDE68A"
    )
    add_poster_vignette(img)
    return img

# 11. FOSS United Pondicherry
def gen_evt_foss_pondicherry():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#0F172A", "#1E293B")
    
    # Open Source code brackets </ >
    draw.line([(360, 120), (280, 240)], fill="#38BDF8", width=12)
    draw.line([(280, 240), (360, 360)], fill="#38BDF8", width=12)
    
    draw.line([(720, 120), (800, 240)], fill="#38BDF8", width=12)
    draw.line([(800, 240), (720, 360)], fill="#38BDF8", width=12)
    
    draw.line([(580, 100), (500, 380)], fill="#F43F5E", width=12)
    
    draw_poster_heading(
        img, 60, H - 150,
        title="FOSS United Chapter",
        subtitle="Puducherry Tech Community · Free & Open Source Software Meetups",
        tag="DEV COMMUNITY",
        accent="#38BDF8"
    )
    add_poster_vignette(img)
    return img

# 12. Robin Hood Army
def gen_act_robin_hood_army():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#14532D", "#166534")
    
    # Green Robin Hood feather & care hands motif
    draw.polygon([(W//2 - 120, 280), (W//2, 60), (W//2 + 40, 280)], fill="#86EFAC")
    draw.line([(W//2 - 40, 180), (W//2 + 20, 180)], fill="#14532D", width=6)
    draw.ellipse([W//2 - 60, 240, W//2 + 60, 360], fill="#BBF7D0")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Robin Hood Army Volunteer Drives",
        subtitle="Zero-Funds Student Movement · Fighting Food Waste Across 400+ Cities",
        tag="FOOD DRIVE",
        accent="#86EFAC"
    )
    add_poster_vignette(img)
    return img

# 13. Khelo India
def gen_act_khelo_india():
    img = Image.new("RGB", (W, H))
    draw = ImageDraw.Draw(img)
    linear_gradient(draw, W, H, "#7C2D12", "#C2410C")
    
    # Athletic track curves & victory flame
    for r in range(160, 340, 35):
        draw.arc([W//2 - r, 80, W//2 + r, 480], 180, 360, fill="#FFFFFF", width=3)
    # Flame
    draw.polygon([(W//2, 110), (W//2 + 30, 200), (W//2, 240), (W//2 - 30, 200)], fill="#FDE047")
    
    draw_poster_heading(
        img, 60, H - 150,
        title="Khelo India Sports & Athletics",
        subtitle="Grassroots Youth Athletics, University Competitions & Fitness Tracking",
        tag="CAMPUS SPORTS",
        accent="#FDE047"
    )
    add_poster_vignette(img)
    return img

POSTER_GENERATORS = {
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
    "act_robin_hood_army": gen_act_robin_hood_army,
    "act_khelo_india": gen_act_khelo_india,
}

def main():
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    print(f"Generating {len(POSTER_GENERATORS)} attractive poster-style assets into {OUT_DIR}...")
    for key, fn in POSTER_GENERATORS.items():
        img = fn()
        dest = OUT_DIR / f"{key}.webp"
        img.save(dest, "WEBP", quality=90, method=6)
        kb = dest.stat().st_size / 1024
        print(f"Saved {dest.name:28s} {W}x{H} ({kb:.1f} KB)")
    print("All poster assets generated successfully!")

if __name__ == "__main__":
    main()
