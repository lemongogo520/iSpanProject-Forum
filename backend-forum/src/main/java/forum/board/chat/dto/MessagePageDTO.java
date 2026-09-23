package forum.board.chat.dto;

import java.util.List;

public record MessagePageDTO(
    List<MessageResponseDTO> items, 
    boolean hasMore, 
    Long nextCursor
) {}
