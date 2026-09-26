package forum.board.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.friends.FriendshipEntity.FriendshipStatus;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.dto.FriendshipResponseDTO;
import forum.board.friends.exception.BusinessException;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.UserDetail;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendshipService {
	private final FriendshipRepository fRepo;
	private final UsersRepository uRepo;

	private UUID[] normalize(UUID a, UUID b) {
		// -1:目前 UUID 值小於目標值
		// 0:相等
		// 1:大於
		return a.compareTo(b) < 0 ? new UUID[] { a, b } : new UUID[] { b, a };
	}


	private FriendshipEntity findPendingFriendship(Long friendshipId) {
		FriendshipEntity friendship = fRepo.findById(friendshipId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到好友邀請"));
		if (friendship.getStatus() != FriendshipStatus.PENDING) {
			throw new BusinessException("此好友邀請目前不是待處理狀態");
		}
		return friendship;
	}

	private void validateRequestReceiver(FriendshipEntity friendship, UUID currentUserId) {
		// requester 不能接受自己的邀請
		if (friendship.getRequester().getId().equals(currentUserId)) {
			throw new BusinessException("不能操作自己發送的邀請");
		}

		// 確認目前使用者是好友關係中的接收者
		boolean isMember = friendship.getUser1().getId().equals(currentUserId)
				|| friendship.getUser2().getId().equals(currentUserId);

		if (!isMember) {
			throw new BusinessException("沒有權限操作此好友邀請");
		}

	}

	private void validateRequester(FriendshipEntity friendship, UUID currentUserId) {
		// 只能取消自己送的好友邀請
		if (!friendship.getRequester().getId().equals(currentUserId)) {
			throw new BusinessException("沒有權限操作此好友邀請");
		}

	}

	private UUID otherSide(FriendshipEntity f, UUID me) {
		return f.getUser1().getId().equals(me)
				? f.getUser2().getId()
				: f.getUser1().getId();
	}

	private FriendResponseDTO toDTO(Users u) {
		UserDetail d = u.getUsersDetail();
		return new FriendResponseDTO(
				u.getId(),
				u.getAccount(),
				d.getName(),
				Boolean.TRUE.equals(d.getShowEmail()) ? d.getEmail() : null,
				Boolean.TRUE.equals(d.getShowBirthday()) ? d.getBirthday() : null);
	}

	/* 發送好友邀請 */
	@Transactional
	public UUID sendFriendRequest(UUID currentUserId, UUID addresseeId) {
		if (currentUserId.equals(addresseeId)) {
			throw new BusinessException("不能加自己好友");
		}

		if (!uRepo.existsById(addresseeId)) {
			throw new ResourceNotFoundException("找不到使用者");
		}

		UUID[] pair = normalize(currentUserId, addresseeId);
		Users user1 = uRepo.getReferenceById(pair[0]);
		Users user2 = uRepo.getReferenceById(pair[1]);
		Users requester = currentUserId.equals(pair[0]) ? user1 : user2;

		Optional<FriendshipEntity> friendship = fRepo.findByUser1IdAndUser2Id(pair[0], pair[1]);
		if (friendship.isPresent()) {
			// 有一邊有寄送過邀請或其他狀態
			FriendshipEntity f = friendship.get();
			if (f.getStatus() == FriendshipStatus.ACCEPTED) {
				throw new BusinessException("已經是好友了");
			}
			if (f.getStatus() == FriendshipStatus.PENDING) {
				throw new BusinessException("已有待處理的請求");
			}
			throw new BusinessException("無法送出邀請");
		}
		// Empty 雙方之間沒有寄送過邀請或其他狀態
		FriendshipEntity f = new FriendshipEntity();
		f.setUser1(user1);
		f.setUser2(user2);
		f.setRequester(requester);
		f.setStatus(FriendshipStatus.PENDING);
		fRepo.save(f);
		UUID other = otherSide(f, currentUserId);
		return other;
	}

	/* 接受好友邀請 */
	@Transactional
	public UUID acceptFriendRequest(Long friendshipId, UUID currentUserId) {
		FriendshipEntity friendship = findPendingFriendship(friendshipId);

		validateRequestReceiver(friendship, currentUserId);
		friendship.setStatus(FriendshipStatus.ACCEPTED);
		fRepo.save(friendship);

		return otherSide(friendship, currentUserId);
	}

	/* 拒絕好友邀請 */
	@Transactional
	public UUID rejectFriendRequest(Long friendshipId, UUID currentUserId) {
		FriendshipEntity friendship = findPendingFriendship(friendshipId);

		// requester 不能接受自己的邀請
		validateRequestReceiver(friendship, currentUserId);
		UUID other = otherSide(friendship, currentUserId);
		fRepo.delete(friendship);
		return other;
	}

	/* 取得好友列表 */
	@Transactional(readOnly = true) 
	public List<FriendResponseDTO> getFriendsList(UUID currentUserId) {
		List<FriendshipEntity> friendsList = fRepo.findFriends(currentUserId, FriendshipStatus.ACCEPTED);

		return friendsList.stream().map(friendship -> {
			Users friend;
			if (friendship.getUser1().getId().equals(currentUserId)) {
				friend = friendship.getUser2();
			} else {
				friend = friendship.getUser1();
			}

			return toDTO(friend);
		}).toList();
	}

	/* 送出的好友邀請 */
	@Transactional(readOnly = true) 
	public List<FriendshipResponseDTO> getRequestList(UUID currentUserId) {
		List<FriendshipEntity> friendshipList = fRepo.findSentRequests(currentUserId,
				FriendshipStatus.PENDING);
		return friendshipList.stream().map(friendship -> {
			Users friend;
			if (friendship.getUser1().getId().equals(currentUserId)) {
				friend = friendship.getUser2();
			} else {
				friend = friendship.getUser1();
			}
			FriendResponseDTO friendDTO = toDTO(friend);
			return new FriendshipResponseDTO(friendship.getId(), friendship.getStatus(), friendDTO);
		}).toList();

	}

	/* 收到的好友邀請 */
	@Transactional(readOnly = true) 
	public List<FriendshipResponseDTO> getReceivedList(UUID currentUserId) {
		List<FriendshipEntity> friendshipList = fRepo.findPendingRequests(currentUserId);
		return friendshipList.stream().map(friendship -> {
			Users friend = friendship.getRequester();
			FriendResponseDTO friendDTO = toDTO(friend);
			return new FriendshipResponseDTO(friendship.getId(), friendship.getStatus(), friendDTO);
		}).toList();
	}

	/* 取消好友邀請 */
	@Transactional
	public UUID cancelFriendRequest(Long friendshipId, UUID currentUserId) {
		FriendshipEntity friendship = findPendingFriendship(friendshipId);
		validateRequester(friendship, currentUserId);
		UUID other = otherSide(friendship, currentUserId);
		fRepo.delete(friendship);
		return other;
	}

}
