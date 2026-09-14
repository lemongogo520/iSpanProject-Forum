package forum.board.userpermission;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import forum.board.users.Users;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {

    // 判斷是否符合身分組permissionName == systemadmin、manager、user
    boolean existsByUserIdAndPermissions_NameIgnoreCase(UUID userId, String permissionName);

    Optional<UserPermission> findByUserId(UUID userId);
}
