package forum.board.userpermission;

import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

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
        return userPermissionRepository.existsByUserIdAndPermissions_NameIgnoreCase(userId,"manager");
    }

    public boolean canManageForum(UUID userId) {

        return isSystemAdmin(userId) || isManager(userId);
        
    }
}
