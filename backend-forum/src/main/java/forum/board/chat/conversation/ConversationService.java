package forum.board.chat.conversation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.chat.conversation.ConversationEntity.ConversationType;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository cRepo;
    private final ConversationMemberRepository mRepo;
    private final UsersRepository uRepo;

    public void findOrCreateDirectConversation(UUID user1Id, UUID user2Id){
        // 先找兩人有沒有聊過天 沒有就建立 conversation+conversation member
        ConversationEntity c = cRepo.findDirectConversation(user1Id, user2Id)
            .orElseGet(() -> createDirectConversation(user1Id, user2Id));
    }

    public ConversationEntity createDirectConversation(UUID user1Id, UUID user2Id){
        Users user1 = uRepo.findById(user1Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        Users user2 = uRepo.findById(user2Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    
        ConversationEntity c = new ConversationEntity();
        c.setType(ConversationType.DIRECT);
        cRepo.save(c);
        
        ConversationMemberEntity m1 = new ConversationMemberEntity();
        m1.setConversation(c);
        m1.setUser(user1);
        mRepo.save(m1);
        
        ConversationMemberEntity m2 = new ConversationMemberEntity();
        m2.setConversation(c);
        m2.setUser(user2);
        mRepo.save(m2);

        return c;
    }


}
