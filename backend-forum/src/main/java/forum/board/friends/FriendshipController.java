package forum.board.friends;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import forum.board.conversation.ConversationUserService;
import forum.board.users.Users;

import jakarta.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
public class FriendshipController {
	
	private final FriendshipService fService;
	private final ConversationUserService uService;
	
	@GetMapping("/friend")
	public String getFriendList(@RequestParam (value="p", defaultValue = "1")Integer pageNumber, Model model, HttpSession httpSession) {
		UUID loginUserId = (UUID)httpSession.getAttribute("loginUserId");
		Page<Friendship> page = fService.getFriends(loginUserId,pageNumber);
		model.addAttribute("page", page);
		
		return "friends/friendsListView";
	}
	
	@GetMapping("/friend/addlist")
	public String getUserList(@RequestParam (value="p", defaultValue = "1")Integer pageNumber
			, Model model, HttpSession httpSession) {
		UUID loginUserId = (UUID)httpSession.getAttribute("loginUserId");
		Page<Users> page = uService.userList(pageNumber);
		model.addAttribute("page", page);
		
		return "friends/addFriendView";
	}
	
	@GetMapping("/friend/add")
	public String add(@RequestParam String param) {
		
		
		return new String();
	}
	
	

}
