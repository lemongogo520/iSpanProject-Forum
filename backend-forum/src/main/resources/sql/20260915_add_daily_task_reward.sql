USE [iSpan_Project];
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @constraintName sysname;
    DECLARE @definition nvarchar(max);
    DECLARE @sql nvarchar(max);

    IF (
        SELECT COUNT(*)
        FROM sys.check_constraints
        WHERE parent_object_id = OBJECT_ID(N'dbo.coin_transaction')
          AND definition LIKE N'%transaction_type%'
    ) <> 1
    BEGIN
        THROW 50001, N'交易類型約束不是一筆，請先確認，未修改資料庫。', 1;
    END;

    SELECT
        @constraintName = name,
        @definition = definition
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.coin_transaction')
      AND definition LIKE N'%transaction_type%';

    SET @sql =
        N'ALTER TABLE dbo.coin_transaction DROP CONSTRAINT '
        + QUOTENAME(@constraintName) + N';'
        + N'ALTER TABLE dbo.coin_transaction WITH CHECK ADD CONSTRAINT '
        + QUOTENAME(@constraintName)
        + N' CHECK ((' + @definition
        + N') OR [transaction_type] = ''DAILY_TASK_REWARD'');';

    EXEC sys.sp_executesql @sql;

    COMMIT TRANSACTION;
    PRINT N'完成：已允許每日任務獎勵交易。';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;