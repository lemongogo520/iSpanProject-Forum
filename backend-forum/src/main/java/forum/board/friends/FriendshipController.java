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

import forum.board.chat.ws.NotificationBroadcaster;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.dto.FriendshipResponseDTO;
import forum.board.friends.dto.SendFriendDTO;
import forum.board.users.UsersRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {

	private final FriendshipService fService;
	private final UsersRepository uRepo;
	private final NotificationBroadcaster notifier;

	private String myName(UUID userId) {
		return uRepo.findById(userId)
					.map(u -> u.getUsersDetail().getName())
					.orElse("某位使用者");
	}
	/*
	 * /api/friendship/request
	 */
	@PostMapping("/request")
	public ResponseEntity<Object> sendFriendRequest(@RequestBody SendFriendDTO request, HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		fService.sendFriendRequest(loginUserId, request.targetUserId());
		notifier.notify(request.targetUserId(), "FRIEND_REQUEST", myName(loginUserId));

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/*
	 * // 接受好友邀請
	 * /api/friendship/request/{friendshipId}/accept
	 */
	@PatchMapping("/request/{friendshipId}/accept")
	public ResponseEntity<Object> acceptFriendRequest(@PathVariable Long friendshipId, HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		UUID otherId = fService.acceptFriendRequest(friendshipId, loginUserId);
		notifier.notify(otherId, "FRIEND_ACCEPTED", myName(loginUserId));
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/*
	 * // 拒絕好友邀請
	 * /api/friendship/request/{friendshipId}
	 */
	@DeleteMapping("/request/{friendshipId}")
	public ResponseEntity<Object> rejectFriendRequest(@PathVariable Long friendshipId, HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		UUID otherId = fService.rejectFriendRequest(friendshipId, loginUserId);
		notifier.notify(otherId, "FRIEND_REJECTED", myName(loginUserId));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/*
	 * // 取得好友列表
	 * /api/friendship/list
	 */
	@GetMapping("/list")
	public ResponseEntity<Object> getFriendList(HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		List<FriendResponseDTO> friendsList = fService.getFriendsList(loginUserId);
		return ResponseEntity.status(HttpStatus.OK).body(friendsList);
	}

	/*
	 * // 送出的好友邀請列表
	 * /api/friendship/request/send/list
	 */
	@GetMapping("/request/send/list")
	public ResponseEntity<Object> getRequestList(HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		List<FriendshipResponseDTO> requestList = fService.getRequestList(loginUserId);
		return ResponseEntity.status(HttpStatus.OK).body(requestList);
	}

	/*
	 * // 收到的好友邀請列表
	 * /api/friendship/request/received/list
	 */
	@GetMapping("/request/received/list")
	public ResponseEntity<Object> getReceivedList(HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		List<FriendshipResponseDTO> receivedList = fService.getReceivedList(loginUserId);
		return ResponseEntity.status(HttpStatus.OK).body(receivedList);

	}

	/*
	 * 取消好友邀請
	 * /api/friendship/request/cancel
	 */
	@PatchMapping("/request/{friendshipId}/cancel")
	public ResponseEntity<Object> cancelFriendRequest(@PathVariable Long friendshipId, HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		UUID otherId = fService.cancelFriendRequest(friendshipId, loginUserId);
		notifier.notify(otherId, "FRIEND_CANCELED", myName(loginUserId));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/*
	 * 取消好友邀請
	 * /api/friendship/{targetUserId}
	 */
	@DeleteMapping("/{targetUserId}")
	public ResponseEntity<?> deleteFriendRequest(@PathVariable UUID targetUserId, 
		HttpSession session) {
		UUID loginUserId = (UUID) session.getAttribute("loginUserId");
		UUID otherId = fService.removeFriend(loginUserId,targetUserId);
		notifier.notify(otherId, "FRIEND_REMOVED", myName(loginUserId));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
