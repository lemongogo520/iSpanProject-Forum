IF NOT EXISTS (SELECT 1 FROM [dbo].[doll_categories])
BEGIN
INSERT INTO [dbo].[doll_categories] (code, is_required, type_name, z_index, created_at, updated_at)
VALUES
('body', 1, '身體', 10, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('head', 1, '頭部', 20, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('legs', 0, '下裝', 30, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('clothes', 0, '上衣', 40, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('hair', 0, '頭髮', 50, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('hat', 0, '帽子', 60, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('shoes', 0, '鞋子', 35, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET())
END;

IF NOT EXISTS (SELECT 1 FROM [dbo].[doll_items])
BEGIN
    INSERT INTO [dbo].[doll_items] (category_id, item_name, is_active, file_path, created_at, updated_at, price)
VALUES
(1, '男性身體', 1, 'doll_items_body/9d0a88ff.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '男性頭部', 1, 'doll_items_head/3a44562e.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(1, '女性身體', 1, 'doll_items_body/64f0e14a.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(1, '骷髏身體', 1, 'doll_items_body/e8b31c6d.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(1, '殭屍身體', 1, 'doll_items_body/0f5a94eb.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '殭屍頭部', 1, 'doll_items_head/96cda552.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50),
(2, '女性頭部', 1, 'doll_items_head/edaaf752.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '骷髏頭部', 1,   'doll_items_head/3f8c29a1.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '南瓜頭部', 1,   'doll_items_head/7b4e1f0c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '哥布林頭部', 1, 'doll_items_head/9d2a56b4.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '外星人頭部', 1, 'doll_items_head/c1f73e8a.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(2, '豬人頭部', 1,   'doll_items_head/54a9d20f.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝1', 1, 'doll_items_legs/744ab80c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝2', 1, 'doll_items_legs/1e866e1b.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝3', 1, 'doll_items_legs/5f97012d.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝4', 1, 'doll_items_legs/763186d0.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝5', 1, 'doll_items_legs/c0d89b9c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝6', 1, 'doll_items_legs/f6bb22db.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝7', 1, 'doll_items_legs/a57f5080.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝8', 1, 'doll_items_legs/0ad25383.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝9', 1, 'doll_items_legs/d58d2ab0.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '下裝10', 1, 'doll_items_legs/461f890b.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服1', 1, 'doll_items_clothes/2f1517cb.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服2', 1, 'doll_items_clothes/4a9f2b1c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服3', 1, 'doll_items_clothes/7c3e9a2f.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服4', 1, 'doll_items_clothes/aa2c0642.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服5', 1, 'doll_items_clothes/b2e61427.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服6', 1, 'doll_items_clothes/b7625c48.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服7', 1, 'doll_items_clothes/b7bd3102.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服8', 1, 'doll_items_clothes/dea328c3.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服9', 1, 'doll_items_clothes/e7b4e5b4.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服10', 1, 'doll_items_clothes/f7e9307a.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮1', 1, 'doll_items_hair/0b3dd0f1.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮2', 1, 'doll_items_hair/23072a50.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮3', 1, 'doll_items_hair/553a5213.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮4', 1, 'doll_items_hair/5bac83f6.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮5', 1, 'doll_items_hair/5d2f12f2.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮6', 1, 'doll_items_hair/65fcd5d9.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮7', 1, 'doll_items_hair/6c6ab190.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮8', 1, 'doll_items_hair/74d114ea.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮9', 1, 'doll_items_hair/87c82c4a.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮10', 1, 'doll_items_hair/8bea6988.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮11', 1, 'doll_items_hair/93d89dc5.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮12', 1, 'doll_items_hair/ac4d0669.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮13', 1, 'doll_items_hair/cfb45548.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮14', 1, 'doll_items_hair/dfb98b64.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮15', 1, 'doll_items_hair/e3e7e745.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮16', 1, 'doll_items_hair/fae1bef2.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(6, '帽子1', 1, 'doll_items_hat/0b886519.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(6, '帽子2', 1, 'doll_items_hat/46fc15e9.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(6, '帽子3', 1, 'doll_items_hat/63683836.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(7, '鞋子1', 1, 'doll_items_shoes/ad44466e.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(7, '鞋子2', 1, 'doll_items_shoes/d3f275dc.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50)
END;

-- ==========================================
-- 補發：讓已經存在的使用者獲得 Doll_Items
-- ==========================================
INSERT INTO [dbo].[doll_user_owned] (user_id, item_id, acquired_at)
SELECT u.id, i.id, SYSDATETIMEOFFSET()
FROM [dbo].[users] u
CROSS JOIN [dbo].[doll_items] i
WHERE i.item_name IN ('男性身體', '男性頭部','下裝10','下裝7','衣服1','衣服2','頭髮1','頭髮3')
  AND NOT EXISTS (
    SELECT 1 
    FROM [dbo].[doll_user_owned] duo 
    WHERE duo.user_id = u.id AND duo.item_id = i.id
);