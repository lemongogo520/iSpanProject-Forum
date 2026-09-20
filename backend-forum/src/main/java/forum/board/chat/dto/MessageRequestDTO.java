package forum.board.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequestDTO(
    @NotBlank @Size(max = 2000)  String content,
    @Size(max = 36) String clientMsgId
) {}
