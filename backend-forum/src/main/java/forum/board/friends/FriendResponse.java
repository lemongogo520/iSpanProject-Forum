package forum.board.friends;

import java.util.UUID;

public record FriendResponse(

        UUID userId,

        String username

        // String displayName,

        // String avatarUrl

) {
}
