package forum.board.coin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import forum.board.users.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@ExtendWith(MockitoExtension.class)
class DailyTaskActivityServiceTest {

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

    // 每個測試都有自己獨立的記憶體資料。
    private Map<TaskKey, MemberDailyTask> tasks;
    private Set<ContentKey> recordedContents;

    private record TaskKey(
            UUID userId,
            LocalDate date,
            DailyTaskType type) {
    }

    private record ContentKey(
            DailyTaskType type,
            String sourceId) {
    }

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

        tasks = new HashMap<>();
        recordedContents = new HashSet<>();

        Users user = mock(Users.class);

        when(entityManager.find(
                Users.class,
                userId,
                LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(user);

        // 模擬查詢每日任務。
        when(taskRepository
                .findByUserIdAndTaskDateAndTaskType(
                        any(UUID.class),
                        any(LocalDate.class),
                        any(DailyTaskType.class)))
                .thenAnswer(invocation -> {

                    TaskKey key = new TaskKey(
                            invocation.getArgument(0),
                            invocation.getArgument(1),
                            invocation.getArgument(2)
                    );

                    return Optional.ofNullable(tasks.get(key));
                });

        // 模擬儲存每日任務。
        when(taskRepository.save(any(MemberDailyTask.class)))
                .thenAnswer(invocation -> {

                    MemberDailyTask task =
                            invocation.getArgument(0);

                    if (task.getId() == null) {
                        task.setId(UUID.randomUUID());
                    }

                    tasks.put(
                            new TaskKey(
                                    task.getUserId(),
                                    task.getTaskDate(),
                                    task.getTaskType()),
                            task
                    );

                    return task;
                });
    }

    /**
     * 發文與留言測試才需要的來源紀錄模擬。
     */
    private void prepareContentRepository() {

        when(contentRecordRepository
                .existsByTaskTypeAndSourceId(
                        any(DailyTaskType.class),
                        anyString()))
                .thenAnswer(invocation -> {

                    ContentKey key = new ContentKey(
                            invocation.getArgument(0),
                            invocation.getArgument(1)
                    );

                    return recordedContents.contains(key);
                });

        when(contentRecordRepository.save(
                any(DailyTaskContentRecord.class)))
                .thenAnswer(invocation -> {

                    DailyTaskContentRecord record =
                            invocation.getArgument(0);

                    recordedContents.add(
                            new ContentKey(
                                    record.getTaskType(),
                                    record.getSourceId())
                    );

                    assertEquals(userId, record.getUserId());

                    return record;
                });
    }

    private MemberDailyTask findTask(DailyTaskType type) {

        List<MemberDailyTask> matches = tasks.values()
                .stream()
                .filter(task -> userId.equals(task.getUserId()))
                .filter(task -> task.getTaskType() == type)
                .toList();

        assertEquals(
                1,
                matches.size(),
                "應只有一筆對應任務");

        return matches.get(0);
    }

    /**
     * 檢查目前暫定的 1 次、10 點設定。
     * 達標後仍應保持尚未領取。
     */
    private void assertCompletedButUnclaimed(
            DailyTaskType type) {

        MemberDailyTask task = findTask(type);

        assertEquals(1, task.getTargetCount().intValue());
        assertEquals(1, task.getProgressCount().intValue());
        assertEquals(10L, task.getRewardAmount().longValue());

        assertFalse(Boolean.TRUE.equals(task.getClaimed()));
        assertNull(task.getClaimedTime());
    }

    private void verifyNoPointsIssued() {

        verifyNoInteractions(
                coinService,
                walletRepository,
                transactionRepository,
                likeRecordRepository);
    }

    @Test
    @DisplayName("登入完成任務，同一天重複登入不累加")
    void loginCompletesOnlyOnce() {

        service.recordLogin(userId);

        assertCompletedButUnclaimed(DailyTaskType.LOGIN);

        service.recordLogin(userId);
        service.recordLogin(userId);

        assertCompletedButUnclaimed(DailyTaskType.LOGIN);

        assertEquals(1, tasks.size());

        verifyNoInteractions(contentRecordRepository);
        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("簽到完成任務，重複簽到仍為可領取且不加點")
    void checkInCompletesOnlyOnce() {

        DailyTaskResponse first =
                service.checkIn(userId);

        assertEquals(
                DailyTaskType.CHECK_IN,
                first.taskType());

        assertEquals("CLAIMABLE", first.status());
        assertEquals(1, first.progressCount().intValue());
        assertEquals(10L, first.rewardAmount().longValue());

        DailyTaskResponse second =
                service.checkIn(userId);

        // 重複簽到沿用同一筆任務。
        assertEquals(first.id(), second.id());
        assertEquals("CLAIMABLE", second.status());
        assertEquals(1, second.progressCount().intValue());

        assertCompletedButUnclaimed(DailyTaskType.CHECK_IN);

        assertEquals(1, tasks.size());

        verifyNoInteractions(contentRecordRepository);
        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("發文計入一次，重送不累計，達標後仍保存新來源")
    void postDeduplicatesAndCapsProgress() {

        prepareContentRepository();

        assertTrue(
                service.recordPost(userId, "post-001"));

        assertCompletedButUnclaimed(DailyTaskType.POST);

        // 同一篇文章再次通知，不重複計算。
        assertFalse(
                service.recordPost(userId, "post-001"));

        assertEquals(1, recordedContents.size());

        // 第二篇是真正的新文章，要保存來源。
        assertTrue(
                service.recordPost(userId, "post-002"));

        // 但今日任務最多顯示 1／1。
        assertCompletedButUnclaimed(DailyTaskType.POST);

        assertEquals(2, recordedContents.size());

        // 即使第二篇沒有增加進度，重送仍應被識別。
        assertFalse(
                service.recordPost(userId, "post-002"));

        verify(contentRecordRepository, times(2))
                .save(any(DailyTaskContentRecord.class));

        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("留言與回覆來源分開記錄，同一內容重送不累計")
    void commentAndReplyDeduplicateAndCapProgress() {

        prepareContentRepository();

        assertTrue(
                service.recordComment(
                        userId,
                        "COMMENT:001"));

        assertFalse(
                service.recordComment(
                        userId,
                        "COMMENT:001"));

        assertCompletedButUnclaimed(DailyTaskType.COMMENT);

        // 回覆 001 與留言 001 是不同內容。
        assertTrue(
                service.recordComment(
                        userId,
                        "REPLY:001"));

        assertFalse(
                service.recordComment(
                        userId,
                        "REPLY:001"));

        assertEquals(2, recordedContents.size());

        assertTrue(recordedContents.contains(
                new ContentKey(
                        DailyTaskType.COMMENT,
                        "COMMENT:001")));

        assertTrue(recordedContents.contains(
                new ContentKey(
                        DailyTaskType.COMMENT,
                        "REPLY:001")));

        // 兩種來源共用每日留言任務，進度仍最多 1／1。
        assertCompletedButUnclaimed(DailyTaskType.COMMENT);

        verify(contentRecordRepository, times(2))
                .save(any(DailyTaskContentRecord.class));

        verifyNoPointsIssued();
    }
}