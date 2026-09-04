package forum.board.dollsystemcategories;

import org.springframework.data.jpa.repository.JpaRepository;


public interface DollCategoryRepository extends JpaRepository<DollCategory,Integer> {
  
    boolean existsByCode(String code);

    boolean existsByTypeName(String typeName);

    boolean existsByZIndex(Integer zIndex);
}
