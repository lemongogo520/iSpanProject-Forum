package forum.board.chat.dto;

import java.time.Instant;

import forum.board.friends.dto.FriendResponseDTO;

public record ConversationMemberResponseDTO(
    long id,
    FriendResponseDTO friend,
    Instant joinTime,
    Instant lastReadTime
) {}
