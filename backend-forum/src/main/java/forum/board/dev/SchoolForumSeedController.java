package forum.board.dev;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import forum.board.userpermission.UserPermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RestController
@Profile("local")
@RequiredArgsConstructor
public class SchoolForumSeedController {

    private final SchoolForumSeedService seedService;

    private final UserPermissionService userPermissionService;

    
    @PostMapping("/api/dev/seed/school-forum")
    public ResponseEntity<?> generate(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("loginUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "請先登入"));
        }
        if (!userPermissionService.isSystemAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "只有系統管理員可以產生假資料"));
        }
        try {
            SchoolForumSeedService.SeedResult result = seedService.generate();
            return ResponseEntity.ok(Map.of(
                "message", "校園論壇假資料建立完成",
                "postsCreated", result.postsCreated(),
                "commentsCreated", result.commentsCreated()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }
}