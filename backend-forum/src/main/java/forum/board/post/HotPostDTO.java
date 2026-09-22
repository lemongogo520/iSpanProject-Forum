package forum.board.post;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotPostDTO {

    private Integer id;
    private String title;
    private UUID userId;
    private String userName;
    private Integer boardId;
    private String boardName;
    private String postType;
    private String coverImageUrl;
    private Integer views;
    private long commentCount;
    private Instant createTime;
    
}