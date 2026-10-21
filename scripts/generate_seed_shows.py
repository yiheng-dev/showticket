# -*- coding: utf-8 -*-
"""生成演出票务平台的 216 条种子数据（54 个作品 x 4 个城市）。"""
from datetime import datetime, timedelta

WORKS = [
    ("周杰伦「嘉年华」世界巡回演唱会", "演唱会", 680),
    ("五月天「回到那一天」巡回演唱会", "演唱会", 580),
    ("陈奕迅 FEAR AND DREAMS 巡回演唱会", "演唱会", 680),
    ("张学友「60+」巡回演唱会", "演唱会", 780),
    ("林俊杰「JJ20」世界巡回演唱会", "演唱会", 680),
    ("邓紫棋「I AM GLORIA」巡回演唱会", "演唱会", 580),
    ("薛之谦「天外来物」巡回演唱会", "演唱会", 480),
    ("李荣浩「纵横四海」巡回演唱会", "演唱会", 480),
    ("毛不易「幼鸟指南」演唱会", "演唱会", 380),
    ("华晨宇「火星演唱会」", "演唱会", 580),
    ("张杰「未·LIVE」巡回演唱会", "演唱会", 480),
    ("孙燕姿「就在日落以后」演唱会", "演唱会", 580),
    ("梁静茹「当我们谈论爱情」演唱会", "演唱会", 480),
    ("蔡依林「Ugly Beauty」世界巡回演唱会", "演唱会", 680),
    ("音乐剧《剧院魅影》", "音乐剧", 480),
    ("音乐剧《汉密尔顿》", "音乐剧", 580),
    ("音乐剧《悲惨世界》", "音乐剧", 480),
    ("音乐剧《猫》", "音乐剧", 380),
    ("音乐剧《妈妈咪呀》", "音乐剧", 380),
    ("音乐剧《芝加哥》", "音乐剧", 380),
    ("音乐剧《摇滚莫扎特》", "音乐剧", 480),
    ("音乐剧《巴黎圣母院》", "音乐剧", 480),
    ("话剧《暗恋桃花源》", "话剧", 380),
    ("话剧《茶馆》", "话剧", 280),
    ("话剧《雷雨》", "话剧", 280),
    ("话剧《日出》", "话剧", 280),
    ("话剧《四世同堂》", "话剧", 320),
    ("话剧《白鹿原》", "话剧", 380),
    ("话剧《活着》", "话剧", 320),
    ("话剧《恋爱的犀牛》", "话剧", 280),
    ("维也纳新年音乐会", "音乐会", 880),
    ("柏林爱乐乐团交响音乐会", "音乐会", 1280),
    ("久石让·宫崎骏动漫交响音乐会", "音乐会", 580),
    ("坂本龙一作品音乐会", "音乐会", 680),
    ("马克西姆跨界钢琴音乐会", "音乐会", 480),
    ("理查德·克莱德曼钢琴音乐会", "音乐会", 580),
    ("上海交响乐团音乐季", "音乐会", 380),
    ("中国爱乐乐团交响音乐会", "音乐会", 480),
    ("德云社相声专场", "相声", 280),
    ("郭德纲于谦相声专场", "相声", 380),
    ("岳云鹏孙越相声专场", "相声", 380),
    ("嘻哈包袱铺相声专场", "相声", 180),
    ("舞蹈诗剧《只此青绿》", "舞剧", 580),
    ("舞剧《永不消逝的电波》", "舞剧", 480),
    ("舞剧《朱鹮》", "舞剧", 380),
    ("舞剧《孔子》", "舞剧", 380),
    ("芭蕾舞剧《天鹅湖》", "舞剧", 480),
    ("儿童剧《冰雪奇缘》", "儿童剧", 260),
    ("儿童剧《小王子》", "儿童剧", 220),
    ("儿童剧《海底小纵队》", "儿童剧", 180),
    ("京剧《贵妃醉酒》", "戏曲", 180),
    ("昆曲《牡丹亭》", "戏曲", 220),
    ("脱口秀大会巡演", "脱口秀", 280),
    ("草莓音乐节", "音乐节", 380),
]

