package forum.board.dollsystemcategories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DollCategoryDTO {

    private Integer id;

    @NotBlank(message = "分類代碼不能為空")
    private String code;

    @NotBlank(message = "分類名稱不能為空")
    private String typeName;

    @NotNull(message = "圖層順序 (Z-Index) 不能為空")
    private Integer zIndex;

    @NotNull(message = "預設圖層 (Is Required) 不能為空")
    private Boolean isRequired;

}
