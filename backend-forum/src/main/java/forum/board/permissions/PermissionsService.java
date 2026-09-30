package forum.board.permissions;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionsService {
	
	private final PermissionsRepository permissionsRepository;
	
	public Permissions getPermission(String rankName) {
		return permissionsRepository.findByName(rankName).get();
	}

	public Permissions getPermissionByRank(Integer rank) {
		
		String rankName = "user";
		switch(rank) {
			case 1:
				rankName = "systemadmin";
				break;
			case 2:
				rankName = "manager";
				break;
			default:
				rankName = "user";
				break;
		}
		return getPermission(rankName);
	}
}
    