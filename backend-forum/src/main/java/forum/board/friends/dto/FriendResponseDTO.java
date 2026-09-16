package forum.board.friends.dto;

import java.util.UUID;

public record FriendResponseDTO(

        UUID userId
        
        ,String account
        
        ,String displayName

        ,String Photo

) {
}
