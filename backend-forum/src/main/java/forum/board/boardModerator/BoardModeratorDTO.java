package forum.board.boardModerator;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class BoardModeratorDTO {

    private Integer id;

    private Integer boardId;

    private String boardName;

    private UUID userId;

    private String account;

    private Instant createTime;

    private UUID creatorId;
    
    private String creatorAccount;
}
