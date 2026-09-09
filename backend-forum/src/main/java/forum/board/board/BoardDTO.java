package forum.board.board;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class BoardDTO {
    // 看板 ID
    private Integer id;

    // 看板名稱
    private String name;

    // 最新文章 ID，沒有文章時為 null
    private Integer latestPostId;

    // 最新文章標題，沒有文章時為 null
    private String latestPostTitle;
}
