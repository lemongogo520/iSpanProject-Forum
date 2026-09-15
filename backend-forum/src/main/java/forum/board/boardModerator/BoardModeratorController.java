package forum.board.boardModerator;

import org.springframework.web.bind.annotation.RestController;

import forum.board.userpermission.UserPermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class BoardModeratorController {

    private final BoardModeratorService boardModeratorService;

    private final UserPermissionService userPermissionService;

    // 新增版主
    @PostMapping("/api/boards/{boardId}/moderators")
    public ResponseEntity<?> addModerator(@PathVariable Integer boardId, @RequestBody AddBoardModeratorRequest userId,
            HttpSession hs) {

        UUID loginUserId = (UUID) hs.getAttribute("loginUserId");

        if (loginUserId == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }
        if (!userPermissionService.canManageForum(loginUserId)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有論壇管理員可以指派版主");
        }

        try {
            BoardModeratorDTO result = boardModeratorService.addModerator(boardId, userId.getUserId(), loginUserId);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException error) {

            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }

    // 查看某討論版的全部版主
    @GetMapping("/api/boards/{boardId}/moderators")
    public ResponseEntity<?> findModerators(@PathVariable Integer boardId, HttpSession hs) {
        UUID loginUserId = (UUID) hs.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }
        if (!userPermissionService.canManageForum(loginUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有論壇管理員可以查看版主");
        }

        List<BoardModeratorDTO> moderators = boardModeratorService.findByBoardId(boardId);

        return ResponseEntity.ok(moderators);
    }

    // 刪除某討論版版主身分
    @DeleteMapping("/api/boards/{boardId}/moderators/{userId}")
    public ResponseEntity<?> removeModerator(@PathVariable Integer boardId, @PathVariable UUID userId, HttpSession hs) {

        UUID loginUserId = (UUID) hs.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }
        if (!userPermissionService.canManageForum(loginUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有論壇管理員可以移除版主");
        }
        try {
            boardModeratorService.removeModerator(boardId, userId);
            return ResponseEntity.ok("版主移除成功");

        } catch (IllegalArgumentException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        }

    }

    // 目前登入者是否能管理這個看板
    @GetMapping("/api/boards/{boardId}/moderators/me")
    public ResponseEntity<?> canModerate(@PathVariable Integer boardId, HttpSession hs) {

        UUID loginUserId = (UUID) hs.getAttribute("loginUserId");

        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }

        boolean canModerate = boardModeratorService.isBoardModerator(boardId, loginUserId);

        return ResponseEntity.ok(Map.of("canModerate", canModerate));
    }

}
