package forum.board.forumtemplate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record AiTemplateRequest(

        @NotBlank(message = "請輸入論壇需求")
        @Size(max = 300, message = "論壇需求不能超過 300 字")
        String description,

        @NotNull(message = "請選擇分類數量")
        @Min(value = 1, message = "至少需要 1 個分類")
        @Max(value = 5, message = "最多只能產生 5 個分類")
        Integer categoryCount,

        @NotNull(message = "請選擇討論版數量")
        @Min(value = 1, message = "每個分類至少需要 1 個討論版")
        @Max(value = 6, message = "每個分類最多只能產生 6 個討論版")
        Integer boardsPerCategory

) {
}