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
import org.springframework.transaction.annotation.Propagation;
import java.time.Instant;


@Service
public class DailyTaskService {

    private static final ZoneId TAIWAN_ZONE =
            ZoneId.of("Asia/Taipei");

    private final MemberDailyTaskRepository taskRepository;
    private final DailyTaskConfig taskConfig;
    private final EntityManager entityManager;
    private final DailyTaskLikeRecordRepository likeRecordRepository;
    private final CoinService coinService;
    private final CoinWalletRepository walletRepository;
    private final CoinTransactionRepository transactionRepository;
    private final DailyTaskContentRecordRepository contentRecordRepository;

    public DailyTaskService(
            MemberDailyTaskRepository taskRepository,
            DailyTaskConfig taskConfig,
            EntityManager entityManager,
            DailyTaskLikeRecordRepository likeRecordRepository,
            CoinService coinService,
            CoinWalletRepository walletRepository,
            CoinTransactionRepository transactionRepository,
            DailyTaskContentRecordRepository contentRecordRepository) {

        this.taskRepository = taskRepository;
        this.taskConfig = taskConfig;
        this.entityManager = entityManager;
        this.likeRecordRepository = likeRecordRepository;
        this.coinService = coinService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.contentRecordRepository = contentRecordRepository;
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
    
    /**
     * 由論壇後端在按讚成功後呼叫。
     *
     * 必須在呼叫端的交易內執行：
     * 按讚、來源紀錄、雙方任務進度一起成功或回滾。
     *
     * @return true：第一次有效按讚，已處理任務。
     *         false：自己按自己，或此按讚已計算過。
     *
     * 注意：true 不代表已發點，也不代表進度一定增加；
     *       當天任務可能已達標。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean recordLike(
            UUID actorUserId,
            UUID authorUserId,
            LikeTargetType targetType,
            String targetId) {

        if (actorUserId == null || authorUserId == null) {
            throw new IllegalArgumentException(
                    "按讚者與作者 ID 不可為空");
        }

        if (targetType == null) {
            throw new IllegalArgumentException(
                    "請指定被按讚的內容類型");
        }

        if (targetId == null || targetId.isBlank()) {
            throw new IllegalArgumentException(
                    "被按讚的內容 ID 不可為空");
        }

        String normalizedTargetId = targetId.strip();

        if (normalizedTargetId.length() > 100) {
            throw new IllegalArgumentException(
                    "內容 ID 不可超過 100 字");
        }

        // 自己對自己的內容按讚，不計入任務。
        if (actorUserId.equals(authorUserId)) {
            return false;
        }

        /*
         * 固定依 UUID 順序鎖定雙方，
         * 降低 A 按 B、B 按 A 同時發生時的鎖定衝突。
         */
        if (actorUserId.compareTo(authorUserId) < 0) {

            lockMember(actorUserId);
            lockMember(authorUserId);

        } else {

            lockMember(authorUserId);
            lockMember(actorUserId);
        }

        boolean alreadyRecorded = likeRecordRepository
                .existsByActorUserIdAndTargetTypeAndTargetId(
                        actorUserId,
                        targetType,
                        normalizedTargetId);

        if (alreadyRecorded) {
            return false;
        }

        /*
         * 即使今日任務已達標，也保留來源紀錄。
         * 避免明天取消後重新按讚，又拿來累積任務。
         */
        DailyTaskLikeRecord record =
                new DailyTaskLikeRecord();

        record.setActorUserId(actorUserId);
        record.setAuthorUserId(authorUserId);
        record.setTargetType(targetType);
        record.setTargetId(normalizedTargetId);

        likeRecordRepository.save(record);

        // 雙方使用同一天，避免午夜跨日出現不同日期。
        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        increaseLikeTaskProgress(
                actorUserId,
                today,
                DailyTaskType.GIVE_LIKE);

        increaseLikeTaskProgress(
                authorUserId,
                today,
                DailyTaskType.RECEIVE_LIKE);

        return true;
    }

