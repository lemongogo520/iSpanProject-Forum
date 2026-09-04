package forum.board.dollsystemitem;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import forum.board.dollsystemcategories.DollCategory;
import forum.board.dollsystemcategories.DollCategoryRepository;


@Service
@RequiredArgsConstructor
public class DollItemService {

    private final DollItemRepository iRepo;
    private final DollCategoryRepository cRepo;

    public DollItem insertItem(Integer categoryId,boolean isDefault,String itemName,byte[]imageFile){

        DollCategory category = cRepo.findById(categoryId).get();
        DollItem item = new DollItem();
        item.setCategory(category);
        item.setDefault(isDefault);
        item.setName(itemName);
        item.setImageFile(imageFile);
        return iRepo.save(item);
    }

    public List<DollItem> findAllItems(){
        return iRepo.findAll();
    }

    public Optional<DollItem> findItemById(Integer id){
        return iRepo.findById(id);
    }

    public List<DollItem> findDefaultItems(){
        return iRepo.findByIsDefaultTrue();
    }
}
