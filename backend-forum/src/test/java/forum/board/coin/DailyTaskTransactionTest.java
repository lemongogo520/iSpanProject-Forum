package forum.board.coin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("coin-it")
@Execution(ExecutionMode.SAME_THREAD)
class DailyTaskTransactionTest {

    @Autowired
    private DailyTaskService service;

    @MockitoSpyBean
    private MemberDailyTaskRepository taskRepository;

    @Autowired
    private CoinWalletRepository walletRepository;

    @Autowired
    private CoinTransactionRepository transactionRepository;

    @Autowired
    private forum.board.users.UsersRepository usersRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("${coin.test.account}")
    private String testAccount;

    private TransactionTemplate tx;

    private UUID userId;
    private UUID walletId;
    private UUID taskId;
    private long balanceBefore;

    private boolean fixtureCreated;
    private boolean workersStopped = true;

    private static class SimulatedWriteFailure
            extends RuntimeException {

        SimulatedWriteFailure() {
            super("測試用：資料已 flush 後故意失敗");
        }
    }

    private record ClaimAttempt(
            boolean success,
            Throwable error) {
    }

    @BeforeEach
    void prepare() {

        tx = new TransactionTemplate(transactionManager);
        tx.setTimeout(20);

        // 防止誤用開發或共用資料庫。
        assertEquals(
                "iSpan_Project_Test",
                jdbc.queryForObject(
                        "SELECT DB_NAME()", String.class),
                "此測試只能在 iSpan_Project_Test 執行");

        taskId = tx.execute(status -> {

            var user = usersRepository
                    .findByAccount(testAccount)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "找不到測試會員"));

            userId = user.getId();

            CoinWallet wallet = walletRepository
                    .findByUserId(userId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "測試會員需要先有錢包"));

            walletId = wallet.getId();
            balanceBefore = wallet.getBalance();

            LocalDate today = LocalDate.now(
                    ZoneId.of("Asia/Taipei"));

            assertTrue(
                    taskRepository
                            .findByUserIdAndTaskDateAndTaskType(
                                    userId,
                                    today,
                                    DailyTaskType.GIVE_LIKE)
                            .isEmpty(),
                    "測試帳號今天已有按讚任務，請換專用測試帳號");

            // 建立已達標、尚未領取的測試任務。
            MemberDailyTask task = new MemberDailyTask();

            task.setUserId(userId);
            task.setTaskDate(today);
            task.setTaskType(DailyTaskType.GIVE_LIKE);
            task.setTargetCount(3);
            task.setProgressCount(3);
            task.setRewardAmount(10L);
            task.setClaimed(false);

