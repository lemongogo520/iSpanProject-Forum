package forum.board.shop;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.dollsystemcategories.DollCategoryDTO;
import forum.board.dollsystemcategories.DollCategoryRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DollShopCategoryController {

    private final DollCategoryRepository categoryRepository;

    @GetMapping("/api/dollshop/categories")
    public List<DollCategoryDTO> getCategories() {
        return categoryRepository.findAll(
                Sort.by("zIndex").ascending().and(Sort.by("id").ascending()))
                .stream()
                .map(category -> {
                    DollCategoryDTO dto = new DollCategoryDTO();
                    dto.setId(category.getId());
                    dto.setCode(category.getCode());
                    dto.setTypeName(category.getTypeName());
                    dto.setZIndex(category.getZIndex());
                    dto.setIsRequired(category.getIsRequired());
                    return dto;
                })
                .toList();
    }
}