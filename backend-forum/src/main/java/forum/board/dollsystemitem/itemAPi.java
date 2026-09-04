package forum.board.dollsystemitem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import forum.board.dollsystemcategories.DollCategory;
import forum.board.dollsystemcategories.DollCategoryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class itemAPi {

    public final DollCategoryService categoryService;
    private final DollItemService itemService;


    @PostMapping("/api/item/add")
    public ResponseEntity<?> addItem(
        @RequestParam Integer categoryId,
        @RequestParam String itemName,
        @RequestParam boolean isDefault,
        @RequestParam MultipartFile file) throws IOException{
        
        Optional<DollCategory> op = categoryService.findById(categoryId);
        if(op.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("檔案不可為空");
        }
        itemService.insertItem(categoryId, isDefault, itemName, file.getBytes());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/item/all")
    public ResponseEntity<?> allItems() {
        List<DollItem> allItems = itemService.findAllItems();
        if(allItems.isEmpty()){
            return ResponseEntity.ok(allItems);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity <List<DollItem>>(allItems,httpHeaders,HttpStatus.OK);      
    }

    @GetMapping("/api/item/download")
    public ResponseEntity<?> downloadItemImage(@RequestParam Integer id) {
        Optional<DollItem> optional = itemService.findItemById(id);
        if(optional.isEmpty()){
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        DollItem item = optional.get();
        byte[] imageFile = item.getImageFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(imageFile, headers, HttpStatus.OK);

    }

    @GetMapping("/api/item")
    public ResponseEntity<?> findItemById(@RequestParam Integer id) {
        Optional<DollItem> optional = itemService.findItemById(id);
        if(optional.isEmpty()){
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        DollItem item = optional.get();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<DollItem>(item, httpHeaders, HttpStatus.OK);
    }
    
    
    

    
}
