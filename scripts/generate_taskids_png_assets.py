"""Rasterize TASKIDS's built-in UI motifs into PNG resources (Pillow required)."""
from math import cos, pi, sin
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parents[1] / 'app/src/main/res/drawable-nodpi'
SIZE = 256

def art(name, painter):
    image = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    d = ImageDraw.Draw(image)
    painter(d)
    image.save(ROOT / f'game_{name}.png', optimize=True)

def star(d, x, y, outer, fill, outline=None):
    pts = []
    for i in range(10):
        a = -pi/2 + i*pi/5
        r = outer if i % 2 == 0 else outer * .48
        pts.append((x+cos(a)*r, y+sin(a)*r))
    d.polygon(pts, fill=fill)
    if outline: d.line(pts+[pts[0]], fill=outline, width=5, joint='curve')

art('book', lambda d: (d.rounded_rectangle((35,73,222,202),22,fill='#FF65A8',outline='#E94783',width=8), d.polygon([(128,82),(128,193),(52,178),(52,69)],fill='#FFFFFF'), d.polygon([(128,82),(128,193),(205,178),(205,69)],fill='#FFF8DF'), d.line((128,80,128,195),fill='#E94884',width=8),star(d,128,136,28,'#FFC82B')))
art('bath', lambda d: (d.ellipse((49,102,207,179),fill='#BDEAFF'),d.rounded_rectangle((28,125,226,201),26,fill='#358BFC',outline='#1968D7',width=7),d.ellipse((52,147,201,182),fill='#D4F3FF'),d.ellipse((62,57,95,90),fill='#8AD9FF'),d.ellipse((125,41,150,66),fill='#8AD9FF'),d.ellipse((179,70,202,93),fill='#8AD9FF')))
art('meal', lambda d: (d.ellipse((55,46,205,211),fill='#FFFFFF',outline='#24B987',width=11),d.ellipse((87,78,174,177),fill='#FFF0B6',outline='#FFC52D',width=5),d.line((30,56,30,204),fill='#2667D6',width=12),d.line((216,56,216,204),fill='#2667D6',width=12),d.line((14,57,47,57),fill='#2667D6',width=8)))
art('homework', lambda d: (d.rounded_rectangle((56,26,195,216),17,fill='#70BAFF',outline='#2868E9',width=7),d.rounded_rectangle((78,43,180,202),7,fill='#FFFFFF'),*[d.line((95,y,168,y),fill='#91B5E8',width=6) for y in (78,103,128,153)],d.polygon([(189,61),(212,75),(148,199),(133,209),(136,188)],fill='#FFC52D'),d.polygon([(133,209),(136,188),(148,199)],fill='#253C5F')))
art('tooth', lambda d: (d.ellipse((49,29,207,120),fill='#DDF6FF',outline='#289EF5',width=7),d.polygon([(53,78),(66,146),(80,209),(103,224),(127,175),(150,224),(174,209),(193,146),(202,78)],fill='#FFFFFF'),d.line([(53,78),(66,146),(80,209),(103,224),(127,175),(150,224),(174,209),(193,146),(202,78)],fill='#289EF5',width=7,joint='curve'),d.ellipse((91,91,100,100),fill='#253C5F'),d.ellipse((150,91,159,100),fill='#253C5F')))
art('toys', lambda d: (d.rounded_rectangle((34,105,115,215),15,fill='#FF65A8',outline='#DF4183',width=6),d.rounded_rectangle((119,66,226,205),15,fill='#51CFA5',outline='#19A77C',width=6),d.rounded_rectangle((50,51,115,111),12,fill='#FFC83D',outline='#E9A81E',width=5),star(d,170,135,35,'#FFDB36')))
art('clean', lambda d: (d.line((73,36,157,169),fill='#965A32',width=16),d.rounded_rectangle((116,152,220,216),17,fill='#FFD552',outline='#E9A622',width=7),d.line((127,193,213,193),fill='#E9A622',width=5),star(d,63,85,22,'#51CFA5')))
art('sport', lambda d: (d.ellipse((39,38,215,214),fill='#FFFFFF',outline='#2776E9',width=10),d.arc((64,64,192,192),35,300,fill='#2776E9',width=12),d.line((126,47,126,206),fill='#2776E9',width=9),d.arc((49,70,203,181),190,350,fill='#F34F78',width=9)))
art('generic', lambda d: (d.rounded_rectangle((43,43,213,213),40,fill='#DEEFFF'),star(d,128,127,78,'#FFC82B','#EF9A1E')))
art('badge', lambda d: (d.ellipse((20,20,236,236),fill='#FFFFFF'),d.ellipse((32,32,224,224),fill='#FFCA33'),star(d,128,128,72,'#FFFFFF')))

confetti = Image.new('RGBA',(1080,720),(0,0,0,0))
d = ImageDraw.Draw(confetti)
colors=['#FFC82B','#FF65A8','#2BCB91','#3197FA','#8A49F1']
for i in range(68):
    x=(i*157+93)%1080; y=(i*271+49)%720
    if i%3 == 0: star(d,x,y,8,colors[i%5])
    else: d.ellipse((x-5,y-5,x+5,y+5),fill=colors[i%5])
confetti.save(ROOT/'game_confetti.png',optimize=True)

# Navigation pictograms share one raster source across phone and parent screens.
def nav(name, painter):
    image = Image.new('RGBA', (96,96), (0,0,0,0))
    painter(ImageDraw.Draw(image))
    image.save(ROOT / f'nav_{name}.png', optimize=True)

nav('home', lambda d: (d.polygon([(9,43),(48,9),(87,43),(80,51),(78,82),(57,82),(57,58),(38,58),(38,82),(18,82),(16,51)], fill='#1762DA')))
nav('missions', lambda d: (d.ellipse((11,11,85,85),outline='#1762DA',width=10),d.line([(29,48),(43,61),(68,34)],fill='#1762DA',width=10,joint='curve')))
nav('star', lambda d: star(d,48,48,40,'#FFC52D','#EA9822'))
nav('more', lambda d: [d.ellipse((x-7,41,x+7,55),fill='#1762DA') for x in (22,48,74)])
