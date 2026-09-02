package forum.board.identitytag;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IdentityTagController {
	private final IdentityTagService identityTagService;
}
    