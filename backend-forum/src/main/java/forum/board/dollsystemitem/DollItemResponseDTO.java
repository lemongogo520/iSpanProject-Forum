package forum.board.dollsystemitem;

import forum.board.dollsystemcategories.DollCategoryDTO;
import lombok.Data;

@Data 
public class DollItemResponseDTO {

    private Integer id;
    private String name;
    private String filePath;
    private Boolean isActive;
    private DollCategoryDTO category;

    public static DollItemResponseDTO fromEntity(DollItem item){
        if(item==null){
            return null;
        }

        DollItemResponseDTO dto = new DollItemResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setFilePath(item.getFilePath());
        dto.setIsActive(item.getIsActive());

        if(item.getCategory() != null){
            DollCategoryDTO categoryDTO = new DollCategoryDTO();
            categoryDTO.setId(item.getCategory().getId());
            categoryDTO.setCode(item.getCategory().getCode());
            categoryDTO.setTypeName(item.getCategory().getTypeName());
            categoryDTO.setZIndex(item.getCategory().getZIndex());
            categoryDTO.setIsRequired(item.getCategory().getIsRequired());

            dto.setCategory(categoryDTO);
        }

        return dto;
    }
}
