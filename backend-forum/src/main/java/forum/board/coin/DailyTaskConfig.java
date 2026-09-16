package forum.board.coin;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DailyTaskConfig {

    public List<DailyTaskType> getEnabledTaskTypes() {

        return List.of(
                DailyTaskType.LOGIN,
                DailyTaskType.CHECK_IN,
                DailyTaskType.POST,
                DailyTaskType.COMMENT,
                DailyTaskType.GIVE_LIKE,
                DailyTaskType.RECEIVE_LIKE
        );
    }

    public int getTargetCount(DailyTaskType taskType) {

        validateEnabled(taskType);

        return switch (taskType) {

            case GIVE_LIKE, RECEIVE_LIKE -> 3;

            case LOGIN, CHECK_IN, POST, COMMENT -> 1;
        };
    }

    public long getRewardAmount(DailyTaskType taskType) {

        validateEnabled(taskType);

        return switch (taskType) {

            // 已確認的規則。
            case GIVE_LIKE, RECEIVE_LIKE -> 10L;

            // 開發暫定值，待小組確認。
            case LOGIN, CHECK_IN, POST, COMMENT -> 10L;
        };
    }

    private void validateEnabled(DailyTaskType taskType) {

        if (taskType == null
                || !getEnabledTaskTypes().contains(taskType)) {

            throw new IllegalArgumentException(
                    "此每日任務尚未啟用");
        }
    }
}