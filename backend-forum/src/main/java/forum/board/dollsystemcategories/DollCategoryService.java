package forum.board.dollsystemcategories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DollCategoryService {

    private final DollCategoryRepository cRepo;

    public List<DollCategory> findAllCategory(){

        return cRepo.findAll();
    }

    public Optional <DollCategory> findById(Integer id){

        return cRepo.findById(id);
    }

    public DollCategory inserDollCategory(String code, String typeName, Integer zIndex, Boolean isRequired){

        if(cRepo.existsByCode(code)){
            throw new IllegalArgumentException("分類代碼 (Code) '" + code + "' 已經存在，請勿重複新增！");
        }
        if(cRepo.existsByTypeName(typeName)){
            throw new IllegalArgumentException("分類名稱 (TypeName) '" + typeName + "' 已經存在，請勿重複新增！");
        }
        if(cRepo.existsByZIndex(zIndex)){
            throw new IllegalArgumentException("圖層順序 (ZIndex) '" + zIndex + "' 已經存在，請勿重複新增！");
        }
        
        DollCategory dollCategory = new DollCategory();
        dollCategory.setCode(code);
        dollCategory.setTypeName(typeName);
        dollCategory.setZIndex(zIndex);
        dollCategory.setIsRequired(isRequired);
        return cRepo.save(dollCategory);
    }

    public DollCategory updateCategory(Integer id,String typeName,Integer zIndex){

        Optional<DollCategory> cId = cRepo.findById(id);
        if(cId.isPresent()){
            DollCategory category = cId.get();
            category.setTypeName(typeName);
            category.setZIndex(zIndex);
            return cRepo.save(category);
        }
        return null;
    }

}
