package forum.board.chat.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import forum.board.board.Board;



public interface ConversationRepository extends JpaRepository<ConversationEntity,Long>{
    List<ConversationEntity> findByMembers(List<ConversationMemberEntity> members);

    @Query ("""
            FROM ConversationEntity c 
            JOIN ConversationMemberEntity m1 
            ON c.id = m1.conversation.id 
            JOIN ConversationMemberEntity m2 
            ON c.id = m2.conversation.id 
            WHERE m1.user.id != m2.user.id
            AND m1.user.id = :user1Id AND m2.user.id = :user2Id
            """)
    Optional<ConversationEntity> findDirectConversation(UUID user1Id, UUID user2Id);

    Optional<ConversationEntity> findByBoard(Board board);






}
