package forum.board.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsHistoryRepository extends JpaRepository<PostsHistory,Integer>{

    List<PostsHistory> findByPostId(Integer postId);

    // 看文章修改紀錄
	List<PostsHistory> findByPostIdOrderByModifyTimeDesc(Integer postId);


}
