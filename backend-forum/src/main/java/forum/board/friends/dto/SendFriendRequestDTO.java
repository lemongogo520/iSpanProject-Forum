package forum.board.friends.dto;

import java.util.UUID;

/**
 * SendFriendRequestDTO
 */
public record SendFriendRequestDTO(
    UUID targetUserId
) {}
