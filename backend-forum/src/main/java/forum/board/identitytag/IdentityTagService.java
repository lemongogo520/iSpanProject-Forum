package forum.board.identitytag;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentityTagService {
	
	private final IdentityTagRepository identityTagRepository;
	
}
    