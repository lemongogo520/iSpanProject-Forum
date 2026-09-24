package forum.board.chat.conversation;

import java.time.Instant;
import java.util.List;

import forum.board.board.Board;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "conversation")
public class ConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConversationType type;

    public enum ConversationType {
        DIRECT,
        BOARD
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", unique = true)
    private Board board;

    @OneToMany(mappedBy = "conversation")
    private List<ConversationMemberEntity> members;

    private String lastMessage;

    @Column(nullable = false, updatable = false)
    private Instant createTime;

    @Column(nullable = false)
    private Instant updateTime;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createTime == null) {
            this.createTime = now;
            this.updateTime = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = Instant.now();
    }

}
