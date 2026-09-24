package forum.board.dollsystemitem;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DollitemAPi {

    private final DollItemService itemService;

    // 新增部件api
    @PostMapping("/api/dollitems/add")
    public ResponseEntity<?> addDollItem(@RequestPart("itemData") DollItemDTO dollItemDTO,
            @RequestPart("file") MultipartFile file) throws IOException {

        DollItem newItem = itemService.addDollItem(dollItemDTO, file);
        return ResponseEntity.ok(newItem);
    }

    // 更改Dollitem名稱api
    @PatchMapping("/api/dollitems/changename/{itemId}")
    public ResponseEntity<?> changeDollItemName(@PathVariable Integer itemId, @RequestParam String newName) {

        DollItem changeItemName = itemService.changeItemName(itemId, newName);
        return ResponseEntity.ok(changeItemName);
    }

    // 更改Dollitem上下架api
    @PatchMapping("/api/dollitems/status/{itemId}")
    public ResponseEntity<?> updateDollItemStatus(@PathVariable Integer itemId, @RequestParam boolean isActive) {
        DollItem updateItemStatus = itemService.updateItemStatus(itemId, isActive);

        return ResponseEntity.status(HttpStatus.OK).body(updateItemStatus);
    }

    // 使用者檢視Dollitem api
    @GetMapping("/api/dollitems/usercheck/{categoryId}")
    public ResponseEntity<?> itemByUser(@PathVariable Integer categoryId) {

        List<DollItemResponseDTO> itemByUser = itemService.checkItemByUser(categoryId);
        return ResponseEntity.ok(itemByUser);
    }

    // admin檢視Dollitem api
    @GetMapping("/api/dollitems/admincheck/{categoryId}")
    public ResponseEntity<List<DollItem>> checkItemByAdmin(@PathVariable Integer categoryId) {

        List<DollItem> checkItemByAdmin = itemService.checkItemByAdmin(categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(checkItemByAdmin);
    }

}
