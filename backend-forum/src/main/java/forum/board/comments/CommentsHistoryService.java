package forum.board.comments;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import forum.board.users.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentsHistoryService {

	private final CommentsHistoryRepository commentsHistoryRepo;

	private final CommentsRepository commentsRepo;

	// 新增修改紀錄
	public void updateHistory(Comments oldComments, String newContent, Users user) {
		CommentsHistory history = new CommentsHistory();

		history.setCommentId(oldComments.getId());
		history.setPostId(oldComments.getPost().getId());
		history.setOldContent(oldComments.getContent());
		history.setUsers(user);
		history.setNewContent(newContent);
		history.setAction("UPDATE");

		commentsHistoryRepo.save(history);

	}

	// 新增刪除留言紀錄
	public void recordDeleteHistory(Comments comment, Users user) {
		CommentsHistory history = new CommentsHistory();
		history.setCommentId(comment.getId());
		history.setPostId(comment.getPost().getId());
		history.setOldContent(comment.getContent());
		history.setUsers(user);
		history.setAction("DELETE");

		commentsHistoryRepo.save(history);

	}

	// 在前端做查看編輯紀錄用要登入才能看
	public List<CommentsHistory> findUpdateHistoryByCommentId(Integer commentId, Users user) {

		if (user == null) {
			throw new RuntimeException("權限不足：請先登入！");
		}
		Optional<Comments> opComment = commentsRepo.findById(commentId);
		if (opComment.isEmpty()) {
			throw new RuntimeException("找不到指定的留言");
		}

		Comments comment = opComment.get();
		if (!comment.getUsers().getId().equals(user.getId())) {
			throw new RuntimeException("權限不足：您只能查看自己留言的修改歷史！");
		}

		return commentsHistoryRepo.findByCommentIdAndActionOrderByModifyTimeDesc(commentId, "UPDATE");
	}

	// 管理者查看某則留言的所有歷程
	public List<CommentsHistory> findAllHistoryByCommentId(Integer commentId) {
		return commentsHistoryRepo.findByCommentIdOrderByModifyTimeDesc(commentId);
	}
}
