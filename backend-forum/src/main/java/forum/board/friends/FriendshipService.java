package forum.board.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.conversation.ConversationUserRepository;
import forum.board.friends.FriendshipEntity.FriendshipStatus;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.exception.BusinessException;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.Users;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FriendshipService {
	private final FriendshipRepository fRepo;
	private final ConversationUserRepository uRepo;
	
	private UUID[] normalize(UUID a, UUID b) {
		//-1:目前 UUID 值小於目標值
		// 0:相等
		// 1:大於
		return a.compareTo(b)<0?new UUID[] {a,b} : new UUID[] {b,a};
	}

	private Users findUser(UUID userId){
		return uRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
	}

	private FriendshipEntity findPendingFriendship(Long friendshipId) {
		FriendshipEntity friendship = fRepo.findById(friendshipId).orElseThrow(()->new ResourceNotFoundException("找不到好友邀請"));
		if(friendship.getStatus() != FriendshipStatus.PENDING){
			throw new BusinessException("此好友邀請目前不是待處理狀態");
		}
		return friendship;
	}

	private void validateRequestReceiver(FriendshipEntity friendship, UUID currentUserId) {
		// requester 不能接受自己的邀請 
		if (friendship.getRequester().getId().equals(currentUserId)) { 
			throw new BusinessException( "不能操作自己發送的邀請" ); }

		// 確認目前使用者是好友關係中的接收者
		boolean isMember = friendship.getUser1().getId().equals(currentUserId)
					 || friendship.getUser2() .getId() .equals(currentUserId);

		if (!isMember) { throw new BusinessException( "沒有權限操作此好友邀請" ); }

	}

	
	/*發送好友邀請*/
	public void sendFriendRequest(UUID requesterId, UUID addresseeId) {
		if(requesterId.equals(addresseeId)) {
			throw new BusinessException("不能加自己好友");
		}

		UUID[] pair = normalize(requesterId, addresseeId);
		Users requester = findUser(requesterId);
		Users user1 = findUser(pair[0]);
		Users user2 = findUser(pair[1]);
		
		Optional<FriendshipEntity> friendship = fRepo.findByUser1IdAndUser2Id(pair[0], pair[1]);
		if(friendship.isPresent()) {
			//有一邊有寄送過邀請或其他狀態
			FriendshipEntity f = friendship.get();
			if(f.getStatus()==FriendshipStatus.ACCEPTED)
			{
				throw new BusinessException("已經是好友了");
			}
			if(f.getStatus() == FriendshipStatus.PENDING)
			{
				throw new BusinessException("已有待處理的請求");
			}
			return ;
		}
		// Empty 雙方之間沒有寄送過邀請或其他狀態
		FriendshipEntity f = new FriendshipEntity();
		f.setUser1(user1);
		f.setUser2(user2);
		f.setRequester(requester);
		f.setStatus(FriendshipStatus.PENDING);
		fRepo.save(f);
	}
	
	/* 接受好友邀請 */
	public void acceptFriendRequest(Long friendshipId, UUID currentUserId) {
		FriendshipEntity friendship = findPendingFriendship(friendshipId);

		validateRequestReceiver(friendship, currentUserId);
		friendship.setStatus( FriendshipStatus.ACCEPTED );
		fRepo.save(friendship);
	}
	
	/* 拒絕好友邀請 */
	public void rejectFriendRequest(Long friendshipId, UUID currentUserId){
		FriendshipEntity friendship = findPendingFriendship(friendshipId);

		// requester 不能接受自己的邀請 
		if (friendship.getRequester().getId().equals(currentUserId)) { 
			throw new BusinessException( "不能接受自己的邀請" ); }

		// 確認目前使用者是好友關係中的其中一人 
		boolean isMember = friendship.getUser1().getId().equals(currentUserId)
					 || friendship.getUser2() .getId() .equals(currentUserId);

		if (!isMember) { throw new BusinessException( "拒絕存取" ); }
		fRepo.delete(friendship);

	}
	
	/* 取得好友列表 */
	public List<FriendResponseDTO> getFriendsList(UUID currentUserId){
		findUser(currentUserId);
		List<FriendshipEntity> friendsList = fRepo.findFriends(currentUserId, FriendshipStatus.ACCEPTED);
		
		return friendsList.stream().map(friendship ->{
			Users friend;
			if(friendship.getUser1().getId().equals(currentUserId)){
				friend = friendship.getUser2();
			}else{
				friend = friendship.getUser1();
			}

			return new FriendResponseDTO(friend.getId(), friend.getAccount(), null);
		}).toList();
	}
	
	
}
	
	
	

