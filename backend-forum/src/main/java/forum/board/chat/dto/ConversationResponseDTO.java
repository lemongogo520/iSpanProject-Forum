package forum.board.chat.dto;

import java.time.Instant;
import java.util.UUID;

import forum.board.chat.conversation.ConversationEntity.ConversationType;

public record ConversationResponseDTO(
    long id,
    ConversationType type,
    String title,          // DIRECT → 對方名字；BOARD → 看板名       
    UUID targetUserId,     // DIRECT 才有
    Integer boardId,       // BOARD  才有
    boolean subscribed,    // BOARD  才有
    String lastMessage,
    Instant createTime,
    Instant updateTime,
    Long unreadCount
) {}
