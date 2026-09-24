package forum.board.users;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsersRepository extends JpaRepository<Users, UUID> {

    boolean existsByAccount(String account);

    Optional<Users> findByAccount(String account);
    
    Optional<Users> findById(String id);

    @Query("""
        SELECT u
        FROM Users u
        LEFT JOIN u.usersDetail d
        WHERE LOWER(u.account) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Users> searchUsers(@Param("keyword") String keyword);
}
    