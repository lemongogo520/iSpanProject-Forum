package forum.board.comments;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_comments_history")
@NoArgsConstructor
@Getter
@Setter
public class CommentsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comment_id", nullable = false)
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "old_content")
    private String oldContent;

    private String action;

    @Column(name = "new_content")
    private String newContent;

    private Instant modifyTime;

    @PrePersist
    public void onCreate() {

        if (modifyTime == null) {

            modifyTime = Instant.now();
        }
    }

}
