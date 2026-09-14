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
    name = "daily_task_content_record",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_task_content_type_source",
            columnNames = {
                "task_type",
                "source_id"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class DailyTaskContentRecord {

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

    @Enumerated(EnumType.STRING)
    @Column(
        name = "task_type",
        nullable = false,
        length = 30
    )
    private DailyTaskType taskType;

    @Column(
        name = "source_id",
        nullable = false,
        length = 100
    )
    private String sourceId;

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