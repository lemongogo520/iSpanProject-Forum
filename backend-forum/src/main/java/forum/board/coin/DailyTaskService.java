package forum.board.coin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.users.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@Service
public class DailyTaskService {

    private static final ZoneId TAIWAN_ZONE =
            ZoneId.of("Asia/Taipei");

    private final MemberDailyTaskRepository taskRepository;
    private final DailyTaskConfig taskConfig;
    private final EntityManager entityManager;

    public DailyTaskService(
            MemberDailyTaskRepository taskRepository,
            DailyTaskConfig taskConfig,
            EntityManager entityManager) {

        this.taskRepository = taskRepository;
        this.taskConfig = taskConfig;
        this.entityManager = entityManager;
    }

    /**
     * 查詢今天的任務。
     * 如果尚未建立，就先建立。
     *
     * userId 必須由後端的登入身分取得。
     */
    @Transactional
    public List<DailyTaskResponse> getTodayTasks(
            UUID userId) {

        lockMember(userId);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        for (DailyTaskType taskType
                : taskConfig.getEnabledTaskTypes()) {

            getOrCreateTask(userId, today, taskType);
        }

        return taskRepository
                .findByUserIdAndTaskDateOrderByTaskTypeAsc(
                        userId,
                        today)
                .stream()
                .filter(task ->
                        taskConfig.getEnabledTaskTypes()
                                .contains(task.getTaskType()))
                .map(this::toResponse)
                .toList();
    }

    /**
     * 鎖定會員資料直到交易結束。
     *
     * 同一會員同時開啟兩個任務頁時，
     * 讓此方法的任務初始化依序執行，
     * 避免同時查不到任務後重複建立。
     */
    private void lockMember(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "會員 ID 不可為空");
        }

        Users user = entityManager.find(
                Users.class,
                userId,
                LockModeType.PESSIMISTIC_WRITE
        );

        if (user == null) {
            throw new IllegalArgumentException(
                    "找不到指定會員");
        }
    }

    /**
     * 已存在就沿用，不覆蓋當天的進度與獎勵設定。
     */
    private MemberDailyTask getOrCreateTask(
            UUID userId,
            LocalDate taskDate,
            DailyTaskType taskType) {

        return taskRepository
                .findByUserIdAndTaskDateAndTaskType(
                        userId,
                        taskDate,
                        taskType)
                .orElseGet(() -> {

                    MemberDailyTask task =
                            new MemberDailyTask();

                    task.setUserId(userId);
                    task.setTaskDate(taskDate);
                    task.setTaskType(taskType);

                    task.setProgressCount(0);

                    task.setTargetCount(
                            taskConfig.getTargetCount(taskType));

                    task.setRewardAmount(
                            taskConfig.getRewardAmount(taskType));

                    task.setClaimed(false);

                    return taskRepository.save(task);
                });
    }

    private DailyTaskResponse toResponse(
            MemberDailyTask task) {

        String status;

        if (Boolean.TRUE.equals(task.getClaimed())) {

            status = "CLAIMED";

        } else if (task.getProgressCount()
                >= task.getTargetCount()) {

            status = "CLAIMABLE";

        } else {

            status = "IN_PROGRESS";
        }

        return new DailyTaskResponse(
                task.getId(),
                task.getTaskDate(),
                task.getTaskType(),
                getTaskName(task.getTaskType()),
                task.getProgressCount(),
                task.getTargetCount(),
                task.getRewardAmount(),
                status
        );
    }

    private String getTaskName(DailyTaskType taskType) {

        return switch (taskType) {

            case LOGIN -> "每日登入";
            case CHECK_IN -> "每日簽到";
            case POST -> "發表文章";
            case COMMENT -> "回覆／留言";
            case GIVE_LIKE -> "每日按讚";
            case RECEIVE_LIKE -> "每日被按讚";
        };
    }
}