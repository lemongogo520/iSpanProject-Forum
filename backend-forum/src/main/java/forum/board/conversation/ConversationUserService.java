package forum.board.conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import forum.board.users.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationUserService {
    private final ConversationUserRepository uRepo;
    public Page<Users> userList(Integer pageNumber) {
    	Pageable pgb = PageRequest.of(pageNumber-1, 5);
    	Page<Users> allUser = uRepo.findAll(pgb);
    	System.out.println(allUser.getTotalElements());
    	if(allUser.isEmpty()) {
    		throw new IllegalStateException("沒有會員");
    	}
    	return allUser;
    }
}