    /**
     * 僅供已鎖定會員的 recordLike() 內部呼叫。
     */
    private void increaseLikeTaskProgress(
            UUID userId,
            LocalDate taskDate,
            DailyTaskType taskType) {

        MemberDailyTask task =
                getOrCreateTask(userId, taskDate, taskType);

        // 已領取或已達標，就不再增加進度。
        if (Boolean.TRUE.equals(task.getClaimed())
                || task.getProgressCount()
                        >= task.getTargetCount()) {
            return;
        }

        task.setProgressCount(
                task.getProgressCount() + 1);

        taskRepository.save(task);
    }
    
    /**
     * 手動領取每日任務獎勵。
     *
     * userId 必須由後端目前登入身分取得。
     * 獎勵金額讀取任務紀錄，不接受前端指定。
     */
    @Transactional
    public DailyTaskClaimResponse claimReward(
            UUID userId,
            UUID taskId) {

        if (taskId == null) {
            throw new IllegalArgumentException(
                    "任務 ID 不可為空");
        }

        /*
         * 沿用現有會員鎖。
         * 同一會員同時按兩次領取時，
         * 第二個請求需等待第一個交易結束。
         */
        lockMember(userId);

        // 1. 確認任務屬於這位會員。
        MemberDailyTask task = taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "找不到你的任務"));

        // 2. 只允許領取台灣日期的當天任務。
        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        if (!today.equals(task.getTaskDate())) {
            throw new IllegalArgumentException(
                    "只能領取今天的任務獎勵");
        }

        // 3. 確認任務目前有啟用。
        if (!taskConfig.getEnabledTaskTypes()
                .contains(task.getTaskType())) {

            throw new IllegalArgumentException(
                    "此任務目前未開放領獎");
        }

        // 4. 防止重複領取。
        if (Boolean.TRUE.equals(task.getClaimed())) {
            throw new IllegalArgumentException(
                    "此任務今天已經領取過獎勵");
        }

        // 5. 確認任務進度有效且已達標。
        if (task.getTargetCount() == null
                || task.getTargetCount() <= 0
                || task.getProgressCount() == null) {

            throw new IllegalStateException(
                    "任務進度設定異常");
        }

        if (task.getProgressCount()
                < task.getTargetCount()) {

            throw new IllegalArgumentException(
                    "任務尚未完成，無法領取獎勵");
        }

        /*
         * 6. 使用建立任務時保存的獎勵金額。
         * 目前兩項按讚任務都是 10 點。
         */
        Long rewardAmount = task.getRewardAmount();

        if (rewardAmount == null || rewardAmount <= 0) {
            throw new IllegalStateException(
                    "任務獎勵點數設定異常");
        }

        // 7. 取得錢包，尚未建立時由 CoinService 建立。
        CoinWallet wallet =
                coinService.getOrCreateWallet(userId);

        /*
         * 8. 額外核對是否已有這個任務的入帳紀錄。
         * referenceId 使用 MemberDailyTask.id。
         */
        boolean alreadyCredited = transactionRepository
                .existsByWalletIdAndTransactionTypeAndReferenceId(
                        wallet.getId(),
                        TransactionType.DAILY_TASK_REWARD,
                        task.getId());

        if (alreadyCredited) {
            throw new IllegalStateException(
                    "此任務已有入帳紀錄，請確認領獎狀態");
        }

        if (wallet.getBalance() == null) {
            throw new IllegalStateException(
                    "錢包餘額資料異常");
        }

        // 9. 計算新餘額，避免整數溢位。
        long balanceAfter = Math.addExact(
                wallet.getBalance(),
                rewardAmount);

        wallet.setBalance(balanceAfter);

        walletRepository.save(wallet);

        // 10. 建立點數交易紀錄。
        CoinTransaction transaction =
                new CoinTransaction();

        transaction.setWallet(wallet);

        transaction.setTransactionType(
                TransactionType.DAILY_TASK_REWARD);

        transaction.setAmount(rewardAmount);
        transaction.setBalanceAfter(balanceAfter);

        // 關聯本次會員每日任務。
        transaction.setReferenceId(task.getId());

        transaction.setDescription(
                getTaskName(task.getTaskType()) + "領獎");

        transactionRepository.save(transaction);

        // 11. 標記任務已領取。
        Instant claimedTime = Instant.now();

        task.setClaimed(true);
        task.setClaimedTime(claimedTime);

        taskRepository.save(task);

        /*
         * 提前同步本交易的變更，檢查資料庫限制與版本衝突。
         * flush 不是提交；發生例外仍會全部回滾。
         */
        entityManager.flush();

        return new DailyTaskClaimResponse(
                task.getId(),
                task.getTaskType(),
                rewardAmount,
                balanceAfter,
                "CLAIMED",
                claimedTime
        );
    }
    /**
     * 由會員後端在登入驗證成功後呼叫。
     *
     * 重複登入不重複累計，也不因今天已完成而拋出錯誤。
     * 呼叫端必須有交易。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void recordLogin(UUID userId) {

        lockMember(userId);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        MemberDailyTask task = getOrCreateTask(
                userId,
                today,
                DailyTaskType.LOGIN);

        // 登入屬於每日一次的動作，不因重送而累加。
        if (task.getProgressCount() == 0
                && !Boolean.TRUE.equals(task.getClaimed())) {

            task.setProgressCount(1);
            taskRepository.save(task);
        }
    }

    /**
     * 會員按下簽到後執行。
     *
     * 只完成簽到任務，不直接發放點數。
     */
    @Transactional
    public DailyTaskResponse checkIn(UUID userId) {

        lockMember(userId);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        MemberDailyTask task = getOrCreateTask(
                userId,
                today,
                DailyTaskType.CHECK_IN);

        if (task.getProgressCount() == 0
                && !Boolean.TRUE.equals(task.getClaimed())) {

            task.setProgressCount(1);
            taskRepository.save(task);
        }

        // 再次簽到，直接回傳目前狀態。
        return toResponse(task);
    }

    /**
     * 由論壇後端在文章新增成功後呼叫。
     * 不在編輯文章時呼叫。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean recordPost(
            UUID authorUserId,
            String postId) {

        return recordContent(
                authorUserId,
                DailyTaskType.POST,
                postId);
    }

    /**
     * 由論壇後端在留言或回覆新增成功後呼叫。
     *
     * sourceId 請使用：
     * COMMENT:留言ID
     * REPLY:回覆ID
     *
     * 防止不同資料表出現相同數字 ID。
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean recordComment(
            UUID authorUserId,
            String sourceId) {

        return recordContent(
                authorUserId,
                DailyTaskType.COMMENT,
                sourceId);
    }

    /**
     * 新增內容的共同進度處理。
     *
     * true：第一次收到此內容，已記錄來源。
     * false：此內容已處理。
     *
     * true 不表示已發點，也不表示進度必然增加。
     */
    private boolean recordContent(
            UUID userId,
            DailyTaskType taskType,
            String sourceId) {

        if (sourceId == null || sourceId.isBlank()) {
            throw new IllegalArgumentException(
                    "文章或留言 ID 不可為空");
        }

        String normalizedSourceId = sourceId.strip();

        if (normalizedSourceId.length() > 100) {
            throw new IllegalArgumentException(
                    "來源 ID 不可超過 100 字");
        }

        lockMember(userId);

        boolean alreadyRecorded = contentRecordRepository
                .existsByTaskTypeAndSourceId(
                        taskType,
                        normalizedSourceId);

        if (alreadyRecorded) {
            return false;
        }

        /*
         * 即使今天已達標，仍記錄新內容。
         * 避免明天重送相同事件時，又拿來累積任務。
         */
        DailyTaskContentRecord record =
                new DailyTaskContentRecord();

        record.setUserId(userId);
        record.setTaskType(taskType);
        record.setSourceId(normalizedSourceId);

        contentRecordRepository.save(record);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        MemberDailyTask task = getOrCreateTask(
                userId,
                today,
                taskType);

        if (!Boolean.TRUE.equals(task.getClaimed())
                && task.getProgressCount()
                        < task.getTargetCount()) {

            task.setProgressCount(
                    task.getProgressCount() + 1);

            taskRepository.save(task);
        }

        return true;
    }
    
    
    
    
}