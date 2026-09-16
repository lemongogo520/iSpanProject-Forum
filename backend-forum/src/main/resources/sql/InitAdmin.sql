/* 初始化admin 密碼預設123456 */
IF NOT EXISTS (SELECT 1 FROM users WHERE account = 'admin')
BEGIN
	INSERT INTO [dbo].[users]
           ([id]
           ,[account]
           ,[password]
           ,[active]
           ,[reset]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('803BB44C-4F12-4056-8733-9D254162CCAE','admin'
	,'$2a$10$Mq5veOM9HJRoYmUGHzA.4ugdnqvDNrRvj5a20Xcj4LAciU/K6n5cS',
	1,0,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE'
	,'127.0.0.1')
END;

/* 初始化admin detail*/
IF NOT EXISTS (SELECT 1 FROM user_detail WHERE user_id = '803BB44C-4F12-4056-8733-9D254162CCAE')
BEGIN
	INSERT INTO [dbo].[user_detail]
           ([user_id]
           ,[name]
           ,[nick_name]
           ,[show_nick_name]
           ,[birthday]
           ,[show_birthday]
           ,[email]
           ,[show_email]
           ,[photo]
           ,[show_photo]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('803BB44C-4F12-4056-8733-9D254162CCAE','系統管理員',NULL,0,NULL,0,NULL,0,NULL,0
	,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE'
	,'127.0.0.1')
END;

/* 初始化身分組(系統管理員、管理員、一般使用者) */
IF NOT EXISTS (SELECT 1 FROM permissions WHERE [name] = 'systemadmin')
BEGIN
INSERT INTO [dbo].[permissions]
           ([name]
           ,[rank]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('systemadmin',1,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE','127.0.0.1')
	
INSERT INTO [dbo].[permissions]
           ([name]
           ,[rank]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('manager',2,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE','127.0.0.1')
	
INSERT INTO [dbo].[permissions]
           ([name]
           ,[rank]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('user',3,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE','127.0.0.1')
END;

/* 初始化身分組(系統管理員、管理員、一般使用者) */
IF NOT EXISTS (SELECT 1 FROM user_permission WHERE [user_id] = '803BB44C-4F12-4056-8733-9D254162CCAE')
BEGIN
INSERT INTO [dbo].[user_permission]
           ([user_id]
           ,[permission_id]
           ,[create_time]
           ,[creator]
           ,[create_ip])
	VALUES('803BB44C-4F12-4056-8733-9D254162CCAE',(SELECT id FROM permissions WHERE [name] = 'systemadmin')
	,SYSDATETIMEOFFSET(),'803BB44C-4F12-4056-8733-9D254162CCAE','127.0.0.1')
END;

IF NOT EXISTS (SELECT 1 FROM config_setting WHERE [item] = 'web_site_name')
BEGIN
INSERT INTO [dbo].[config_setting]
           ([item]
           ,[value]
           ,[comment])
 	VALUES
 		('web_site_name', 'iSpan論壇', '網站名稱'),
		('web_site_description', '這是一個iSpan論壇', '網站描述'),
		('login_logo', '', '登入頁LOGO'),
		('token_active', '30', 'token有效日期'),
		
		('allow_register','true','開放會員註冊'),
		('account_min_length','6','帳號最小長度'),
		('account_max_length','20','帳號最大長度'),
		('password_min_length','6','密碼最小長度'),
		('allow_change_password','true','是否允許修改密碼'),
		
		('post_paper_page','10','每頁文章數'),
		('comment_paper_page','5','每頁留言數'),
		('allow_edit_post','true','是否允許編輯文章'),
		('edit_post_days','3','允許編輯文章日期限制'),
		('allow_delete_post','false','是否允許刪除')
END;