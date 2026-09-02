package forum.board.userpermission;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserPermissionController {
	private final UserPermissionService userPermissionService;
}
    