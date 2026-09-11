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
	
	public Integer getPermissionRankByUserId(UUID userId) {
		UserPermission up = userPermissionRepository.findByUserId(userId).get();
		return permissionsRepository.findById(up.getPermissionId()).get().getRank();
	}
}
    