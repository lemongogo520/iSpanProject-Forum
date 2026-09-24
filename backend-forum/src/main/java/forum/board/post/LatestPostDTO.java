package forum.board.post;

import java.time.Instant;

public record  LatestPostDTO( 
        Integer id,
        String title,
        String postType,
        Integer boardId,
        String boardName,
        String userName,
        Instant createTime) {
    
}
