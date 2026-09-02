package forum.board.usertag;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserTagController {
	private final UserTagService userTagService;
}
    