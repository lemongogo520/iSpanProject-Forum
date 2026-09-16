--帳號 test1~test17
--密碼 aa1234

DECLARE @i INT =1 ;
WHILE @i<18
BEGIN
    DECLARE @UserId UNIQUEIDENTIFIER =
        CAST(
            '00000000-0000-4000-8000-' +
            RIGHT('000000000000' + CAST(@i AS VARCHAR(12)), 12)
            AS UNIQUEIDENTIFIER
        );

    DECLARE @Account VARCHAR(50) = 'test' + CAST(@i AS VARCHAR);
    DECLARE @Name NVARCHAR(50) = N'測試' + CAST(@i AS NVARCHAR);
    --DECLARE @Name = name 

    DECLARE @Creator UNIQUEIDENTIFIER = '803BB44C-4F12-4056-8733-9D254162CCAE';

    -- users
    INSERT INTO users
    VALUES (
       @UserId,
       @Account,
       1,
       '0:0:0:0:0:0:0:1',
       SYSDATETIMEOFFSET(),
       @Creator,
       NULL,
       NULL,
       NULL,
       '$2a$10$PAePRg5s7CpOur2DN7YnGuUhjGT8KVzEl1iWEJHbAzfgqvMkEctgm',
       0
    );

    -- user_detail
    INSERT INTO user_detail
       ([name], [user_id], [create_time], [creator])
    VALUES (
       @Name,
       @UserId,
       SYSDATETIMEOFFSET(),
       @Creator
    );

    -- user_permission
    INSERT INTO user_permission
        ([user_id], [permission_id], [create_time], [creator], [create_ip])
    VALUES (
        @UserId,
        (SELECT id FROM permissions WHERE [name] = 'user'),
        SYSDATETIMEOFFSET(),
        @Creator,
        '127.0.0.1'
    );
    SET @i = @i+1;
END;





