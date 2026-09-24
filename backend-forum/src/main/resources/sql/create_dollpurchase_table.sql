-- 在專案使用的 SQL Server 資料庫執行；不修改舊 shop_order。
IF OBJECT_ID(N'dbo.doll_purchase_order', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.doll_purchase_order (
        id uniqueidentifier NOT NULL PRIMARY KEY,
        user_id uniqueidentifier NOT NULL,
        item_id int NOT NULL,
        item_name nvarchar(100) NOT NULL,
        price bigint NOT NULL,
        created_at datetimeoffset(7) NOT NULL,
        CONSTRAINT uq_doll_purchase_user_item UNIQUE (user_id, item_id),
        CONSTRAINT ck_doll_purchase_price CHECK (price >= 0),
        CONSTRAINT fk_doll_purchase_user FOREIGN KEY (user_id) REFERENCES dbo.users(id),
        CONSTRAINT fk_doll_purchase_item FOREIGN KEY (item_id) REFERENCES dbo.doll_items(id)
    );
END;
