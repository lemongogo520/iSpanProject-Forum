package forum.board.conversation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Long>{
    boolean existsByConversationIdAndUserId(
        Long conversationId,
        UUID userUuid
    );

    List<ConversationMember> findByUserId(UUID userUuid);

}
