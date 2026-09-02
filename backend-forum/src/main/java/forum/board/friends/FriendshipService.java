package forum.board.friends;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import forum.board.friends.Friendship.FriendshipStatus;
import forum.board.users.Users;
import forum.board.users.UsersRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FriendshipService {
	private final FriendshipRepository fRepo;
	private final UsersRepository uRepo;
	
	private UUID[] normalize(UUID a, UUID b) {
		//-1:目前 UUID 值小於目標值
		// 0:相等
		// 1:大於
		return a.compareTo(b)<0?new UUID[] {a,b} : new UUID[] {b,a};
	}
	
	/*請求*/
	public void sendRequest(UUID fromUserId, UUID toUserId) {
		UUID[] pair = normalize(fromUserId, toUserId);
		Optional<Users> o1 = uRepo.findById(pair[0]);
		Optional<Users> o2 = uRepo.findById(pair[1]);
		
		if(fromUserId.equals(toUserId)) {
			throw new IllegalStateException("不能加自己好友");
		}
		
		if(o1.isEmpty()||o2.isEmpty()) {
			throw new IllegalStateException("用戶不存在");
		}
		
		Optional<Friendship> relation = fRepo.findRelationBetween(pair[0], pair[1]);
		if(relation.isPresent()) {
			//有一邊有寄送過邀請或其他狀態
			Friendship f = relation.get();
			if(f.getStatus()==FriendshipStatus.ACCEPTED)
			{
				throw new IllegalStateException("已經是好友了");
			}
			if(f.getStatus() == FriendshipStatus.PENDING)
			{
				throw new IllegalStateException("已有待處理的請求");
			}
			if(f.getStatus() == FriendshipStatus.BLOCKED)
			{
				throw new IllegalStateException("雙方存在封鎖關係，無法發送請求");
			}	
			// rejected 重新送邀請
			f.setStatus(FriendshipStatus.PENDING);
			f.setRequester(uRepo.getReferenceById(fromUserId));
			fRepo.save(f);
		}
		// Empty 雙方之間沒有寄送過邀請或其他狀態
		Friendship f = new Friendship();
		f.setUser1(uRepo.getReferenceById(pair[0]));
		f.setUser2(uRepo.getReferenceById(pair[1]));
		f.setRequester(uRepo.getReferenceById(fromUserId));
		f.setStatus(FriendshipStatus.PENDING);
		fRepo.save(f);
	}
	
	public Friendship acceptRequest(UUID userId, Friendship friendshipId) {
		return null;
	}
	// --- 請求相關 ---
	/*	acceptRequest(userId: string, friendshipId: string): Promise<Friendship>;
		rejectRequest(userId: string, friendshipId: string): Promise<void>;
		cancelRequest(userId: string, friendshipId: string): Promise<void>;
		*/
	
	
	
	public Page<Friendship> getFriends(UUID userId, Integer pageNumber){
		Optional<Users> userOp = uRepo.findById(userId);
		if(userOp.isEmpty()) {
			throw new IllegalStateException("用戶不存在");
		}
		Pageable pgb = PageRequest.of(pageNumber-1, 5);
		Page<Friendship> friends = fRepo.findFriends(userId, pgb);
		if(friends.isEmpty()) {
			throw new IllegalStateException("還沒有好友");
		}
		return friends;
	}
	
	
	
	//TODO:
		/*
	

	// --- 關係管理 ---
	removeFriend(userId: string, friendId: string): Promise<void>;
	blockUser(blockerId: string, blockedId: string): Promise<void>;
	unblockUser(blockerId: string, blockedId: string): Promise<void>;

	// --- 查詢 ---
	getFriendship(userId1: string, userId2: string): Promise<Friendship | null>;
	getPendingRequestsReceived(userId: string): Promise<Friendship[]>;
	getPendingRequestsSent(userId: string): Promise<Friendship[]>;
	getFriendCount(userId: string): Promise<number>;
	isFriend(userId1: string, userId2: string): Promise<boolean>;
	isBlocked(userId1: string, userId2: string): Promise<boolean>;
	getMutualFriends(userId1: string, userId2: string): Promise<string[]>; // 共同好友
	}
		 * 
		 * 
		 * */
}
	
	
	

