package forum.board.users;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UsersController {
	private final UsersService usersService;
}
    