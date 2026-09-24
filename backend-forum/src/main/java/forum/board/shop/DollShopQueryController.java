package forum.board.shop;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import forum.board.dollsystemuserowneditem.DollUserOwnedService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DollShopQueryController {

    private final DollUserOwnedService ownedService;

    @GetMapping("/api/dollshop/items")
    public ResponseEntity<?> getShopItems(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            HttpSession session) {

        Object loginUserId = session.getAttribute("loginUserId");
        UUID userId = null;

        if (loginUserId instanceof UUID id) {
            userId = id;
        } else if (loginUserId instanceof String id) {
            try {
                userId = UUID.fromString(id);
            } catch (IllegalArgumentException ignored) {
                // 無效的登入身分，交由下方回傳 401。
            }
        }

        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "請先登入"));
        }

        if (categoryId <= 0 || pageNumber <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "分類編號與頁碼必須大於 0"));
        }

        return ResponseEntity.ok(
                ownedService.getShopItemsByPage(userId, categoryId, pageNumber));
    }
}