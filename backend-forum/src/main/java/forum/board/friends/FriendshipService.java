package forum.board.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.conversation.ConversationUserRepository;
import forum.board.friends.FriendshipEntity.FriendshipStatus;
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
	
	/*發送好友邀請*/
	public void sendFriendRequest(UUID requesterId, UUID addresseeId) {
		if(requesterId.equals(addresseeId)) {
			throw new RuntimeException("不能加自己好友");
		}

		UUID[] pair = normalize(requesterId, addresseeId);
		Optional<Users> o1 = uRepo.findById(pair[0]);
		Optional<Users> o2 = uRepo.findById(pair[1]);
		
		if(o1.isEmpty()||o2.isEmpty()) {
			throw new RuntimeException("用戶不存在");
		}
		
		Optional<FriendshipEntity> relation = fRepo.findByUser1IdAndUser2Id(pair[0], pair[1]);
		if(relation.isPresent()) {
			//有一邊有寄送過邀請或其他狀態
			FriendshipEntity f = relation.get();
			if(f.getStatus()==FriendshipStatus.ACCEPTED)
			{
				throw new RuntimeException("已經是好友了");
			}
			if(f.getStatus() == FriendshipStatus.PENDING)
			{
				throw new RuntimeException("已有待處理的請求");
			}
		}
		// Empty 雙方之間沒有寄送過邀請或其他狀態
		FriendshipEntity f = new FriendshipEntity();
		f.setUser1(uRepo.getReferenceById(pair[0]));
		f.setUser2(uRepo.getReferenceById(pair[1]));
		f.setRequester(uRepo.findById(requesterId).get());
		f.setStatus(FriendshipStatus.PENDING);
		fRepo.save(f);
	}
	
	/* 接受好友邀請 */
	public void acceptFriendRequest(Long friendshipId, UUID currentUserId) {
		// Friendship 是否存在 
		FriendshipEntity friendship = fRepo.findById(friendshipId)
				.orElseThrow(()->new RuntimeException("找不到好友邀請"));

		// status 是否為 PENDING
		if(friendship.getStatus()!= FriendshipStatus.PENDING){
			throw new RuntimeException( "好友關係不是pending" );
		}

		// requester 不能接受自己的邀請 
		if (friendship.getRequester().getId().equals(currentUserId)) { 
			throw new RuntimeException( "不能接受自己的邀請" ); }

		// 確認目前使用者是好友關係中的其中一人 
		boolean isMember = friendship.getUser1().getId().equals(currentUserId)
					 || friendship.getUser2() .getId() .equals(currentUserId);

		if (!isMember) { throw new RuntimeException( "拒絕存取" ); }
		friendship.setStatus( FriendshipStatus.ACCEPTED );
		fRepo.save(friendship);
	}
	
	/* 拒絕好友邀請 */
	public void rejectFriendRequest(Long friendshipId, UUID currentUserId){
		// Friendship 是否存在 
		FriendshipEntity friendship = fRepo.findById(friendshipId)
				.orElseThrow(()->new RuntimeException("找不到好友邀請"));

		// status 是否為 PENDING
		if(friendship.getStatus()!= FriendshipStatus.PENDING){
			throw new RuntimeException( "好友關係不是pending" );
		}

		// requester 不能接受自己的邀請 
		if (friendship.getRequester().getId().equals(currentUserId)) { 
			throw new RuntimeException( "不能接受自己的邀請" ); }

		// 確認目前使用者是好友關係中的其中一人 
		boolean isMember = friendship.getUser1().getId().equals(currentUserId)
					 || friendship.getUser2() .getId() .equals(currentUserId);

		if (!isMember) { throw new RuntimeException( "拒絕存取" ); }
		fRepo.delete(friendship);

	}
	
	/* 取得好友列表 */
	public List<FriendResponse> getFriendsList(UUID currentUserId){
		Optional<Users> userOp = uRepo.findById(currentUserId);
		if(userOp.isEmpty()) {
			throw new RuntimeException("用戶不存在");
		}

		List<FriendshipEntity> friends = fRepo.findFriends(currentUserId, FriendshipStatus.ACCEPTED);
		if(friends.isEmpty()) {
			throw new RuntimeException("還沒有好友");
		}

		return friends.stream().map(friendship ->{
			Users friend;
			if(friendship.getUser1().getId().equals(currentUserId)){
				friend = friendship.getUser2();
			}else{
				friend = friendship.getUser1();
			}

			return new FriendResponse(friend.getId(), friend.getAccount());
		}).toList();
	}
	
	
}
	
	
	

