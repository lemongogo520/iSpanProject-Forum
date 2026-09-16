package forum.board.friends.dto;

import java.util.UUID;

/**
 * SendFriendRequestDTO
 */
public record SendFriendDTO(
    UUID targetUserId
) {}
