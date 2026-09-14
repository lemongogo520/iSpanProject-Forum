package forum.board.userpermission;

import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.permissions.PermissionsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionsRepository permissionsRepository;

    public boolean isSystemAdmin(UUID userId) {
        if (userId == null) {
            return false;
        }
        return userPermissionRepository.existsByUserIdAndPermissions_NameIgnoreCase(userId, "systemadmin");
    }

    public boolean isManager(UUID userId) {
        if (userId == null) {
            return false;
        }
        return userPermissionRepository.existsByUserIdAndPermissions_NameIgnoreCase(userId, "manager");
    }

    public boolean canManageForum(UUID userId) {

        return isSystemAdmin(userId) || isManager(userId);

    }

    public Integer getPermissionRankByUserId(UUID userId) {
        UserPermission up = userPermissionRepository.findByUserId(userId).get();
        return permissionsRepository.findById(up.getPermissionId()).get().getRank();
    }
}
