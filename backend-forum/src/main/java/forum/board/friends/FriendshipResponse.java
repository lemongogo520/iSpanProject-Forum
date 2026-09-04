package forum.board.friends;

import java.time.Instant;
import java.util.UUID;

import forum.board.friends.FriendshipEntity.FriendshipStatus;

public record FriendshipResponse(

        Long id,

        UUID requesterId,

        String requesterName,

        UUID addresseeId,

        String addresseeName,

        FriendshipStatus status,

        Instant createTime

) {
}