            return taskRepository.saveAndFlush(task).getId();
        });

        fixtureCreated = true;
    }

    @Test
    @DisplayName("三份資料已寫入後失敗，整個領獎交易回滾")
    void failureRollsBackAllChanges() {

        /*
         * claimReward() 最後會 save 已領取的任務。
         * 在這裡先 flush，確認 SQL 真正執行，
         * 然後故意丟出 RuntimeException。
         */
        doAnswer(invocation -> {

            MemberDailyTask task = invocation.getArgument(0);

            if (taskId.equals(task.getId())
                    && Boolean.TRUE.equals(task.getClaimed())) {

                entityManager.flush();

                // 同一交易內，確認三份變更都已送到 SQL Server。
                assertEquals(
                        balanceBefore + 10L,
                        jdbc.queryForObject(
                                "SELECT balance FROM dbo.coin_wallet "
                                + "WHERE id = ?",
                                Long.class,
                                walletId.toString()).longValue());

                assertEquals(
                        1,
                        jdbc.queryForObject(
                                "SELECT COUNT(*) "
                                + "FROM dbo.coin_transaction "
                                + "WHERE reference_id = ? "
                                + "AND transaction_type = 'DAILY_TASK_REWARD'",
                                Integer.class,
                                taskId.toString()).intValue());

                assertEquals(
                        1,
                        jdbc.queryForObject(
                                "SELECT CAST(claimed AS int) "
                                + "FROM dbo.member_daily_task WHERE id = ?",
                                Integer.class,
                                taskId.toString()).intValue());

                throw new SimulatedWriteFailure();
            }

            return invocation.callRealMethod();

        }).when(taskRepository).save(any(MemberDailyTask.class));

        // 沒有外層測試交易，由 Service 的 @Transactional 負責。
        assertThrows(
                SimulatedWriteFailure.class,
                () -> service.claimReward(userId, taskId));

        // 新交易重新查詢，確認沒有留下部分更新。
        assertDatabaseState(balanceBefore, false, 0);
    }

    @Test
    @DisplayName("兩個執行緒同時領取，只成功一次並入帳 10 點")
    void concurrentClaimsCreditOnlyOnce() throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        workersStopped = false;

        try {
            Callable<ClaimAttempt> worker = () -> {

                ready.countDown();

                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new TimeoutException("等待開始領獎逾時");
                }

                try {
                    // 每個執行緒各自進入 Service 交易。
                    service.claimReward(userId, taskId);
                    return new ClaimAttempt(true, null);

                } catch (Exception error) {
                    return new ClaimAttempt(false, error);
                }
            };

            Future<ClaimAttempt> first = pool.submit(worker);
            Future<ClaimAttempt> second = pool.submit(worker);

            assertTrue(
                    ready.await(10, TimeUnit.SECONDS),
                    "兩個執行緒未準備完成");

            start.countDown();

            ClaimAttempt a = first.get(30, TimeUnit.SECONDS);
            ClaimAttempt b = second.get(30, TimeUnit.SECONDS);

            long successes = List.of(a, b)
                    .stream()
                    .filter(ClaimAttempt::success)
                    .count();

            assertEquals(
                    1L,
                    successes,
                    "應只有一筆成功；結果：" + a + " / " + b);

            ClaimAttempt rejected = a.success() ? b : a;

            assertInstanceOf(
                    IllegalArgumentException.class,
                    rejected.error());

            assertEquals(
                    "此任務今天已經領取過獎勵",
                    rejected.error().getMessage());

            // 確認成功交易已提交，且沒有重複入帳。
            assertDatabaseState(balanceBefore + 10L, true, 1);

        } finally {
            start.countDown();
            pool.shutdownNow();

            workersStopped = pool.awaitTermination(
                    30, TimeUnit.SECONDS);

            assertTrue(
                    workersStopped,
                    "執行緒未停止，暫不清理資料，請先停止測試程序");
        }
    }

    private void assertDatabaseState(
            long expectedBalance,
            boolean expectedClaimed,
            int expectedTransactionCount) {

        tx.executeWithoutResult(status -> {

            entityManager.clear();

            CoinWallet wallet = walletRepository
                    .findById(walletId)
                    .orElseThrow();

            MemberDailyTask task = taskRepository
                    .findById(taskId)
                    .orElseThrow();

            assertEquals(
                    expectedBalance,
                    wallet.getBalance().longValue());

            assertEquals(
                    expectedClaimed,
                    Boolean.TRUE.equals(task.getClaimed()));

            if (expectedClaimed) {
                assertNotNull(task.getClaimedTime());
            } else {
                assertNull(task.getClaimedTime());
            }

            List<CoinTransaction> rewards = findTestRewards();

            assertEquals(
                    expectedTransactionCount,
                    rewards.size());

            if (expectedTransactionCount == 1) {
                CoinTransaction reward = rewards.get(0);

                assertEquals(10L, reward.getAmount().longValue());

                assertEquals(
                        expectedBalance,
                        reward.getBalanceAfter().longValue());
            }
        });
    }

    private List<CoinTransaction> findTestRewards() {

        return transactionRepository
                .findByWalletIdOrderByCreateTimeDesc(walletId)
                .stream()
                .filter(transaction ->
                        transaction.getTransactionType()
                                == TransactionType.DAILY_TASK_REWARD)
                .filter(transaction ->
                        taskId.equals(transaction.getReferenceId()))
                .toList();
    }

    @AfterEach
    void cleanUp() {

        // 移除故意失敗的設定，讓清理使用真實 Repository。
        reset(taskRepository);

        if (!fixtureCreated || !workersStopped) {
            return;
        }

        tx.executeWithoutResult(status -> {

            // 僅清除此測試任務的獎勵交易。
            transactionRepository.deleteAll(findTestRewards());
            transactionRepository.flush();

            taskRepository.deleteById(taskId);
            taskRepository.flush();

            // 專用測試會員：恢復測試前餘額。
            CoinWallet wallet = walletRepository
                    .findById(walletId)
                    .orElseThrow();

            wallet.setBalance(balanceBefore);
            walletRepository.saveAndFlush(wallet);
        });
    }
}