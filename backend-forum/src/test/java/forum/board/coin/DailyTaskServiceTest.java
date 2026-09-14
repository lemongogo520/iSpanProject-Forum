package forum.board.coin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import forum.board.users.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@ExtendWith(MockitoExtension.class)
class DailyTaskServiceTest {

    @Mock
    private MemberDailyTaskRepository taskRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private DailyTaskLikeRecordRepository likeRecordRepository;

    @Mock
    private CoinService coinService;

    @Mock
    private CoinWalletRepository walletRepository;

    @Mock
    private CoinTransactionRepository transactionRepository;
    
    @Mock
    private DailyTaskContentRecordRepository contentRecordRepository;

    private DailyTaskService service;

    private UUID userId;
    private UUID taskId;

    private MemberDailyTask task;
    private CoinWallet wallet;

    @BeforeEach
    void setUp() {

    	service = new DailyTaskService(
    	        taskRepository,
    	        new DailyTaskConfig(),
    	        entityManager,
    	        likeRecordRepository,
    	        coinService,
    	        walletRepository,
    	        transactionRepository,
    	        contentRecordRepository
    	);

        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        // 模擬會員存在，不查詢真實資料庫。
        Users user = mock(Users.class);

        when(entityManager.find(
                Users.class,
                userId,
                LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(user);

        // 建立當天任務的記憶體物件。
        task = new MemberDailyTask();

        task.setId(taskId);
        task.setUserId(userId);

        task.setTaskDate(
                LocalDate.now(ZoneId.of("Asia/Taipei")));

        task.setTaskType(DailyTaskType.GIVE_LIKE);
        task.setTargetCount(3);
        task.setProgressCount(3);
        task.setRewardAmount(10L);
        task.setClaimed(false);

        // 模擬會員原本有 100 點。
        wallet = new CoinWallet();

        wallet.setId(UUID.randomUUID());
        wallet.setBalance(100L);
    }

    private void mockOwnedTask() {

        when(taskRepository.findByIdAndUserId(
                taskId,
                userId))
                .thenReturn(Optional.of(task));
    }

    private void mockWallet() {

        when(coinService.getOrCreateWallet(userId))
                .thenReturn(wallet);
    }

    @Test
    @DisplayName("未達到 3 次，不能領取獎勵")
    void incompleteTaskCannotBeClaimed() {

        task.setProgressCount(2);

        mockOwnedTask();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.claimReward(userId, taskId)
        );

        assertEquals(
                "任務尚未完成，無法領取獎勵",
                error.getMessage());

        assertFalse(task.getClaimed());
        assertEquals(100L, wallet.getBalance().longValue());

        // 未達標時，不應進行加點或寫入交易。
        verifyNoInteractions(
                coinService,
                walletRepository,
                transactionRepository);
    }

    @Test
    @DisplayName("達到 3 次，領取後增加 10 點並建立交易")
    void completedTaskReceivesTenPoints() {

        mockOwnedTask();
        mockWallet();

        DailyTaskClaimResponse result =
                service.claimReward(userId, taskId);

        assertEquals(taskId, result.taskId());
        assertEquals(10L, result.rewardAmount().longValue());
        assertEquals(110L, result.balanceAfter().longValue());
        assertEquals("CLAIMED", result.status());

        assertEquals(110L, wallet.getBalance().longValue());

        assertTrue(task.getClaimed());
        assertNotNull(task.getClaimedTime());
        assertEquals(task.getClaimedTime(), result.claimedTime());

        verify(walletRepository).save(wallet);
        verify(taskRepository).save(task);

        // 檢查交給 Repository 儲存的交易內容。
        ArgumentCaptor<CoinTransaction> captor =
                ArgumentCaptor.forClass(CoinTransaction.class);

        verify(transactionRepository).save(captor.capture());

        CoinTransaction transaction = captor.getValue();

        assertSame(wallet, transaction.getWallet());

        assertEquals(
                TransactionType.DAILY_TASK_REWARD,
                transaction.getTransactionType());

        assertEquals(
                10L,
                transaction.getAmount().longValue());

        assertEquals(
                110L,
                transaction.getBalanceAfter().longValue());

        assertEquals(
                taskId,
                transaction.getReferenceId());
    }

    @Test
    @DisplayName("同一任務連續領取兩次，只加一次點數")
    void repeatedClaimDoesNotAddPointsAgain() {

        mockOwnedTask();
        mockWallet();

        // 第一次正常領取。
        service.claimReward(userId, taskId);

        // 第二次應被拒絕。
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.claimReward(userId, taskId)
        );

        assertEquals(
                "此任務今天已經領取過獎勵",
                error.getMessage());

        assertEquals(110L, wallet.getBalance().longValue());
        assertTrue(task.getClaimed());

        verify(walletRepository, times(1)).save(wallet);

        verify(transactionRepository, times(1))
                .save(any(CoinTransaction.class));
    }

    @Test
    @DisplayName("不能領取不屬於自己的任務")
    void cannotClaimAnotherMembersTask() {

        // 模擬用目前會員 ID 查不到這項任務。
        when(taskRepository.findByIdAndUserId(
                taskId,
                userId))
                .thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.claimReward(userId, taskId)
        );

        assertEquals(
                "找不到你的任務",
                error.getMessage());

        verifyNoInteractions(
                coinService,
                walletRepository,
                transactionRepository);
    }
}