package forum.board.shop;

import java.util.Map;
import java.util.UUID;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DollPurchaseController {
    private final DollPurchaseService purchaseService;

    @PostMapping("/api/dollshop/items/{itemId}/purchase")
    public ResponseEntity<?> purchase(@PathVariable("itemId") Integer itemId, HttpSession session) {
        Object value = session.getAttribute("loginUserId");
        UUID userId = null;
        if (value instanceof UUID id)
            userId = id;
        else if (value instanceof String text) {
            try {
                userId = UUID.fromString(text);
            } catch (IllegalArgumentException ignored) {
                /* 無效登入身分 */ }
        }
        if (userId == null)
            return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        try {
            return ResponseEntity.ok(purchaseService.purchase(userId, itemId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (ConcurrencyFailureException | DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(Map.of("message", "資料已變動，請重新查詢背包與點數後再操作"));
        }
    }
}
