package forum.board.permissions;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionsService {
	
	private final PermissionsRepository permissionsRepository;
	
}
    