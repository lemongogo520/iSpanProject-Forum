/* ---------- 1. users ---------- */
WITH seed AS (
    SELECT n,
           CAST('44444444-2222-4222-8222-' +
                RIGHT('000000000000' + CAST(n AS varchar(12)), 12)
                AS uniqueidentifier) AS uid,
           'test' + CAST(n AS varchar(10)) AS account
    FROM (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15)) AS v(n)
)
INSERT INTO [dbo].[users]
    ([id], [account], [password], [active], [reset], [create_time], [creator], [create_ip])
SELECT s.uid, s.account,
       '$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
       1, 0, SYSDATETIMEOFFSET(),
       '803BB44C-4F12-4056-8733-9D254162CCAE', '127.0.0.1'
FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.account = s.account);


/* ---------- 2. user_detail ---------- */
WITH seed AS (
    SELECT n, name,
           CAST('44444444-2222-4222-8222-' +
                RIGHT('000000000000' + CAST(n AS varchar(12)), 12)
                AS uniqueidentifier) AS uid,
           'test' + CAST(n AS varchar(10)) + '@example.com' AS email
    FROM (VALUES
        (1,N'小明'),(2,N'阿華'),(3,N'美玲'),(4,N'志豪'),(5,N'怡君'),
        (6,N'建宏'),(7,N'雅婷'),(8,N'俊傑'),(9,N'淑芬'),(10,N'家豪'),
        (11,N'曉薇'),(12,N'宗翰'),(13,N'佩蓉'),(14,N'柏翰'),(15,N'欣怡')
    ) AS v(n, name)
)
INSERT INTO [dbo].[user_detail]
    ([user_id], [name], [nick_name], [show_nick_name],
     [birthday], [show_birthday], [email], [show_email],
     [photo], [show_photo], [create_time], [creator], [create_ip])
SELECT s.uid, s.name, NULL, 0,
       DATEADD(DAY, ABS(CHECKSUM(NEWID())) % 7300, '1985-01-01'),
       ABS(CHECKSUM(NEWID())) % 2,
       s.email,
       ABS(CHECKSUM(NEWID())) % 2,
       NULL, 0, SYSDATETIMEOFFSET(),
       '803BB44C-4F12-4056-8733-9D254162CCAE', '127.0.0.1'
FROM seed s
WHERE NOT EXISTS (SELECT 1 FROM user_detail d WHERE d.user_id = s.uid);


/* ---------- 3. user_permission ---------- */
WITH seed AS (
    SELECT CAST('44444444-2222-4222-8222-' +
                RIGHT('000000000000' + CAST(n AS varchar(12)), 12)
                AS uniqueidentifier) AS uid
    FROM (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15)) AS v(n)
)
INSERT INTO [dbo].[user_permission]
    ([user_id], [permission_id], [create_time], [creator], [create_ip])
SELECT s.uid, p.id, SYSDATETIMEOFFSET(),
       '803BB44C-4F12-4056-8733-9D254162CCAE', '127.0.0.1'
FROM seed s
CROSS JOIN (SELECT id FROM permissions WHERE [name] = 'user') p
WHERE NOT EXISTS (
    SELECT 1 FROM user_permission up WHERE up.user_id = s.uid AND up.permission_id = p.id
);


/* ---------- 4. friendship（前 10 個為好友）(user001) ---------- */
WITH seed AS (
    SELECT CAST('44444444-2222-4222-8222-' +
                RIGHT('000000000000' + CAST(n AS varchar(12)), 12)
                AS uniqueidentifier) AS uid
    FROM (VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS v(n)
)
INSERT INTO [dbo].[friendship]
    ([user1_id], [user2_id], [status], [requester_id], [create_time], [update_time])
SELECT '22222222-2222-4222-8222-222222222222', s.uid, 'ACCEPTED',
       '22222222-2222-4222-8222-222222222222',
       SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()
FROM seed s
WHERE NOT EXISTS (
    SELECT 1 FROM friendship f
    WHERE (f.user1_id = '22222222-2222-4222-8222-222222222222' AND f.user2_id = s.uid)
       OR (f.user1_id = s.uid AND f.user2_id = '22222222-2222-4222-8222-222222222222')
);

/* ---------- 4. friendship（11-13 個為好友邀請） (user001)---------- */
WITH seed AS (
    SELECT CAST('44444444-2222-4222-8222-' +
                RIGHT('000000000000' + CAST(n AS varchar(12)), 12)
                AS uniqueidentifier) AS uid
    FROM (VALUES (11),(12),(13)) AS v(n)
)
INSERT INTO [dbo].[friendship]
    ([user1_id], [user2_id], [status], [requester_id], [create_time], [update_time])
SELECT '22222222-2222-4222-8222-222222222222', s.uid, 'PENDING',
       s.uid,
       SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()
FROM seed s
WHERE NOT EXISTS (
    SELECT 1 FROM friendship f
    WHERE (f.user1_id = '22222222-2222-4222-8222-222222222222' AND f.user2_id = s.uid)
       OR (f.user1_id = s.uid AND f.user2_id = '22222222-2222-4222-8222-222222222222')
);
-- 裝備
DELETE FROM [dbo].[doll_user_equip]
WHERE user_id IN (SELECT id FROM users WHERE account LIKE 'test%');

DELETE FROM [dbo].[doll_user_owned]
WHERE user_id IN (SELECT id FROM users WHERE account LIKE 'test%');

/* 1. 每人每個分類隨機獲得一件 */
INSERT INTO [dbo].[doll_user_owned] (user_id, item_id, acquired_at)
SELECT u.id, pick.item_id, SYSDATETIMEOFFSET()
FROM [dbo].[users] u
CROSS JOIN [dbo].[doll_categories] c
CROSS APPLY (
    SELECT TOP 1 i.id AS item_id
    FROM [dbo].[doll_items] i
    WHERE i.category_id = c.id AND i.is_active = 1
    ORDER BY CHECKSUM(CONVERT(varchar(36), u.id) + CONVERT(varchar(10), i.id))
) pick
WHERE u.account LIKE 'test%'
  AND NOT EXISTS (
      SELECT 1 FROM [dbo].[doll_user_owned] o
      WHERE o.user_id = u.id AND o.item_id = pick.item_id
  );


/* 2. 穿上：必備部位一定穿，其他依 id 雜湊決定（約七成） */
INSERT INTO [dbo].[doll_user_equip] (user_id, category_id, item_id, created_at, updated_at)
SELECT o.user_id, i.category_id, o.item_id, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()
FROM [dbo].[doll_user_owned] o
JOIN [dbo].[doll_items] i      ON i.id = o.item_id
JOIN [dbo].[doll_categories] c ON c.id = i.category_id
JOIN [dbo].[users] u           ON u.id = o.user_id
WHERE u.account LIKE 'test%'
  AND (
      c.is_required = 1
      OR ABS(CHECKSUM(CONVERT(varchar(36), u.id) + c.code)) % 10 < 7
  )
  AND NOT EXISTS (
      SELECT 1 FROM [dbo].[doll_user_equip] e
      WHERE e.user_id = o.user_id AND e.category_id = i.category_id
  );
