/* 初始化 admin，密碼 123456 */
IF NOT EXISTS (SELECT 1 FROM users WHERE account = 'admin')
BEGIN
    INSERT INTO [dbo].[users]
        ([id], [account], [password], [active], [reset],
         [create_time], [creator], [create_ip])
    VALUES
        ('803BB44C-4F12-4056-8733-9D254162CCAE',
         'admin',
         '$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
         1, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化 admin 詳細資料 */
IF NOT EXISTS (
    SELECT 1 FROM user_detail
    WHERE user_id = '803BB44C-4F12-4056-8733-9D254162CCAE'
)
BEGIN
    INSERT INTO [dbo].[user_detail]
        ([user_id], [name], [nick_name], [show_nick_name],
         [birthday], [show_birthday], [email], [show_email],
         [photo], [show_photo], [create_time], [creator], [create_ip])
    VALUES
        ('803BB44C-4F12-4056-8733-9D254162CCAE',
         N'系統管理員', NULL, 0,
         NULL, 0, NULL, 0,
         NULL, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化系統管理員權限 */
IF NOT EXISTS (SELECT 1 FROM permissions WHERE [name] = 'systemadmin')
BEGIN
    INSERT INTO [dbo].[permissions]
        ([name], [rank], [create_time], [creator], [create_ip])
    VALUES
        ('systemadmin', 1, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化討論版管理員權限 */
IF NOT EXISTS (SELECT 1 FROM permissions WHERE [name] = 'manager')
BEGIN
    INSERT INTO [dbo].[permissions]
        ([name], [rank], [create_time], [creator], [create_ip])
    VALUES
        ('manager', 2, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化一般使用者權限 */
IF NOT EXISTS (SELECT 1 FROM permissions WHERE [name] = 'user')
BEGIN
    INSERT INTO [dbo].[permissions]
        ([name], [rank], [create_time], [creator], [create_ip])
    VALUES
        ('user', 3, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 設定 admin 為系統管理員 */
IF NOT EXISTS (
    SELECT 1
    FROM user_permission up
    INNER JOIN permissions p ON p.id = up.permission_id
    WHERE up.user_id = '803BB44C-4F12-4056-8733-9D254162CCAE'
      AND p.[name] = 'systemadmin'
)
BEGIN
    INSERT INTO [dbo].[user_permission]
        ([user_id], [permission_id], [create_time], [creator], [create_ip])
    VALUES
        ('803BB44C-4F12-4056-8733-9D254162CCAE',
         (SELECT id FROM permissions WHERE [name] = 'systemadmin'),
         SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化討論版管理員 manager01，密碼 123456 */
IF NOT EXISTS (SELECT 1 FROM users WHERE account = 'manager01')
BEGIN
    INSERT INTO [dbo].[users]
        ([id], [account], [password], [active], [reset],
         [create_time], [creator], [create_ip])
    VALUES
        ('11111111-1111-4111-8111-111111111111',
         'manager01',
         '$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
         1, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1 FROM user_detail
    WHERE user_id = '11111111-1111-4111-8111-111111111111'
)
BEGIN
    INSERT INTO [dbo].[user_detail]
        ([user_id], [name], [nick_name], [show_nick_name],
         [birthday], [show_birthday], [email], [show_email],
         [photo], [show_photo], [create_time], [creator], [create_ip])
    VALUES
        ('11111111-1111-4111-8111-111111111111',
         N'討論版管理員', NULL, 0,
         NULL, 0, NULL, 0,
         NULL, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1
    FROM user_permission up
    INNER JOIN permissions p ON p.id = up.permission_id
    WHERE up.user_id = '11111111-1111-4111-8111-111111111111'
      AND p.[name] = 'manager'
)
BEGIN
    INSERT INTO [dbo].[user_permission]
        ([user_id], [permission_id], [create_time], [creator], [create_ip])
    VALUES
        ('11111111-1111-4111-8111-111111111111',
         (SELECT id FROM permissions WHERE [name] = 'manager'),
         SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化一般使用者 user001，密碼 123456 */
IF NOT EXISTS (SELECT 1 FROM users WHERE account = 'user001')
BEGIN
    INSERT INTO [dbo].[users]
        ([id], [account], [password], [active], [reset],
         [create_time], [creator], [create_ip])
    VALUES
        ('22222222-2222-4222-8222-222222222222',
         'user001',
         '$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
         1, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1 FROM user_detail
    WHERE user_id = '22222222-2222-4222-8222-222222222222'
)
BEGIN
    INSERT INTO [dbo].[user_detail]
        ([user_id], [name], [nick_name], [show_nick_name],
         [birthday], [show_birthday], [email], [show_email],
         [photo], [show_photo], [create_time], [creator], [create_ip])
    VALUES
        ('22222222-2222-4222-8222-222222222222',
         N'一般使用者一', NULL, 0,
         NULL, 0, NULL, 0,
         NULL, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1
    FROM user_permission up
    INNER JOIN permissions p ON p.id = up.permission_id
    WHERE up.user_id = '22222222-2222-4222-8222-222222222222'
      AND p.[name] = 'user'
)
BEGIN
    INSERT INTO [dbo].[user_permission]
        ([user_id], [permission_id], [create_time], [creator], [create_ip])
    VALUES
        ('22222222-2222-4222-8222-222222222222',
         (SELECT id FROM permissions WHERE [name] = 'user'),
         SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化一般使用者 user002，密碼 123456 */
IF NOT EXISTS (SELECT 1 FROM users WHERE account = 'user002')
BEGIN
    INSERT INTO [dbo].[users]
        ([id], [account], [password], [active], [reset],
         [create_time], [creator], [create_ip])
    VALUES
        ('33333333-3333-4333-8333-333333333333',
         'user002',
         '$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
         1, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1 FROM user_detail
    WHERE user_id = '33333333-3333-4333-8333-333333333333'
)
BEGIN
    INSERT INTO [dbo].[user_detail]
        ([user_id], [name], [nick_name], [show_nick_name],
         [birthday], [show_birthday], [email], [show_email],
         [photo], [show_photo], [create_time], [creator], [create_ip])
    VALUES
        ('33333333-3333-4333-8333-333333333333',
         N'一般使用者二', NULL, 0,
         NULL, 0, NULL, 0,
         NULL, 0, SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
IF NOT EXISTS (
    SELECT 1
    FROM user_permission up
    INNER JOIN permissions p ON p.id = up.permission_id
    WHERE up.user_id = '33333333-3333-4333-8333-333333333333'
      AND p.[name] = 'user'
)
BEGIN
    INSERT INTO [dbo].[user_permission]
        ([user_id], [permission_id], [create_time], [creator], [create_ip])
    VALUES
        ('33333333-3333-4333-8333-333333333333',
         (SELECT id FROM permissions WHERE [name] = 'user'),
         SYSDATETIMEOFFSET(),
         '803BB44C-4F12-4056-8733-9D254162CCAE',
         '127.0.0.1')
END;
/* 初始化網站設定 */
IF NOT EXISTS (
    SELECT 1 FROM config_setting
    WHERE [item] = 'web_site_name'
)
BEGIN
    INSERT INTO [dbo].[config_setting]
        ([item], [value], [comment])
    VALUES
        ('web_site_name', N'iSpan論壇', N'網站名稱'),
        ('web_site_description', N'這是一個iSpan論壇', N'網站描述'),
        ('login_logo', N'', N'登入頁LOGO'),
        ('token_active', N'30', N'token有效日期'),
        ('allow_register', N'true', N'開放會員註冊'),
        ('account_min_length', N'6', N'帳號最小長度'),
        ('account_max_length', N'20', N'帳號最大長度'),
        ('password_min_length', N'6', N'密碼最小長度'),
        ('allow_change_password', N'true', N'是否允許修改密碼'),
        ('post_paper_page', N'10', N'每頁文章數'),
        ('comment_paper_page', N'5', N'每頁留言數'),
        ('allow_edit_post', N'true', N'是否允許編輯文章'),
        ('edit_post_days', N'3', N'允許編輯文章日期限制'),
        ('allow_delete_post', N'false', N'是否允許刪除')
END;