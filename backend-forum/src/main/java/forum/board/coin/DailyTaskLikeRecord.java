package forum.board.coin;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "daily_task_like_record",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_task_like_actor_target",
            columnNames = {
                "actor_user_id",
                "target_type",
                "target_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class DailyTaskLikeRecord {

    @Id
    @Column(
        name = "id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID id;

    // 按讚者。
    @Column(
        name = "actor_user_id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID actorUserId;

    // 被按讚內容的作者。
    @Column(
        name = "author_user_id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID authorUserId;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "target_type",
        nullable = false,
        length = 20
    )
    private LikeTargetType targetType;

    // 使用字串，方便串接 UUID 或數字型的內容 ID。
    @Column(
        name = "target_id",
        nullable = false,
        length = 100
    )
    private String targetId;

    @Column(
        name = "create_time",
        nullable = false,
        columnDefinition = "datetimeoffset(7)"
    )
    private Instant createTime;

    @PrePersist
    public void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        createTime = Instant.now();
    }
}