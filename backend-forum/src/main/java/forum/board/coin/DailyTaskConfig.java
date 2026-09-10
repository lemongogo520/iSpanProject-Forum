package forum.board.coin;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DailyTaskConfig {

    /**
     * 目前啟用的每日任務。
     */
    public List<DailyTaskType> getEnabledTaskTypes() {

        return List.of(
                DailyTaskType.GIVE_LIKE,
                DailyTaskType.RECEIVE_LIKE
        );
    }

    /**
     * 任務達標次數。
     */
    public int getTargetCount(DailyTaskType taskType) {

        validateEnabled(taskType);

        return 3;
    }

    /**
     * 任務獎勵點數。
     */
    public long getRewardAmount(DailyTaskType taskType) {

        validateEnabled(taskType);

        return 10L;
    }

    private void validateEnabled(DailyTaskType taskType) {

        if (taskType == null
                || !getEnabledTaskTypes().contains(taskType)) {

            throw new IllegalArgumentException(
                    "此每日任務尚未啟用");
        }
    }
}