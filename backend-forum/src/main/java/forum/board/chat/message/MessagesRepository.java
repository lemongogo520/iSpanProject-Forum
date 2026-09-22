package forum.board.chat.message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface MessagesRepository extends JpaRepository<MessagesEntity, Long> {

    Optional<MessagesEntity> findByConversationId(Long conversationId);

    @Query("""
    SELECT COUNT(m)
    FROM MessagesEntity m
    WHERE m.conversation.id = :conversationId
      AND m.createTime > :lastReadTime
      AND m.sender.id <> :userId
    """)
    Long countUnreadMessages(
        @Param("conversationId") Long conversationId,
        @Param("lastReadTime") Instant lastReadTime,
        @Param("userId") UUID userId
    );
    

    Optional<MessagesEntity> findByConversationIdAndClientMsgId(
        Long conversationId, String clientMsgId);

    @Query("""
        FROM MessagesEntity m
        JOIN FETCH m.sender s
        JOIN FETCH s.usersDetail
        WHERE m.conversation.id = :conversationId
        AND m.deleteTime IS NULL
        AND (:before IS NULL OR m.id < :before)
        ORDER BY m.id DESC
        """)
    List<MessagesEntity> findPage(
        @Param("conversationId") Long conversationId,
        @Param ("before") Long before,
        Pageable pageable);
    
}
