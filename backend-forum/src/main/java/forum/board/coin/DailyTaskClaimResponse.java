package forum.board.coin;

import java.time.Instant;
import java.util.UUID;

public record DailyTaskClaimResponse(
        UUID taskId,
        DailyTaskType taskType,
        Long rewardAmount,
        Long balanceAfter,
        String status,
        Instant claimedTime
) {
}