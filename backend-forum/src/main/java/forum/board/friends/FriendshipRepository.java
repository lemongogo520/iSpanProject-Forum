package forum.board.friends;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import forum.board.users.Users;



public interface FriendshipRepository extends JpaRepository<Friendship, UUID>{
	@Query("""
			from Friendship f 
			where(f.user1.id = :user1Id and f.user2.id = :user2Id) 
			or (f.user1.id = :user2Id and f.user2.id = :user1Id)
			""")
	Optional<Friendship> findRelationBetween(UUID user1Id, UUID user2Id);
	//檢查雙向關係 
	
	
	Set<Friendship> findByRequester(Users user);
	
	@Query("""
	        from Friendship f
	        JOIN FETCH f.user1
	        JOIN FETCH f.user2
	        WHERE (f.user1.id = :userId OR f.user2.id = :userId)
	          AND f.status = 'ACCEPTED'
	    """)
	    Page<Friendship> findFriends(@Param("userId") UUID userId, Pageable pageable);
	
	@Query("""
			select count(*)
			from Friendship f 
			where(f.user1.id = :userId OR f.user2.id = :userId)
	          AND f.status = 'ACCEPTED'
			""")
	
	long countFriends(UUID userId);
}
