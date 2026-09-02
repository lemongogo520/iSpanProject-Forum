package forum.board.permissions;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PermissionsController {
	private final PermissionsService permissionsService;
}
    