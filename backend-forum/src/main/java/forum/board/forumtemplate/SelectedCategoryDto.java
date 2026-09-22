package forum.board.forumtemplate;

import java.util.List;

public record SelectedCategoryDto(String categoryName,List<String> boardNames) {}
