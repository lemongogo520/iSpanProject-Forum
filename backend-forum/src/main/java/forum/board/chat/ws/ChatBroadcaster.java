package forum.board.chat.ws;

import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import forum.board.chat.conversation.ConversationMemberRepository;
import forum.board.chat.conversation.ConversationRepository;
import forum.board.chat.conversation.ConversationEntity.ConversationType;
import forum.board.chat.dto.MessageResponseDTO;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatBroadcaster {
    private final SimpMessagingTemplate messaging;
    private final ConversationMemberRepository cmRepo;
    private final ConversationRepository cRepo;
    private final SimpUserRegistry userRegistry;

    public void messageCreated(MessageResponseDTO msg) {
        System.out.println("線上使用者=" + userRegistry.getUsers().stream()
                .map(SimpUser::getName).toList());
        userRegistry.getUsers()
                .forEach(u -> u.getSessions().forEach(s -> System.out.println("  " + u.getName() + " 訂閱=" +
                        s.getSubscriptions().stream().map(sub -> sub.getDestination()).toList())));
        Long cid = msg.conversationId();
        var type = cRepo.findTypeById(cid);
        var uids = cmRepo.findUserIdsByConversationId(cid);
        System.out.println("推播 conversationId=" + cid + " type=" + type + " 對象=" + uids);
        if (cRepo.findTypeById(cid) == ConversationType.BOARD) {
            messaging.convertAndSend("/topic/conversation." + cid, msg);
            return;
        }

        for (UUID uid : cmRepo.findUserIdsByConversationId(cid)) {
            messaging.convertAndSendToUser(uid.toString(), "/queue/messages", msg);
        }
    }
}
