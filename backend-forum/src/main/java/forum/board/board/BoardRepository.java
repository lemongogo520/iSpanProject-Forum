package forum.board.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface BoardRepository extends JpaRepository<Board, Integer>{

    Optional<Board> findByName(String name);

    List<Board> findByCategoryId(Integer categoryId);
}
