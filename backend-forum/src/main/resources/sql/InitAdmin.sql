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
	VALUES('803BB44C-4F12-4056-8733-9D254162CCAE',NULL,NULL,0,NULL,0,NULL,0,NULL,0
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