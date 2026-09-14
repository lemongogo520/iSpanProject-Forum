package forum.board.chat.conversation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.users.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository cRepo;
    private final ConversationMemberRepository mRepo;
    private final FriendshipUserRepository uRepo;

    public void findOrCreateDirectConversation(UUID user1, UUID user2){
        //TODO:
        // 1. 先找兩人有沒有聊過天 conversationMember 
        mRepo.findByUser(uRepo.findById(user1).get());
        mRepo.findByUser(uRepo.findById(user2).get());
        cRepo.findByMembers(null);
        // 2. 沒有 -> 建立新的Conversation

        // 2.1 -> 新增ConversationMember


    }


}
