package forum.board.friends.dto;

import java.util.UUID;

public record FriendResponseDTO(

        UUID userId,

        String username

        // String displayName,

        // String avatarUrl

) {
}
