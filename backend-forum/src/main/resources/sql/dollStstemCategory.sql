INSERT INTO [dbo].[doll_categories] (code, is_required, type_name, z_index, created_at, updated_at)
VALUES
('body', 1, '身體', 10, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('head', 1, '頭部', 20, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('legs', 0, '下裝', 30, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('clothes', 0, '上衣', 40, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('hair', 0, '頭髮', 50, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('hats', 0, '帽子', 60, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()),
('shoes', 0, '鞋子', 70, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET());