package forum.board.dollsystemuserequip;

import org.springframework.web.bind.annotation.RestController;

import forum.board.dollsystemitem.DollItemResponseDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class DollUserEquipApi {

    private final DollUserEquipService dService;

    // 檢視穿著api
    @GetMapping("/api/dollitem/equipped")
    public ResponseEntity<?> getEquipItems(HttpSession session) {

        Object sessionUserId = session.getAttribute("loginUserId");
        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("使用者尚未登入");
        }

        UUID userId = (UUID) sessionUserId;
        // 透過userId檢視穿著紀錄
        List<DollItemResponseDTO> userEquip = dService.getUserEquip(userId);
        return ResponseEntity.ok(userEquip);
    }

    // 取得指定玩家目前的穿著 (供其他使用者查看大頭貼用)
    @GetMapping("/api/dollitem/equipped/{userId}")
    public ResponseEntity<?> getEquippedItemsByUserId(@PathVariable UUID userId) {
        // 透過指定 userId 檢視該玩家的穿著紀錄
        List<DollItemResponseDTO> userEquip = dService.getUserEquip(userId);
        return ResponseEntity.ok(userEquip);
    }

    // 更新穿著api
    @PutMapping("/api/dollitem/newequip")
    public ResponseEntity<?> newEquipItems(
            HttpSession session,
            @RequestBody List<Integer> itemIds) {

        Object sessionUserId = session.getAttribute("loginUserId");
        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("使用者尚未登入");
        }

        UUID userId = (UUID) sessionUserId;

        // 把穿著紀錄放到後端
        dService.updateEquipItems(userId, itemIds);

        // 重新檢視穿著紀錄
        List<DollItemResponseDTO> userEquip = dService.getUserEquip(userId);

        return ResponseEntity.ok(userEquip);
    }
}
