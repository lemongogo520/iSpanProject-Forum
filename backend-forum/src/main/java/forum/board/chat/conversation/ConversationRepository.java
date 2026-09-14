package forum.board.chat.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ConversationRepository extends JpaRepository<ConversationEntity,Long>{
    List<ConversationEntity> findByMembers(List<ConversationMemberEntity> members);
}
