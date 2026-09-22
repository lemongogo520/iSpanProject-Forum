package forum.board.forumtemplate;

import java.util.List;

public record CategoryTemplateDto(
    String name,
    Integer rank,
    List<BoardTemplateDto> boards
) {
}
