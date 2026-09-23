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
(2, '殭屍頭部', 1, 'doll_items_head/96cda552.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50),
(2, '女性頭部', 1, 'doll_items_head/edaaf752.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '裙子', 1, 'doll_items_legs/913f7ee1.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '褲子1', 1, 'doll_items_legs/9a2c5f13.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(3, '褲子2', 1, 'doll_items_legs/1f8d4b6e.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50),
(4, '衣服1', 1, 'doll_items_clothes/b7625c48.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服2', 1, 'doll_items_clothes/4a9f2b1c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(4, '衣服3', 1, 'doll_items_clothes/7c3e9a2f.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50),
(5, '頭髮1', 1, 'doll_items_hair/61ef3f3c.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮2', 1, 'doll_items_hair/a0cc2af6.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 0),
(5, '頭髮3', 1, 'doll_items_hair/a1879654.png', SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET(), 50),
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
WHERE i.item_name IN ('男性身體', '男性頭部')
  AND NOT EXISTS (
    SELECT 1 
    FROM [dbo].[doll_user_owned] duo 
    WHERE duo.user_id = u.id AND duo.item_id = i.id
);