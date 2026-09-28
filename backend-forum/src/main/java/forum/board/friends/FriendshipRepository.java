package forum.board.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import forum.board.friends.FriendshipEntity.FriendshipStatus;



public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long>{
	// 檢查雙向關係 
	Optional<FriendshipEntity> findByUser1IdAndUser2Id( UUID user1Id, UUID user2Id );
	
	// 指定user的好友關係
	@Query("""
	        FROM FriendshipEntity f
			JOIN FETCH f.user1 u1 JOIN FETCH u1.usersDetail
    		JOIN FETCH f.user2 u2 JOIN FETCH u2.usersDetail
	        WHERE (f.user1.id = :userId OR f.user2.id = :userId)
	          AND f.status = :status
	    """)
	List<FriendshipEntity> findFriends(
		@Param("userId") UUID userId
		,@Param("status") FriendshipStatus status); 
	
	/* 某user收到的待處理好友邀請
		requester 是發送邀請的人 
		透過 user1 / user2 找出誰是接收者*/
	@Query(""" 
		FROM FriendshipEntity f 
		JOIN FETCH f.requester r JOIN FETCH r.usersDetail
		WHERE f.status = 'PENDING' 
		AND f.requester.id <> :userId 
		AND ( f.user1.id = :userId OR f.user2.id = :userId ) """) 
	List<FriendshipEntity> findPendingRequests( @Param("userId") UUID userId );

	/* user自己送出的待處理好友邀請 */ 
	@Query("""
    SELECT f FROM FriendshipEntity f
    JOIN FETCH f.user1 u1 JOIN FETCH u1.usersDetail
    JOIN FETCH f.user2 u2 JOIN FETCH u2.usersDetail
    WHERE f.requester.id = :userId AND f.status = :status
    """)
	List<FriendshipEntity> findSentRequests(@Param("userId") UUID userId,
											@Param("status") FriendshipStatus status);
}
