import os
from PIL import Image

def process():
    logo_path = 'files/logo.jpeg'
    im = Image.open(logo_path).convert('RGBA')
    w, h = im.size

    # Save full logo as vilzo_logo.webp
    os.makedirs('app/src/main/res/drawable-nodpi', exist_ok=True)
    im.save('app/src/main/res/drawable-nodpi/vilzo_logo.webp', 'WEBP', quality=95)
    print("Saved vilzo_logo.webp")

    # Crop the central circular mark:
    # Logo has VZ inside circle roughly from y=100 to y=800, x=150 to x=1100
    # Let's crop mark (circle + VZ + anti-drug stamp)
    mark_box = (150, 80, 1104, 800)
    mark_im = im.crop(mark_box)
    
    # Make a square padded mark
    mw, mh = mark_im.size
    side = max(mw, mh) + 60
    square_mark = Image.new('RGBA', (side, side), (253, 252, 247, 255))
    square_mark.paste(mark_im, ((side - mw) // 2, (side - mh) // 2))
    square_mark.save('app/src/main/res/drawable-nodpi/vilzo_logo_mark.webp', 'WEBP', quality=95)
    print("Saved vilzo_logo_mark.webp")

    # Mipmap densities
    densities = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192,
    }

    for folder, size in densities.items():
        dir_path = os.path.join('app/src/main/res', folder)
        os.makedirs(dir_path, exist_ok=True)
        # Resample with Lanczos
        resized = square_mark.resize((size, size), Image.Resampling.LANCZOS)
        resized.save(os.path.join(dir_path, 'ic_launcher.png'), 'PNG')
        resized.save(os.path.join(dir_path, 'ic_launcher_round.png'), 'PNG')
        print(f"Generated {folder} ic_launcher.png ({size}x{size})")

if __name__ == '__main__':
    process()
