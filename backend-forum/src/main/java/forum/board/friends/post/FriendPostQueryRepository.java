package forum.board.friends.post;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import forum.board.post.Posts;

public interface FriendPostQueryRepository extends JpaRepository<Posts,Integer> {
     @Query("""
        SELECT p FROM Posts p
        JOIN FETCH p.board
        WHERE p.users.id = :userId
          AND p.deletedTime IS NULL
        ORDER BY p.id DESC
        """)
    List<Posts> findRecentByAuthor(@Param("userId") UUID userId, Pageable pageable);
}
