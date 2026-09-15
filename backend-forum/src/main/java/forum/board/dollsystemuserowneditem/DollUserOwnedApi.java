package forum.board.dollsystemuserowneditem;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import forum.board.dollsystemitem.DollItemResponseDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DollUserOwnedApi {

    private final DollUserOwnedService ownedService;

    // 取得該部位所有裝備，並標示玩家是否已經擁有 (取代原本的 /api/dollitems/usercheck/{categoryId})
    @GetMapping("/api/dollitem/shop/{categoryId}")
    public ResponseEntity<?> getShopItemsWithOwnership(
            @PathVariable Integer categoryId,
            HttpSession session) {

        Object sessionUserId = session.getAttribute("loginUserId");
        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("使用者尚未登入");
        }

        UUID userId = (UUID) sessionUserId;

        List<DollItemResponseDTO> items = ownedService.getAllItemsWithOwnedStatus(userId, categoryId);

        return ResponseEntity.ok(items);
    }

    // 玩家獲得裝備 (點擊購買時呼叫)
    @PostMapping("/api/dollitem/acquire/{itemId}")
    public ResponseEntity<?> acquireItem(
            @PathVariable Integer itemId,
            HttpSession session) {

        Object sessionUserId = session.getAttribute("loginUserId");
        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("使用者尚未登入");
        }

        UUID userId = (UUID) sessionUserId;

        try {
            // 呼叫獲得裝備邏輯
            ownedService.acquireItem(userId, itemId);
            return ResponseEntity.ok("成功獲得裝備！");
        } catch (RuntimeException e) {
            // 如果已經擁有了，或是發生其他錯誤，把錯誤訊息回傳給前端
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
