package forum.board.comments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsHistoryRepository extends JpaRepository<CommentsHistory,Integer>{

    List<CommentsHistory> findByCommentIdAndActionOrderByModifyTimeDesc(Integer commentId, String action);

    List<CommentsHistory> findByCommentIdOrderByModifyTimeDesc(Integer commentId);

}
