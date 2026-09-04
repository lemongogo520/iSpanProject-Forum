package forum.board.post;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class PostDTO {
    private Integer id;
    private String title;
    private String content;
    private Integer views;
    private Instant createTime;
    private Instant modifyTime;
    private Boolean isDeleted;
    private String userName;
    private UUID userId;
    private Integer boardId;
    private String postType;
}
