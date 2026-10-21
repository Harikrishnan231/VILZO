import glob
import re

for f in glob.glob('app/src/main/res/values*/strings.xml'):
    with open(f, 'r', encoding='utf-8') as fp:
        content = fp.read()
    new_content = re.sub(r'<string name="app_name">.*?</string>', '<string name="app_name">VILZO</string>', content)
    new_content = re.sub(r'<string name="app_tagline">.*?</string>', '<string name="app_tagline">Vibrant India Zero Overdose</string>', new_content)
    with open(f, 'w', encoding='utf-8') as fp:
        fp.write(new_content)
    print('Updated', f)
