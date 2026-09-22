package forum.board.friends.post;

import java.time.Instant;

/**
 * PostSummaryDTO
 */
public record PostSummaryDTO(
        Integer id,
        String title,
        String excerpt,
        String thumbnailUrl, // 內文第一張圖，沒有就是 null
        String postType,
        Integer boardId,
        String boardName,
        Instant createTime

) {
}
