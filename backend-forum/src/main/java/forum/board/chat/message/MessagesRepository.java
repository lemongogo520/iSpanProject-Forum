package forum.board.chat.message;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface MessagesRepository extends JpaRepository<MessagesEntity, Long> {

    MessagesEntity findByConversationId(Long conversationId);

    List<MessagesEntity> findLast50ByConversationIdOrderByCreateTimeAsc(Long conversationId);
    
}
