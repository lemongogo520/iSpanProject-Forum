package forum.board.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;

    // 找所有看板分類
    public List<Category> findAll() {

        return categoryRepo.findAll();
    }

    // 根據id找類別
    public Category findById(Integer id) {

        Optional<Category> opCategory = categoryRepo.findById(id);

        if (opCategory.isEmpty()) {

            return null;
        }
        return opCategory.get();
    }

    // 根據名稱找類別
    public Category findByName(String name) {

        Optional<Category> opCategory = categoryRepo.findByName(name);

        if (opCategory.isEmpty()) {

            return null;
        }

        return opCategory.get();
    }

    // 新增類別
    public Category insertCategory(Category category) {

        Optional<Category> opCategory = categoryRepo.findByName(category.getName());
        if (opCategory.isPresent()) {
            throw new RuntimeException("分類存在" +category.getName());
        }
        return categoryRepo.save(category);
    }

    // 更新排名和名稱
    public Category updateCategory(Category category) {

        Optional<Category> opCategory = categoryRepo.findById(category.getId());
        if (opCategory.isEmpty()) {
            return null;
        }
        Category newCategory = opCategory.get();
        newCategory.setName(category.getName());
        newCategory.setRank(category.getRank());

        return categoryRepo.save(newCategory);
    }

    // 刪除類別
    public void delete(Integer id) {

        categoryRepo.deleteById(id);
    }

}
