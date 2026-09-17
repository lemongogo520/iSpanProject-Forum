package forum.board.forumtemplate;

import java.util.List;

public record ForumTemplateDto(
    String code,
    String name,
    String description,
    List<CategoryTemplateDto> categories
) {
}
