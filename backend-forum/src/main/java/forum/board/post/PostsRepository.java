package forum.board.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostsRepository extends JpaRepository<Posts, Integer> {

	List<Posts> findByIsDeletedFalse();

	Optional<Posts> findByIdAndIsDeletedFalse(Integer id);

	List<Posts> findByTitleContainingAndIsDeletedFalse(String title);

	List<Posts> findByBoardIdAndIsDeletedFalse(Integer boardId);

	Page<Posts> findByBoardIdAndIsDeletedFalse(Integer boardId, Pageable pageable);

	@Modifying
	@Transactional
	@Query("""
			UPDATE Posts p
			SET p.views = p.views + 1
			WHERE p.id = :id
			  AND p.isDeleted = false
			""")
	int increaseViews(@Param("id") Integer id);

	// 全站標題搜尋，排除已刪除文章
	Page<Posts>findByTitleContainingAndIsDeletedFalse(String title,Pageable pageable);
	

// 指定看板的標題搜尋，排除已刪除文章
	Page<Posts> findByBoardIdAndTitleContainingAndIsDeletedFalse(Integer boardId,String title,Pageable pageable);

}
