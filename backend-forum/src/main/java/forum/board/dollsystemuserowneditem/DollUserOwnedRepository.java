package forum.board.dollsystemuserowneditem;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import forum.board.dollsystemitem.DollItem;

public interface DollUserOwnedRepository extends JpaRepository<DollUserOwned, Integer> {

    // 拿到 DollItem (裝備本身)
    @Query("SELECT d.item FROM DollUserOwned d WHERE d.user.id = :userId")
    List<DollItem> findItemsByUserId(UUID userId);

    // 取得玩家擁有的「特定分類」的所有裝備
    @Query("SELECT d.item FROM DollUserOwned d WHERE d.user.id = :userId AND d.item.category.id = :categoryId")
    List<DollItem> findItemsByUserIdAndCategoryId(UUID userId, Integer categoryId);

    boolean existsByUserIdAndItemId(UUID userId, Integer itemId);

}