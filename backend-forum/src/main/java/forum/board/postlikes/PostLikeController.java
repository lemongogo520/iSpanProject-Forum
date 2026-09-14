package forum.board.postlikes;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	// 取得文章按讚狀態
	@GetMapping("/api/posts/{postId}/like")
	public ResponseEntity<?> getLikeStatus(@PathVariable Integer postId, HttpSession session) {

		UUID userId = (UUID) session.getAttribute("loginUserId");
	
		try {
			PostLikeDTO result = postLikeService.getLikeStatus(postId, userId);

			return ResponseEntity.ok(result);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// 新增或取消按讚
	@PutMapping("/api/posts/{postId}/like")
	public ResponseEntity<?> toggleLike(@PathVariable Integer postId, HttpSession session) {

		UUID userId = (UUID) session.getAttribute("loginUserId");
		 if (userId == null) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("請先登入才能按讚！");
    }
		try {
			PostLikeDTO result = postLikeService.toggleLike(postId, userId);

			return ResponseEntity.ok(result);

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
