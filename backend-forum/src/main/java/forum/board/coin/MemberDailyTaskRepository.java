package forum.board.coin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDailyTaskRepository
        extends JpaRepository<MemberDailyTask, UUID> {

    List<MemberDailyTask>
        findByUserIdAndTaskDateOrderByTaskTypeAsc(
            UUID userId,
            LocalDate taskDate
        );

    Optional<MemberDailyTask>
        findByUserIdAndTaskDateAndTaskType(
            UUID userId,
            LocalDate taskDate,
            DailyTaskType taskType
        );

    Optional<MemberDailyTask>
        findByIdAndUserId(
            UUID id,
            UUID userId
        );
}