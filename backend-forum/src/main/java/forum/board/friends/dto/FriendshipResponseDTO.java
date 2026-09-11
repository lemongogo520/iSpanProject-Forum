package forum.board.friends.dto;

import java.util.UUID;

import forum.board.friends.FriendshipEntity.FriendshipStatus;

public record FriendshipResponseDTO(

        Long id

        , FriendshipStatus status

        , FriendResponseDTO friend
) {
}
