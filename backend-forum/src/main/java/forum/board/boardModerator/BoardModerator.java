package forum.board.boardModerator;

import java.time.Instant;
import java.util.UUID;

import forum.board.board.Board;
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

@Entity
@Table(name = "forum_board_moderator", uniqueConstraints = {
        @UniqueConstraint(name = "uk_board_moderator_board_user", columnNames = { "board_id", "user_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class BoardModerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "creator", nullable = false)
    private UUID creator;

    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @PrePersist
    public void onCreate() {
        if (createTime == null) {
            createTime = Instant.now();
        }
    }

}
