package forum.board.friends.dto;

import java.time.Instant;
import java.util.UUID;

import forum.board.friends.FriendshipEntity.FriendshipStatus;

public record FriendshipResponseDTO(

        Long id,

        UUID requesterId,

        String requesterName,

        UUID addresseeId,

        String addresseeName,

        FriendshipStatus status,

        Instant createTime

) {
}
