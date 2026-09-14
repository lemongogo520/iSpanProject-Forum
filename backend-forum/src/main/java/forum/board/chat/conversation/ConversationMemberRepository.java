package forum.board.chat.conversation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMemberRepository extends JpaRepository<ConversationMemberEntity, Long>{
    boolean existsByConversationIdAndUserId(
        Long conversationId,
        UUID userUuid
    );

    List<ConversationMemberEntity> findByUserId(UUID userUuid);

}
