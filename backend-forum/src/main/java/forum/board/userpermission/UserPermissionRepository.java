package forum.board.userpermission;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {

    // 判斷是否符合身分組permissionName == systemadmin、manager、user
    boolean existsByUserIdAndPermissions_NameIgnoreCase(UUID userId,String permissionName);

}
    