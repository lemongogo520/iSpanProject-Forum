package forum.board.friends;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import forum.board.conversation.ConversationUserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping ("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
	
	private final FriendshipService fService;
	private final ConversationUserService uService;
	
//	@GetMapping("/request")
//	public ResponseEntity<Object> sendFriendRequest(@RequestParam UUID targetUserId
//		, HttpSession session) {
//		UUID loginUserId = (UUID)session.getAttribute("loginUserId");	
//		if(loginUserId != null) {
//			fService.sendFriendRequest(loginUserId, targetUserId);
//		}
//		return ResponseEntity.status(HttpStatus.CREATED).build();
//	}
//
//	// 取得好友列表
//	@GetMapping("/list")
//	public ResponseEntity<Object> getFriendList(Model model, HttpSession session) {
//		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
//		List<FriendResponse> friendsList = fService.getFriendsList(loginUserId);
//		
//		return ResponseEntity.status(HttpStatus.OK).body(friendsList);
//	}
//
//	// 接受好友邀請
//	@PatchMapping ("/request/{friendshipId}/accept")
//	public ResponseEntity<Object> acceptFriendRequest(@PathVariable Long friendshipId
//		, HttpSession session) {
//		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
//		fService.acceptFriendRequest(friendshipId, loginUserId);
//		
//		return ResponseEntity.status(HttpStatus.OK).build();
//	}


	
	
	
	
	
	
	

}
