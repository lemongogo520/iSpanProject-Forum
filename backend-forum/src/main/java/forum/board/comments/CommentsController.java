package forum.board.comments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import forum.board.users.Users;
import forum.board.users.UsersRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentsController {

	private final CommentsService commentsService;

	private final UsersRepository usersRepo;

	private final CommentsHistoryService commentsHistoryService;

	// 新增留言 如果沒加parentCommentId代表為主要留言
	@PostMapping("/api/comments/posts/{postId}")
	public ResponseEntity<?> createComments(@PathVariable Integer postId,
			@RequestParam(required = false) Integer parentCommentId,
			@RequestBody Comments newComments,
			HttpSession session) {

		UUID userId = (UUID) session.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：必須登入才能發表留言！");
		}

		Optional<Users> opUser = usersRepo.findById(userId);

		if (opUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：找不到登入使用者！");
		}

		Users user = opUser.get();

		try {
			Comments comment = commentsService.createComments(postId, parentCommentId, newComments, user);

			return ResponseEntity.ok(comment);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 查看某post底下的主要留言
	@GetMapping("/api/comments/posts/{postId}")
	public Page<Comments> findByPostId(@PathVariable Integer postId, Pageable pageable) {

		return commentsService.findByPostId(postId, pageable);
	}

	@GetMapping("/api/comments/{parentId}/children")
	public List<Comments> findChildComments(@PathVariable Integer parentId) {

		return commentsService.findChildComments(parentId);
	}

	// 修改留言
	@PutMapping("/api/comments/{commentId}")
	public ResponseEntity<?> update(@PathVariable Integer commentId, @RequestBody Comments newComments,
			HttpSession session) {

		UUID userId = (UUID) session.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：必須登入才能修改留言！");
		}

		Optional<Users> opUser = usersRepo.findById(userId);
		if (opUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：找不到登入使用者！");
		}

		Users user = opUser.get();
		try {
			Comments result = commentsService.update(commentId, newComments.getContent(), user);

			return ResponseEntity.ok(result);

		} catch (RuntimeException e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

	}

	// 刪除留言
	@DeleteMapping("/api/comments/{commentId}")
	public ResponseEntity<?> delete(@PathVariable Integer commentId,@RequestBody(required = false)
        DeleteRequestDTO request, HttpSession session) {

		UUID userId = (UUID) session.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：必須登入才能發表留言！");
		}
		Optional<Users> opUser = usersRepo.findById(userId);

		if (opUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("錯誤：找不到登入使用者！");
		}

		Users user = opUser.get();
		String reason = request == null ? null : request.getReason();
		try {
			commentsService.delete(commentId, user, reason);

			return ResponseEntity.ok("留言刪除成功！");

		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// 查詢這筆留言的編輯紀錄
	@GetMapping("/api/comments/{commentId}/history")
	public ResponseEntity<?> findUpdateHistory(@PathVariable Integer commentId, HttpSession hs) {
		UUID userId = (UUID) hs.getAttribute("loginUserId");

		if (userId == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("錯誤：必須登入才能查看留言編輯紀錄！");
		}

		Optional<Users> opUser = usersRepo.findById(userId);

		if (opUser.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("錯誤：找不到登入使用者！");
		}

		try {
			List<CommentsHistory> history = commentsHistoryService.findUpdateHistoryByCommentId(commentId,
					opUser.get());

			return ResponseEntity.ok(history);

		} catch (RuntimeException e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	// 管理員功能之後補做

}
