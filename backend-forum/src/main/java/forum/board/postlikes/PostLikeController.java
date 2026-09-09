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

		// 登入 API 完成後改
		// UUID userId =(UUID) session.getAttribute("loginUserId");

		UUID userId = UUID.fromString("10334E31-124A-4A74-B898-4A2C62753CC9");

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

		// 登入 API 完成後改
		// UUID userId =(UUID) session.getAttribute("loginUserId");

		UUID userId = UUID.fromString("10334E31-124A-4A74-B898-4A2C62753CC9");

		try {
			PostLikeDTO result = postLikeService.toggleLike(postId, userId);

			return ResponseEntity.ok(result);

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
