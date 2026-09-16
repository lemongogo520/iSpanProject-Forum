package forum.board.chat.dto;

import java.time.Instant;
import java.util.List;

import forum.board.chat.conversation.ConversationEntity.ConversationType;

public record ConversationResponseDTO(
    long id,
    ConversationType type,
    String lastMessage,
    Instant createTime,
    Instant updateTime
    // List<ConversationMemberResponseDTO> members
) {}
