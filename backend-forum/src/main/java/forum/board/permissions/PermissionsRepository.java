package forum.board.permissions;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionsRepository extends JpaRepository<Permissions, Integer> {

	Optional<Permissions> findByName(String rankName);
}
    