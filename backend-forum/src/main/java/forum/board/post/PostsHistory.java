package forum.board.post;

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
@Table(name = "forum_posts_history")
@NoArgsConstructor
@Getter
@Setter
public class PostsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(name = "old_title")
    private String oldTitle;

    @Column(name = "old_content", columnDefinition = "nvarchar(max)")
    private String oldContent;

    @Column(name = "new_title")
    private String newTitle;

    @Column(name = "new_content", columnDefinition = "nvarchar(max)")
    private String newContent;

    @Column(nullable = false)
    private String action;

    @Column(name = "modify_time")
    private Instant modifyTime;

    @PrePersist
    public void onCreate() {
        if (modifyTime == null) {

            modifyTime = Instant.now();
        }
    }
}
