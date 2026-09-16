package forum.board.chat.conversation;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import forum.board.users.Users;



public interface ConversationMemberRepository extends JpaRepository<ConversationMemberEntity, Long>{

    List<ConversationMemberEntity> findByUser(Users user);

    Optional<ConversationMemberEntity> findByConversationIdAndUserId(Long conversationId, UUID userId);


}
