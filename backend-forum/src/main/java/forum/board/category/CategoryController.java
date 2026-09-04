package forum.board.category;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	// 新增
	@PostMapping("/api/categories")
	public ResponseEntity<?> save(@RequestBody Category category) {
		categoryService.insertCategory(category);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// 查詢全部
	@GetMapping("/api/categories")
	public List<Category> findAll() {

		return categoryService.findAll();
	}

	//查詢單一分類
	@GetMapping("/api/categories/{id}")
	public ResponseEntity<Category> findById(@PathVariable Integer id) {
		Category category = categoryService.findById(id);
		
		if (category == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(category);
	}
	
	// 更新分類
	@PutMapping("/api/categories/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody Category category) {

		category.setId(id);

		Category upCategory = categoryService.updateCategory(category);

		if (upCategory == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	// 刪除分類
	@DeleteMapping("/api/categories/delete/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Integer id, HttpSession hs) {

		// 權限判斷(管理員)先隨便設待之後實作後補上
	    boolean isAdmin = true; // 現在先寫死為 true，讓自己測試「刪除分類」功能時可以順利通行
	    
	    if (!isAdmin) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有管理員可以刪除分類");
	    }
		
		
		categoryService.delete(id);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
