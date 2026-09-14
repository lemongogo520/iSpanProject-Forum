package forum.board.post;

import java.time.Instant;

import lombok.Data;

@Data
public class PostsHistoryDTO {
    private Integer id;
    private Integer postId;
    private String oldTitle;
    private String oldContent;
    private String newTitle;
    private String newContent;
    private String action;
    private Instant modifyTime;
}
