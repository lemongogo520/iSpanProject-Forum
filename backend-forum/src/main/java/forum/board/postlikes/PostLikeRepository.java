package forum.board.postlikes;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    Optional<PostLike> findByPostIdAndUserId(Integer postId,UUID userId);
    
    long countByPostId(Integer postId);
    
    

}
