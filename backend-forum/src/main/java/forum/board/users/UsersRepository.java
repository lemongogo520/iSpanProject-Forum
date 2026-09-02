package forum.board.users;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {

    boolean existsByAccount(String account);

    Optional<Users> findByAccount(String account);
    
    Optional<Users> findById(String id);
}
    