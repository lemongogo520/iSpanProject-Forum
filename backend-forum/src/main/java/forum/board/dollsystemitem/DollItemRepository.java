package forum.board.dollsystemitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DollItemRepository extends JpaRepository<DollItem,Integer> {

    @Query("from DollItem i where i.category.id= :categoryId")
    Optional<DollItem>findByCategory(Integer categoryId);

    List<DollItem> findByIsDefaultTrue();
}
