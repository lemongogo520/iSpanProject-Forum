package forum.board.chat.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ConversationRepository extends JpaRepository<ConversationEntity,Long>{
    List<ConversationEntity> findByMembers(List<ConversationMemberEntity> members);

    @Query ("""
            FROM ConversationEntity c 
            JOIN ConversationMemberEntity m 
            ON c.id = m.conversation.id 
            AND m.user.id = :user1Id OR m.user.id = :user2Id
            """)
    Optional<ConversationEntity> findDirectConversation(UUID user1Id, UUID user2Id);


}
