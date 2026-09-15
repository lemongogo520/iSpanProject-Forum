package forum.board.coin;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "member_daily_task",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_member_daily_task_user_date_type",
            columnNames = {
                "user_id",
                "task_date",
                "task_type"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class MemberDailyTask {

    @Id
    @Column(
        name = "id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID id;

    @Column(
        name = "user_id",
        nullable = false,
        columnDefinition = "uniqueidentifier"
    )
    private UUID userId;

    // 由 Service 按台灣時區取得日期。
    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "task_type",
        nullable = false,
        length = 30
    )
    private DailyTaskType taskType;

    // 目前完成次數。
    @Column(name = "progress_count", nullable = false)
    private Integer progressCount = 0;

    // 達標所需次數，建立任務時從設定複製。
    @Column(name = "target_count", nullable = false)
    private Integer targetCount;

    // 獎勵點數，建立任務時從設定複製。
    @Column(
        name = "reward_amount",
        nullable = false,
        columnDefinition = "bigint"
    )
    private Long rewardAmount;

    @Column(name = "claimed", nullable = false)
    private Boolean claimed = false;

    @Column(
        name = "claimed_time",
        columnDefinition = "datetimeoffset(7)"
    )
    private Instant claimedTime;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(
        name = "create_time",
        nullable = false,
        columnDefinition = "datetimeoffset(7)"
    )
    private Instant createTime;

    @Column(
        name = "modify_time",
        columnDefinition = "datetimeoffset(7)"
    )
    private Instant modifyTime;

    @PrePersist
    public void onCreate() {

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (progressCount == null) {
            progressCount = 0;
        }

        if (claimed == null) {
            claimed = false;
        }

        createTime = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        modifyTime = Instant.now();
    }
}