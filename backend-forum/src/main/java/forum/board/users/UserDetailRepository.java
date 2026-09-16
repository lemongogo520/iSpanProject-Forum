package forum.board.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {

    Optional<UserDetail> findByUserId(UUID userId);

    @Query("""
    	    SELECT d
    	    FROM UserDetail d
    	    JOIN d.users u
    	    JOIN UserPermission p ON p.userId = u.id 
    	    JOIN Permissions r ON r.id = p.permissionId
    	    WHERE r.rank IN :ranks
    	""")
	List<UserDetail> findByRankIn(@Param("ranks") List<Integer> ranks);
}
    