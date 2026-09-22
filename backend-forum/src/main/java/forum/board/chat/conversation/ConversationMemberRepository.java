package forum.board.chat.conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMemberEntity, Long> {
    Optional<ConversationMemberEntity> findByConversationIdAndUserId(Long conversationId, UUID userId);

    Boolean existsByConversationIdAndUserId(Long conversationId, UUID userId);

    @Query("""
            SELECT cm.conversation
            FROM ConversationMemberEntity cm
            WHERE cm.user.id = :userId
            ORDER BY cm.conversation.updateTime DESC
            """)
    List<ConversationEntity> findByUserId(UUID userId);

    @Query("""
            SELECT m.conversation.id, COUNT(msg)
            FROM ConversationMemberEntity m
            LEFT JOIN MessagesEntity msg
              ON msg.conversation = m.conversation
             AND msg.sender.id <> :userId
             AND msg.deleteTime IS NULL
             AND (m.lastReadTime IS NULL OR msg.createTime > m.lastReadTime)
            WHERE m.user.id = :userId
            AND m.conversation.type = ConversationType.DIRECT
            GROUP BY m.conversation.id
            """)
    List<Object[]> countUnreadByUser(@Param("userId") UUID userId);

}
