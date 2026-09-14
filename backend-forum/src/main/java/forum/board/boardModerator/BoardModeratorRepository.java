package forum.board.boardModerator;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardModeratorRepository extends JpaRepository<BoardModerator, Integer> {

    boolean existsByBoard_IdAndUser_Id(Integer boardId,UUID userId);

    List<BoardModerator> findByBoard_IdOrderByIdAsc(Integer boardId);

     void deleteByBoard_IdAndUser_Id(Integer boardId,UUID userId);
}
