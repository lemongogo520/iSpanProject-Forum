package forum.board.board;

import org.springframework.web.bind.annotation.RestController;

import forum.board.category.CategoryService;
import forum.board.userpermission.UserPermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	private final UserPermissionService userPermissionService;

	// 新增
	@PostMapping("/api/board")
	public ResponseEntity<?> postMethodName(@RequestBody Board board, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
		}

		if (!userPermissionService.isSystemAdmin(userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有系統管理員可以新增看板");
		}
		Board newboard = boardService.createBoard(board);

		if (newboard == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// 查看全部
	@GetMapping("/api/boards")
	public List<Board> findAll() {

		return boardService.findAll();

	}

	// 查看單一項目
	@GetMapping("/api/boards/{id}")
	public ResponseEntity<Board> findById(@PathVariable Integer id) {
		Board board = boardService.findById(id); // 假設組員有寫這個方法

		if (board == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(board);
	}

	// 用category查board
	@GetMapping("/api/boards/category/{categoryId}")
	public List<Board> findByCategoryId(@PathVariable Integer categoryId) {
		// 直接返回組員查出來的 List 列表
		return boardService.findByCategoryId(categoryId);
	}

	// 更新
	@PutMapping("/api/boards/{id}")
	public ResponseEntity<?> putMethodName(@PathVariable Integer id, @RequestBody Board board, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
		}

		if (!userPermissionService.isSystemAdmin(userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有系統管理員可以修改看板");
		}

		board.setId(id);
		Board upBoard = boardService.updateBoard(board);

		if (upBoard == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(upBoard);
	}

	// 刪除
	@DeleteMapping("/api/boards/delete/{id}")
	public ResponseEntity<?> deleteBoard(@PathVariable Integer id, HttpSession hs) {

		UUID userId = (UUID) hs.getAttribute("loginUserId");

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
		}

		if (!userPermissionService.isSystemAdmin(userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("權限不足：只有系統管理員可以新增看板");
		}

		boardService.delete(id);
		return ResponseEntity.status(HttpStatus.OK).build();

	}

}
