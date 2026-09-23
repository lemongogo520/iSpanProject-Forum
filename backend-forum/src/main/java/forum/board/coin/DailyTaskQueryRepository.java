package forum.board.coin;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

/**
 * 每日任務專用的唯讀來源查詢，不修改論壇的 Repository 或 Entity。
 * 原生 SQL 依據 SQL Server 及已提供的三份 Entity 撰寫。
 */
@Repository
@Transactional(readOnly = true)
public class DailyTaskQueryRepository {

    private final EntityManager entityManager;

    public DailyTaskQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public long countPosts(UUID userId, Instant start, Instant end) {
        return count("""
                SELECT COUNT_BIG(*)
                FROM forum_posts p
                WHERE p.user_id = CAST(:userId AS uniqueidentifier)
                  AND p.is_deleted = 0
                  AND p.create_time >= :startTime
                  AND p.create_time < :endTime
                """, userId, start, end);
    }

    public long countComments(UUID userId, Instant start, Instant end) {
        // 主留言、子樓回覆都計算；目前僅依留言本身的刪除狀態判斷。
        return count("""
                SELECT COUNT_BIG(*)
                FROM post_comments c
                WHERE c.user_id = CAST(:userId AS uniqueidentifier)
                  AND c.is_deleted = 0
                  AND c.create_time >= :startTime
                  AND c.create_time < :endTime
                """, userId, start, end);
    }

    public long countGivenLikes(UUID userId, Instant start, Instant end) {
        return count("""
                SELECT COUNT_BIG(*)
                FROM post_like pl
                JOIN forum_posts p ON p.id = pl.post_id
                WHERE pl.user_id = CAST(:userId AS uniqueidentifier)
                  AND p.user_id <> CAST(:userId AS uniqueidentifier)
                  AND p.is_deleted = 0
                  AND pl.created_time >= :startTime
                  AND pl.created_time < :endTime
                """, userId, start, end);
    }

    public long countReceivedLikes(UUID userId, Instant start, Instant end) {
        return count("""
                SELECT COUNT_BIG(*)
                FROM post_like pl
                JOIN forum_posts p ON p.id = pl.post_id
                WHERE p.user_id = CAST(:userId AS uniqueidentifier)
                  AND pl.user_id <> CAST(:userId AS uniqueidentifier)
                  AND p.is_deleted = 0
                  AND pl.created_time >= :startTime
                  AND pl.created_time < :endTime
                """, userId, start, end);
    }

    private long count(String sql, UUID userId, Instant start, Instant end) {
        Number result = (Number) entityManager.createNativeQuery(sql)
                .setParameter("userId", userId.toString())
                .setParameter("startTime", start.atOffset(ZoneOffset.UTC))
                .setParameter("endTime", end.atOffset(ZoneOffset.UTC))
                .getSingleResult();
        return result.longValue();
    }
}
