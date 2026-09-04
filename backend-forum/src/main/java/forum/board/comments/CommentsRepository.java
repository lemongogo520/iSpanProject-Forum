package forum.board.comments;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentsRepository extends JpaRepository<Comments,Integer>{

    
    // 撈某篇post中所有主留言
    Page<Comments> findByPostIdAndParentCommentIsNull(Integer postId, Pageable pageable);

    // 撈某主留言中的子留言並按時間排序
    List<Comments> findByParentCommentIdOrderByCreateTimeAsc(Integer parentId);

    // 計算子留言筆數
    long countByParentCommentId(Integer parentId);

}
