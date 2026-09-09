package forum.board.dollsystemitem;

import lombok.Data;

@Data
public class DollItemDTO {
    private  Integer categoryId;

    private  String name;

    private  String filePath;

    private Boolean isActive;
}
