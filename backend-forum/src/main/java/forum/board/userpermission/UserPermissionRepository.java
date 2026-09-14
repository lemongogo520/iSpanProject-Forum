package forum.board.userpermission;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import forum.board.users.Users;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {

    Optional<UserPermission> findByUserId(UUID userId);
}
    