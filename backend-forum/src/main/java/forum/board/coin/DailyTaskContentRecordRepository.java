package forum.board.coin;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTaskContentRecordRepository
        extends JpaRepository<DailyTaskContentRecord, UUID> {

    boolean existsByTaskTypeAndSourceId(
            DailyTaskType taskType,
            String sourceId
    );
}