CITIES = [
    ("上海", ("上海大剧院", "上海梅赛德斯-奔驰文化中心")),
    ("北京", ("国家大剧院", "国家体育场（鸟巢）")),
    ("广州", ("广州大剧院", "宝能国际体育演艺中心")),
    ("深圳", ("深圳保利剧院", "深圳湾体育中心")),
    ("杭州", ("杭州大剧院", "黄龙体育中心")),
    ("成都", ("成都城市音乐厅", "凤凰山体育公园")),
    ("武汉", ("琴台大剧院", "武汉体育中心")),
    ("南京", ("江苏大剧院", "南京奥体中心")),
    ("西安", ("陕西大剧院", "西安奥体中心")),
    ("重庆", ("重庆大剧院", "华熙LIVE·鱼洞")),
    ("苏州", ("苏州文化艺术中心", "苏州奥体中心")),
    ("长沙", ("梅溪湖国际文化艺术中心", "贺龙体育中心")),
    ("天津", ("天津大剧院", "天津体育馆")),
    ("郑州", ("河南艺术中心", "郑州奥体中心")),
    ("青岛", ("青岛大剧院", "国信体育馆")),
    ("厦门", ("闽南大戏院", "厦门体育中心")),
]

ARENA_GENRES = {"演唱会", "音乐节", "脱口秀"}
THEATER_TICKETS = [("楼座C区", 0.8), ("楼座B区", 1.0), ("池座A区", 1.4), ("池座VIP", 2.0)]
ARENA_TICKETS = [("看台C区", 0.8), ("看台B区", 1.0), ("内场A区", 1.4), ("内场VIP", 2.0)]
CHILD_TICKETS = [("普通票", 0.8), ("亲子票", 1.0), ("前排互动票", 1.4), ("VIP家庭票", 2.0)]
OPERA_TICKETS = [("楼座", 0.8), ("普通票", 1.0), ("前排", 1.4), ("VIP", 2.0)]


def ticket_types(genre):
    if genre == "儿童剧":
        return CHILD_TICKETS
    if genre == "戏曲":
        return OPERA_TICKETS
    if genre in ARENA_GENRES:
        return ARENA_TICKETS
    return THEATER_TICKETS


def sql_quote(value):
    return "'" + str(value).replace("'", "''") + "'"


rows = []
show_id = 1
base_day = datetime(2026, 11, 1, 19, 30, 0)
for i, (work, genre, base_price) in enumerate(WORKS):
    for j in range(4):
        city, venues = CITIES[(i * 4 + j) % len(CITIES)]
        venue = venues[1] if genre in ARENA_GENRES else venues[0]
        if genre == "儿童剧":
            show_time = base_day.replace(hour=10, minute=30) + timedelta(days=(i * 7 + j * 41) % 500)
        else:
            hour = 19 if j % 2 == 0 else 20
            show_time = base_day.replace(hour=hour, minute=30) + timedelta(days=(i * 7 + j * 41) % 500)
        ticket_type, multiplier = ticket_types(genre)[(i + j) % 4]
        price = int(round(base_price * multiplier)) * 100
        stock = 50 + ((i * 97 + j * 53) % 1450)
        sold = 200 + ((i * 331 + j * 71) % 9000)
        review_count = 10 + ((i * 53 + j * 17) % 2500)
        is_featured = 1 if (i + j) % 9 == 0 else 0
        rows.append((
            show_id,
            "{} · {}站".format(work, city),
            price,
            stock,
            "/posters/show-{:03d}.jpg".format(show_id),
            genre,
            venue,
            ticket_type,
            show_time.strftime("%Y-%m-%d %H:%M:%S"),
            sold,
            review_count,
            is_featured,
            1,
        ))
        show_id += 1

lines = [
    "-- 演出票务平台 216 条种子数据（由 scripts/generate_seed_shows.py 生成）",
    "-- 注意：会清空 ticket_show.show_info 后重新写入",
    "SET NAMES utf8mb4;",
    "USE `ticket_show`;",
    "TRUNCATE TABLE `show_info`;",
    "INSERT INTO `show_info`",
    "(`id`,`name`,`price`,`stock`,`poster`,`genre`,`venue`,`ticket_type`,`show_time`,`sold`,`review_count`,`is_featured`,`status`) VALUES",
]
values = []
for row in rows:
    values.append(
        "({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})".format(
            row[0], sql_quote(row[1]), row[2], row[3], sql_quote(row[4]), sql_quote(row[5]),
            sql_quote(row[6]), sql_quote(row[7]), sql_quote(row[8]), row[9], row[10], row[11], row[12]
        )
    )
lines.append(",\n".join(values) + ";")
lines.append("ALTER TABLE `show_info` AUTO_INCREMENT = 217;")
lines.append("")

with open("sql/03_seed_shows.sql", "w", encoding="utf-8", newline="\n") as fh:
    fh.write("\n".join(lines))

print("generated {} rows".format(len(rows)))
