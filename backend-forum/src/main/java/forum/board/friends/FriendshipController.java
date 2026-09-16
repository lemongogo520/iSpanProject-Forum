package forum.board.friends;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.dto.FriendshipResponseDTO;
import forum.board.friends.dto.SendFriendDTO;
import jakarta.servlet.http.HttpSession;



@RestController 
@RequestMapping ("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
	
	private final FriendshipService fService;
	
	
	/*
	/api/friendship/request
	*/
	@PostMapping("/request")
	public ResponseEntity<Object> sendFriendRequest(@RequestBody SendFriendDTO request
		, HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");	
		fService.sendFriendRequest(loginUserId, request.targetUserId());
	
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/*
	// 接受好友邀請
	/api/friendship/request/{friendshipId}/accept
	*/
	@PatchMapping ("/request/{friendshipId}/accept")
	public ResponseEntity<Object> acceptFriendRequest(@PathVariable Long friendshipId
		, HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		fService.acceptFriendRequest(friendshipId, loginUserId);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/*
	// 拒絕好友邀請
	/api/friendship/request/{friendshipId}
	*/
	@DeleteMapping("/request/{friendshipId}")
	public ResponseEntity<Object> rejectFriendRequest(@PathVariable Long friendshipId
		,HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		fService.rejectFriendRequest(friendshipId, loginUserId);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	/*
	// 取得好友列表
	/api/friendship/list
	*/
	@GetMapping("/list")
	public ResponseEntity<Object> getFriendList(HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		List<FriendResponseDTO> friendsList = fService.getFriendsList(loginUserId);
		
		return ResponseEntity.status(HttpStatus.OK).body(friendsList);
	}

	/*
	// 送出的好友邀請列表
	/api/friendship/request/send/list
	*/
	@GetMapping("/request/send/list")
	public ResponseEntity<Object> getRequestList(HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		List<FriendshipResponseDTO> requestList = fService.getRequestList(loginUserId);
		if(requestList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(requestList);
	}

	/*
	// 收到的好友邀請列表
	/api/friendship/request/received/list
	*/
	@GetMapping("/request/received/list")
	public ResponseEntity<Object> getReceivedList(HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		List<FriendshipResponseDTO> receivedList = fService.getReceivedList(loginUserId);
		if(receivedList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(receivedList);
		
	}

	/* 
	取消好友邀請
	/api/friendship/request/cancel
	*/
	@PatchMapping ("/request/{friendshipId}/cancel")
	public ResponseEntity<Object> cancelFriendRequest(@PathVariable Long friendshipId
		, HttpSession session) {
		UUID loginUserId = (UUID)session.getAttribute("loginUserId");
		fService.cancelFriendRequest(friendshipId, loginUserId);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	


	
	
	
	
	
	
	

}
