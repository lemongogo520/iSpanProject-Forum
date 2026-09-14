package forum.board.chat.dto;

import java.time.Instant;

import forum.board.chat.message.MessagesEntity.ChatType;
import forum.board.friends.dto.FriendResponseDTO;

public record MessageDTO(
    long id,
    long conversationId,
    ChatType type,
    FriendResponseDTO sender,
    String content,
    Instant createTime
) {}
