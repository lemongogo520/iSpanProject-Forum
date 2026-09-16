package forum.board.coin;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import forum.board.users.Users;
import forum.board.users.UsersRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
        "spring.jpa.properties.hibernate.cache.use_query_cache=false"
    }
)
@Transactional
@Rollback
class DailyTaskSqlServerTest {

    // 改成你本機資料庫中存在的測試帳號。
    private static final String TEST_ACCOUNT =
            "zzz123";

    private static final ZoneId TAIWAN_ZONE =
            ZoneId.of("Asia/Taipei");

    @Autowired
    private DailyTaskService dailyTaskService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MemberDailyTaskRepository taskRepository;

    @Autowired
    private CoinWalletRepository walletRepository;

    @Autowired
    private CoinTransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * 兩項任務各測試一次。
     * 每次測試各自執行並回滾。
     */
    @ParameterizedTest(name = "{0}：領獎後三份資料一致")
    @EnumSource(
        value = DailyTaskType.class,
        names = {"GIVE_LIKE", "RECEIVE_LIKE"}
    )
    void claimUpdatesWalletTransactionAndTask(
            DailyTaskType taskType) {

        // 1. 使用真實資料庫中的測試會員。
        Users user = usersRepository
                .findByAccount(TEST_ACCOUNT)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "找不到測試會員，請修改 TEST_ACCOUNT"));

        UUID userId = user.getId();

        // 2. 建立或取得今天的每日任務。
        dailyTaskService.getTodayTasks(userId);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        MemberDailyTask task = taskRepository
                .findByUserIdAndTaskDateAndTaskType(
                        userId,
                        today,
                        taskType)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "找不到今天的任務"));

        // 不重設已領過的任務。
        assertFalse(
                Boolean.TRUE.equals(task.getClaimed()),
                "此測試會員今天已領過這項任務，請換測試帳號");

        assertEquals(
                3,
                task.getTargetCount().intValue(),
                "任務目標應為 3 次");

        assertEquals(
                10L,
                task.getRewardAmount().longValue(),
                "任務獎勵應為 10 點");

        // 3. 取得真實錢包與原始餘額。
        CoinWallet wallet =
                coinService.getOrCreateWallet(userId);

        UUID walletId = wallet.getId();
        UUID taskId = task.getId();

        long balanceBefore = wallet.getBalance();

        boolean alreadyCredited = transactionRepository
                .existsByWalletIdAndTransactionTypeAndReferenceId(
                        walletId,
                        TransactionType.DAILY_TASK_REWARD,
                        taskId);

        assertFalse(
                alreadyCredited,
                "此任務已有入帳紀錄，請換測試帳號");

        /*
         * 4. 在測試交易中準備達標狀態。
         *
         * 此測試驗證領獎，不驗證論壇按讚。
         * 測試结束後，此進度修改也會回滾。
         */
        task.setProgressCount(3);
        taskRepository.save(task);

        entityManager.flush();
        entityManager.clear();

        // 5. 呼叫真實 Service 領獎。
        DailyTaskClaimResponse response =
                dailyTaskService.claimReward(
                        userId,
                        taskId);

        assertEquals(
                10L,
                response.rewardAmount().longValue());

        assertEquals("CLAIMED", response.status());

        /*
         * 6. 將變更寫入 SQL Server，
         * 清除 JPA 快取後重新查詢。
         *
         * 這裡仍未提交交易。
         */
        entityManager.flush();
        entityManager.clear();

        // 7. 從資料庫重新讀取錢包。
        CoinWallet savedWallet = walletRepository
                .findById(walletId)
                .orElseThrow();

        assertEquals(
                balanceBefore + 10L,
                savedWallet.getBalance().longValue(),
                "資料庫餘額應增加 10 點");

        // 8. 從資料庫重新讀取任務。
        MemberDailyTask savedTask = taskRepository
                .findByIdAndUserId(taskId, userId)
                .orElseThrow();

        assertTrue(
                Boolean.TRUE.equals(savedTask.getClaimed()),
                "任務應標記為已領取");

        assertNotNull(
                savedTask.getClaimedTime(),
                "任務應有領取時間");

        assertEquals(
                3,
                savedTask.getProgressCount().intValue());

        /*
         * 9. 從資料庫查出這次任務的所有入帳紀錄，
         * 確認只有一筆。
         */
        List<CoinTransaction> rewards =
                transactionRepository
                        .findByWalletIdOrderByCreateTimeDesc(
                                walletId)
                        .stream()
                        .filter(transaction ->
                                transaction.getTransactionType()
                                        == TransactionType.DAILY_TASK_REWARD)
                        .filter(transaction ->
                                taskId.equals(
                                        transaction.getReferenceId()))
                        .toList();

        assertEquals(
                1,
                rewards.size(),
                "這項任務應只有一筆獎勵交易");

        CoinTransaction savedTransaction = rewards.get(0);

        assertEquals(
                10L,
                savedTransaction.getAmount().longValue(),
                "交易金額應為 10 點");

        assertEquals(
                balanceBefore + 10L,
                savedTransaction.getBalanceAfter().longValue(),
                "交易完成後餘額應與錢包一致");

        assertEquals(
                savedWallet.getBalance(),
                savedTransaction.getBalanceAfter());

        assertEquals(
                taskId,
                savedTransaction.getReferenceId());

        assertNotNull(savedTransaction.getCreateTime());

        System.out.printf(
                "%s 測試通過：餘額 %d → %d，交易 1 筆，任務已領取%n",
                taskType,
                balanceBefore,
                savedWallet.getBalance()
        );
    }
}