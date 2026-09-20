package forum.board.chat.conversation;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;




public interface ConversationMemberRepository extends JpaRepository<ConversationMemberEntity, Long>{
    Optional<ConversationMemberEntity> findByConversationIdAndUserId(Long conversationId, UUID userId);

    Boolean existsByConversationIdAndUserId(Long conversationId, UUID userId);

    @Query ("""
            SELECT cm.conversation
            FROM ConversationMemberEntity cm
            WHERE cm.user.id = :userId
            ORDER BY cm.conversation.updateTime DESC
            """)
    List<ConversationEntity> findByUserId(UUID userId);

}
