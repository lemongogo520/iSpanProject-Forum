package forum.board.chat.conversation;

import java.time.Instant;

import forum.board.users.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "conversation_member", uniqueConstraints = {
        @UniqueConstraint(name = "UK_conversation_user", columnNames = {
                "conversation_id",
                "user_id"
        })
})
public class ConversationMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Instant joinTime;

    @Column(nullable = false)
    private Instant lastReadTime;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (joinTime == null) {
            this.joinTime = now;
            this.lastReadTime = now;
        }
    }

}
