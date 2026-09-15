package forum.board.dollsystemuserequip;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DollUserEquipRepository extends JpaRepository<DollUserEquip,Integer> {

    List<DollUserEquip> findByUserId (UUID userId);

    @Modifying 
    @Query("DELETE FROM DollUserEquip u WHERE u.user.id =:userId") 
    void deleteByUserId(@Param ("userId") UUID userId);

}
