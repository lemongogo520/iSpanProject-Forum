package forum.board.chat.dto;

import java.time.Instant;

import forum.board.chat.message.MessagesEntity.ChatType;
import forum.board.friends.dto.FriendResponseDTO;

public record MessageResponseDTO(
    Long id,
    Long conversationId,
    FriendResponseDTO sender,
    String content,
    ChatType type,
    Instant createTime,
    String clientMsgId,
    boolean mine
) {}
