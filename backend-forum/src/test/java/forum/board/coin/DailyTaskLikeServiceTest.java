package forum.board.coin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
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
class DailyTaskLikeServiceTest {

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

    private UUID actorUserId;
    private UUID authorUserId;

    // 模擬資料庫，僅存放於本次測試的記憶體。
    private Map<TaskKey, MemberDailyTask> tasks;
    private Set<LikeKey> recordedLikes;

    private record TaskKey(
            UUID userId,
            LocalDate date,
            DailyTaskType type) {
    }

    private record LikeKey(
            UUID actorUserId,
            LikeTargetType targetType,
            String targetId) {
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

        actorUserId = UUID.randomUUID();
        authorUserId = UUID.randomUUID();

        tasks = new HashMap<>();
        recordedLikes = new HashSet<>();
    }

    /**
     * 設定模擬會員、任務與按讚紀錄。
     *
     * 自己按自己會直接結束，不需要這些設定，
     * 所以由需要的測試個別呼叫。
     */
    private void prepareRepositories() {

        Users actor = mock(Users.class);
        Users author = mock(Users.class);

        when(entityManager.find(
                Users.class,
                actorUserId,
                LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(actor);

        when(entityManager.find(
                Users.class,
                authorUserId,
                LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(author);

        // 模擬查詢：此按讚是否已計入任務。
        when(likeRecordRepository
                .existsByActorUserIdAndTargetTypeAndTargetId(
                        any(UUID.class),
                        any(LikeTargetType.class),
                        anyString()))
                .thenAnswer(invocation -> {

                    LikeKey key = new LikeKey(
                            invocation.getArgument(0),
                            invocation.getArgument(1),
                            invocation.getArgument(2)
                    );

                    return recordedLikes.contains(key);
                });

        // 模擬儲存按讚來源紀錄。
        when(likeRecordRepository.save(
                any(DailyTaskLikeRecord.class)))
                .thenAnswer(invocation -> {

                    DailyTaskLikeRecord record =
                            invocation.getArgument(0);

                    recordedLikes.add(new LikeKey(
                            record.getActorUserId(),
                            record.getTargetType(),
                            record.getTargetId()
                    ));

                    return record;
                });

        // 模擬查詢指定會員、日期與類型的任務。
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

        // 模擬儲存任務。
        when(taskRepository.save(any(MemberDailyTask.class)))
                .thenAnswer(invocation -> {

                    MemberDailyTask task =
                            invocation.getArgument(0);

                    if (task.getId() == null) {
                        task.setId(UUID.randomUUID());
                    }

                    TaskKey key = new TaskKey(
                            task.getUserId(),
                            task.getTaskDate(),
                            task.getTaskType()
                    );

                    tasks.put(key, task);

                    return task;
                });
    }

    private MemberDailyTask findTask(
            UUID userId,
            DailyTaskType type) {

        MemberDailyTask task = tasks.get(new TaskKey(
                userId,
                LocalDate.now(ZoneId.of("Asia/Taipei")),
                type
        ));

        assertNotNull(task, "應建立對應的每日任務");

        return task;
    }

    /**
     * 累積任務進度時，不應直接發放點數。
     */
    private void verifyNoPointsIssued() {

        verifyNoInteractions(
                coinService,
                walletRepository,
                transactionRepository);
    }

    @Test
    @DisplayName("有效按讚後，按讚者與作者的任務各增加一次")
    void validLikeIncreasesBothTasks() {

        prepareRepositories();

        boolean processed = service.recordLike(
                actorUserId,
                authorUserId,
                LikeTargetType.POST,
                "post-001"
        );

        assertTrue(processed);

        MemberDailyTask actorTask = findTask(
                actorUserId,
                DailyTaskType.GIVE_LIKE);

        MemberDailyTask authorTask = findTask(
                authorUserId,
                DailyTaskType.RECEIVE_LIKE);

        assertEquals(1, actorTask.getProgressCount().intValue());
        assertEquals(1, authorTask.getProgressCount().intValue());

        assertEquals(3, actorTask.getTargetCount().intValue());
        assertEquals(3, authorTask.getTargetCount().intValue());

        assertEquals(10L, actorTask.getRewardAmount().longValue());
        assertEquals(10L, authorTask.getRewardAmount().longValue());

        assertFalse(actorTask.getClaimed());
        assertFalse(authorTask.getClaimed());

        assertEquals(2, tasks.size());
        assertEquals(1, recordedLikes.size());

        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("同一會員對同一內容重複按讚，不重複累計")
    void repeatedLikeDoesNotIncreaseProgress() {

        prepareRepositories();

        boolean first = service.recordLike(
                actorUserId,
                authorUserId,
                LikeTargetType.POST,
                "post-001"
        );

        boolean second = service.recordLike(
                actorUserId,
                authorUserId,
                LikeTargetType.POST,
                "post-001"
        );

        assertTrue(first);
        assertFalse(second);

        assertEquals(
                1,
                findTask(
                        actorUserId,
                        DailyTaskType.GIVE_LIKE)
                        .getProgressCount().intValue());

        assertEquals(
                1,
                findTask(
                        authorUserId,
                        DailyTaskType.RECEIVE_LIKE)
                        .getProgressCount().intValue());

        assertEquals(1, recordedLikes.size());

        verify(likeRecordRepository, times(1))
                .save(any(DailyTaskLikeRecord.class));

        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("自己按讚自己的內容，不計入任務")
    void selfLikeDoesNotCount() {

        boolean processed = service.recordLike(
                actorUserId,
                actorUserId,
                LikeTargetType.POST,
                "my-post"
        );

        assertFalse(processed);

        verifyNoInteractions(
                entityManager,
                taskRepository,
                likeRecordRepository);

        verifyNoPointsIssued();
    }

    @Test
    @DisplayName("四次不同內容的按讚，雙方進度最多為 3／3")
    void progressStopsAtThree() {

        prepareRepositories();

        for (int i = 1; i <= 4; i++) {

            boolean processed = service.recordLike(
                    actorUserId,
                    authorUserId,
                    LikeTargetType.POST,
                    "post-" + i
            );

            // 第四次仍記錄來源，但不再增加已達標的進度。
            assertTrue(processed);
        }

        MemberDailyTask actorTask = findTask(
                actorUserId,
                DailyTaskType.GIVE_LIKE);

        MemberDailyTask authorTask = findTask(
                authorUserId,
                DailyTaskType.RECEIVE_LIKE);

        assertEquals(3, actorTask.getProgressCount().intValue());
        assertEquals(3, authorTask.getProgressCount().intValue());

        assertFalse(actorTask.getClaimed());
        assertFalse(authorTask.getClaimed());

        // 四次不同的按讚都要留下紀錄。
        assertEquals(4, recordedLikes.size());

        // 重送第四次按讚，仍應被識別為已處理。
        assertFalse(service.recordLike(
                actorUserId,
                authorUserId,
                LikeTargetType.POST,
                "post-4"
        ));

        verify(likeRecordRepository, times(4))
                .save(any(DailyTaskLikeRecord.class));

        verifyNoPointsIssued();
    }
}