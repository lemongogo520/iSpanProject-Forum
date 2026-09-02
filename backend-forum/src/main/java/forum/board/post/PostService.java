package forum.board.post;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostsRepository postRepo;

	private final UsersRepository usersRepo;
	
	private final BoardRepository boardRepo;

	private final PostsHistoryService historyService;

	// 找所有貼文
	public List<Posts> findAll() {

		return postRepo.findByIsDeletedFalse();

	}

	// 新增貼文
	public Posts create(Posts newPost, UUID userId) {
		
		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入後再發文！");
		}

		Integer boardId = newPost.getBoard().getId();
		
		Optional<Board> board = boardRepo.findById(boardId);
		if(board.isEmpty()) {
			throw new RuntimeException("找不到指定的版區！");
		}
		Users newUser = user.get();
		Board newBoard = board.get();
		newPost.setUsers(newUser);
		newPost.setBoard(newBoard);
		newPost.setViews(0); // 假設有瀏覽數欄位

		return postRepo.save(newPost);
	}

	// 用id找貼文
	public Posts findById(Integer id) {

		Optional<Posts> opPost = postRepo.findByIdAndIsDeletedFalse(id);

		if (opPost.isEmpty()) {
			return null;
		}
		return opPost.get();
	}

	// 用title找貼文
	public List<Posts> findPostsByTitle(String title) {
		return postRepo.findByTitleContainingAndIsDeletedFalse(title);
	}

	// 查某個board的文章
	public List<Posts> findByBoardId(Integer boardId) {

		return postRepo.findByBoardIdAndIsDeletedFalse(boardId);

	}

	// 修改文章
	@Transactional
	public Posts update(Integer id, Posts newPost, UUID userId) {

		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入！");
		}
		
		Optional<Posts> oldPost = postRepo.findById(id);
		if (oldPost.isEmpty()) {
			throw new RuntimeException("修改失敗！找不到指定的文章 ID: " + id);

		}
		
		Users loginUser = user.get();
		Posts post = oldPost.get();
		
		// 刪除狀態的文章不能修改
		if (Boolean.TRUE.equals(post.getIsDeleted())) {
		    throw new RuntimeException("已刪除的文章不能修改！");
		}
		
		// 檢查使用者
		if (!post.getUsers().getId().equals(loginUser.getId())) {
			throw new RuntimeException("不能修改別人的文章");
		}

		historyService.saveUpdateHistory(post, loginUser, newPost.getTitle(), newPost.getContent());

		// post更新文章
		post.setTitle(newPost.getTitle());
		post.setContent(newPost.getContent());
		post.setModifyTime(Instant.now());

		return postRepo.save(post);

	}

	// 刪除文章並寫入到修改文章記錄
	@Transactional
	public void delete(Integer id, UUID userId) {
		
		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入！");
		}
		
		Optional<Posts> oldPost = postRepo.findById(id);
		if (oldPost.isEmpty()) {
			throw new RuntimeException("刪除失敗！找不到指定的文章 ID: " + id);
		}
		
		Users loginUser = user.get();
		Posts post = oldPost.get();
		
		if (Boolean.TRUE.equals(post.getIsDeleted())) {
		    throw new RuntimeException("文章已經刪除！");
		}

		if (!post.getUsers().getId().equals(loginUser.getId())) {
			throw new RuntimeException("不能刪除別人的文章");
		}
		// 管理員權限檢查尚未實作待補!!!!!!!!!!!!!!!!

		// 建立刪除紀錄物件
		historyService.saveDeleteHistory(post, loginUser);

	    post.setIsDeleted(true);
	    post.setDeletedTime(Instant.now());
		
		postRepo.save(post);
	}

	// 增加觀看數
	@Transactional
	public int  increaseViews(Integer id) {

		return postRepo.increaseViews(id);

	}

	// 分頁功能
	public Page<Posts> findByBoardId(Integer boardId, Pageable pageable) {
		
		 Pageable sortedPageable = PageRequest.of( pageable.getPageNumber(),
				 											pageable.getPageSize(),
				 											Sort.by(Sort.Direction.DESC, "createTime"));

		return postRepo.findByBoardIdAndIsDeletedFalse(boardId, sortedPageable);
	}

}
