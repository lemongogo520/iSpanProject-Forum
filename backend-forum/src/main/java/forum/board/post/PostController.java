package forum.board.post;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import forum.board.config.Settings;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	private final PostsHistoryService postsHistoryService;

	private final PostImageService postImageService;

	private final Settings settings;

	// 取得熱門文章
	@GetMapping("/api/posts/hot")
	public List<HotPostDTO> findHotPosts(@RequestParam(defaultValue = "5") int limit) {
		return postService.findHotPosts(limit);
	}

	// 取得全部文章
	@GetMapping("/api/posts")
	public List<Posts> findAll() {
		return postService.findAll();
	}

	// 取得單篇文章
	@GetMapping("/api/posts/{id}")
	public ResponseEntity<PostDTO> findById(@PathVariable Integer id) {

		PostDTO postDTO = postService.findPostDTOById(id);

		if (postDTO == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(postDTO);
	}

	// 取得文章編輯紀錄
	@GetMapping("/api/posts/{id}/history")
	public ResponseEntity<List<PostsHistoryDTO>> findHistory(
			@PathVariable Integer id) {

		List<PostsHistoryDTO> history = postsHistoryService.findByPostId(id);

		return ResponseEntity.ok(history);
	}

	// 根據board找文章有分頁
	@GetMapping("/api/posts/boards/{boardId}")
	public Page<Posts> findByBoardIdpage(@PathVariable Integer boardId,
			@RequestParam(defaultValue = "latest") String sortBy, Pageable pageable) {

		Pageable configuredPageable = PageRequest.of(pageable.getPageNumber(), getPostPageSize(), pageable.getSort());

		return postService.findByBoardId(boardId, sortBy, configuredPageable);
	}

	// 用posttitle找文章
	@GetMapping("/api/posts/search")
	public Page<PostDTO> search(
			@RequestParam String title,
			@RequestParam(required = false) Integer boardId,
			@RequestParam(defaultValue = "latest") String sortBy,
			Pageable pageable) {

		Pageable configuredPageable = PageRequest.of(pageable.getPageNumber(), getPostPageSize(), pageable.getSort());

		return postService.searchPosts(title, boardId, sortBy, configuredPageable);
	}

	// 新增文章
	@PostMapping("/api/posts")
	public ResponseEntity<?> createPost(@RequestBody Posts post, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能發表文章");
		}
		try {
			Posts result = postService.create(post, userId);

			return ResponseEntity.status(HttpStatus.CREATED).body(result);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 刪除文章
	@DeleteMapping("/api/posts/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能刪除文章");
		}
		try {
			postService.delete(id, userId);

			return ResponseEntity.ok("文章刪除成功！");
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	// 更新文章
	@PutMapping("/api/posts/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Posts post, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能更新文章");
		}
		try {
			Posts result = postService.update(id, post, userId);
			return ResponseEntity.ok(result);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	// 增加文章瀏覽數
	@PatchMapping("/api/posts/{id}/views")
	public ResponseEntity<Void> increaseViews(@PathVariable Integer id) {

		int result = postService.increaseViews(id);

		if (result == 0) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// 上傳文章圖片
	@PostMapping("/api/posts/images")
	public ResponseEntity<?> uploadPostImage(
			@RequestParam("file") MultipartFile file,
			HttpSession hs) {
		UUID userId = (UUID) hs.getAttribute("loginUserId");

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入才能上傳文章圖片");
		}

		try {
			String fileName = postImageService.saveImage(file);

			return ResponseEntity.ok(
					Map.of(
							"url",
							"/uploads/img/" + fileName));

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest()
					.body(e.getMessage());
		}
	}

	private int getPostPageSize() {
		try {
			int size = Integer.parseInt(settings.get("post_paper_page"));
			return size > 0 && size <= 100 ? size : 10;
		} catch (Exception e) {
			return 10;
		}
	}
}
