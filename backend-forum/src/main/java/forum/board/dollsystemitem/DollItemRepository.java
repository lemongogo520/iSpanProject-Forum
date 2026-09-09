package forum.board.dollsystemitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DollItemRepository extends JpaRepository<DollItem,Integer> {

    //篩選上架的部件
    List<DollItem> findByIsActiveTrue();

    //篩選特定圖層且上架的部件
    List<DollItem> findByCategoryIdAndIsActiveTrue(Integer categoryId);

    //篩選特定圖層全部部件
    List<DollItem> findByCategoryId(Integer categoryId);
}
