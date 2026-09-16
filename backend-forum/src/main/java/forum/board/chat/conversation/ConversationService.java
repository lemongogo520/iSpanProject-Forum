package forum.board.chat.conversation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.chat.conversation.ConversationEntity.ConversationType;
import forum.board.chat.dto.ConversationMemberResponseDTO;
import forum.board.chat.dto.ConversationResponseDTO;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.exception.BusinessException;
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

    public ConversationResponseDTO findOrCreateDirectConversation(UUID user1Id, UUID user2Id){
        if(user1Id.equals(user2Id)){
            throw new BusinessException("不可以跟自己聊天");
        }
        // 先找兩人有沒有聊過天 沒有就建立 conversation+conversation member
        ConversationEntity c = cRepo.findDirectConversation(user1Id, user2Id)
            .orElseGet(() -> createDirectConversation(user1Id, user2Id));
        return new ConversationResponseDTO(c.getId(), c.getType(), c.getLastMessage(), c.getCreateTime(), c.getUpdateTime());
    }

    private ConversationEntity createDirectConversation(UUID user1Id, UUID user2Id){
        Users user1 = uRepo.findById(user1Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        Users user2 = uRepo.findById(user2Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    
        ConversationEntity c = new ConversationEntity();
        c.setType(ConversationType.DIRECT);
        
        ConversationMemberEntity m1 = new ConversationMemberEntity();
        m1.setConversation(c);
        m1.setUser(user1);
        ConversationMemberEntity m2 = new ConversationMemberEntity();
        m2.setConversation(c);
        m2.setUser(user2);
        
        c.setMembers(List.of(m1, m2));
        cRepo.save(c);
        mRepo.save(m1);
        mRepo.save(m2);
        
        return c;
    }


}
