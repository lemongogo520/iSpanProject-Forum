package forum.board.dollsystemcategories;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
public class DOllCategoryApi {

    private final DollCategoryService categoryService;

    @GetMapping("/api/dollcategory")
    public ResponseEntity<?> categoryById(@RequestParam Integer id) {

        Optional<DollCategory> optional = categoryService.findById(id);
        if (optional.isPresent()) {
            DollCategory category = optional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/json");
            return new ResponseEntity<DollCategory>(category, headers, HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @GetMapping("/api/dollcategory/all")
    public ResponseEntity<?> categoryAll() {

        List<DollCategory> allCategory = categoryService.findAllCategory();
        return new ResponseEntity<List<DollCategory>>(allCategory, HttpStatus.OK);
    }

    @PostMapping("/api/newcategory")
    public ResponseEntity<?> addDollCategory(@RequestBody DollCategoryDTO requestDTO){

        DollCategory newDollCategory = categoryService.inserDollCategory(
                        requestDTO.getCode(),
                        requestDTO.getTypeName(),
                        requestDTO.getZIndex(),
                        requestDTO.getIsRequired());
        return ResponseEntity.status(HttpStatus.CREATED).body(newDollCategory);
    }

    @PatchMapping("/api/dollcategory/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Integer id, @RequestBody DollCategoryDTO requestDTO) {

        categoryService.updateCategory(id, requestDTO.getTypeName(), requestDTO.getZIndex());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
