package forum.board.friends.dto;

import forum.board.friends.FriendshipEntity.FriendshipStatus;

public record FriendshipResponseDTO(

        Long id

        , FriendshipStatus status

        , FriendResponseDTO friend
) {
}
