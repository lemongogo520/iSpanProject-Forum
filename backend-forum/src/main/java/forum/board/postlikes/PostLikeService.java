package forum.board.postlikes;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.post.Posts;
import forum.board.post.PostsRepository;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeRepository postLikeRepo;

	private final PostsRepository postsRepo;

	private final UsersRepository userRepo;

	 // 取得文章按讚狀態
	public PostLikeDTO getLikeStatus(Integer postId, UUID userId) {

		Optional<Posts> post = postsRepo.findByIdAndIsDeletedFalse(postId);
		if (post.isEmpty()) {
			throw new RuntimeException("找不到文章");
		}

		long likeCount = postLikeRepo.countByPostId(post.get().getId());

		boolean likedByCurrentUser = userId != null && postLikeRepo.findByPostIdAndUserId(postId, userId).isPresent();

		return new PostLikeDTO(likeCount, likedByCurrentUser);
	}
	
	// 切換按讚狀態
	@Transactional
	public PostLikeDTO toggleLike(Integer postId, UUID userId) {

		// 確認文章存在，而且尚未被刪除
		Optional<Posts> post = postsRepo.findByIdAndIsDeletedFalse(postId);
		if (post.isEmpty()) {
			throw new RuntimeException("找不到文章");
		}

		// 使用者必須登入
		if (userId == null) {
			throw new RuntimeException("請先登入");

		}

		// 確認目前使用者存在
		Optional<Users> user = userRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("找不到登入使用者");
		}

		// 查詢這位使用者是否已經按過這篇文章
		Optional<PostLike> existingLike = postLikeRepo.findByPostIdAndUserId(postId, userId);

		boolean likedByCurrentUser;

		if (existingLike.isPresent()) {
			// 已經按過讚：再次點擊代表取消按讚
			postLikeRepo.delete(existingLike.get());

			likedByCurrentUser = false;
		} else {
			// 尚未按讚：新增按讚紀錄
			PostLike postLike = new PostLike();

			postLike.setPost(post.get());
			postLike.setUser(user.get());

			postLikeRepo.save(postLike);

			likedByCurrentUser = true;
		}
		
		// 讓新增或刪除先同步到資料庫
		postLikeRepo.flush();
		
		// 重新計算最新按讚數量
        long likeCount =postLikeRepo.countByPostId(postId);
        return new PostLikeDTO(likeCount,likedByCurrentUser);
	}
}
