package forum.board.usertag;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTagService {
	
	private final UserTagRepository userTagRepository;
	
}
    