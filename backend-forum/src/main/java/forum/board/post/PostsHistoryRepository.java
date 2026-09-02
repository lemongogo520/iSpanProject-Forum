package forum.board.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsHistoryRepository extends JpaRepository<PostsHistory,Integer>{

    List<PostsHistory> findByPostId(Integer postId);

}
