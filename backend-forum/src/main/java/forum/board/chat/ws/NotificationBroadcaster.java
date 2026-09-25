package forum.board.chat.ws;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationBroadcaster {
    private final SimpMessagingTemplate messaging;

    /** 通知某位使用者：好友資料有變動 */
    public void notify(UUID userId, String type, String actorName) {
        if (userId == null)
            return;
        messaging.convertAndSendToUser(userId.toString(), "/queue/notifications",
                Map.of("type", type, "name", actorName == null ? "" : actorName));
    }

    public void boardPosted(List<UUID> subscriberIds, UUID authorId, String boardName, Integer postId) {
        for (UUID uid : subscriberIds) {
            if (uid.equals(authorId))
                continue;
            messaging.convertAndSendToUser(uid.toString(), "/queue/notifications",
                    Map.of("type", "BOARD_POST",
                            "name", boardName,
                            "postId", postId));
        }
    }
}
