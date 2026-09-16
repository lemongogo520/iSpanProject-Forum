package forum.board.coin;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTaskLikeRecordRepository
        extends JpaRepository<DailyTaskLikeRecord, UUID> {

    boolean existsByActorUserIdAndTargetTypeAndTargetId(
            UUID actorUserId,
            LikeTargetType targetType,
            String targetId
    );
}