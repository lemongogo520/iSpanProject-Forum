package forum.board.boardModerator;

import java.util.UUID;

import lombok.Data;

@Data 
public class AddBoardModeratorRequest {

    private UUID userId;
}
