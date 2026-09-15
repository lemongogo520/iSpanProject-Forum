package forum.board.coin;

import java.time.LocalDate;
import java.util.UUID;

public record DailyTaskResponse(
        UUID id,
        LocalDate taskDate,
        DailyTaskType taskType,
        String taskName,
        Integer progressCount,
        Integer targetCount,
        Long rewardAmount,
        String status
) {
}