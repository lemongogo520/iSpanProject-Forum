package forum.board.chat.dto;

public record MessageRequestDTO(
    Long conversationId,
    String content
) {}
