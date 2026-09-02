package forum.board.userpermission;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPermissionService {
	
	private final UserPermissionRepository userPermissionRepository;
	
}
    