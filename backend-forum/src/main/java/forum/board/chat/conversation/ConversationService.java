package forum.board.chat.conversation;

import org.springframework.stereotype.Service;

import forum.board.users.Users;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository cRepo;

    public void findOrCreateDirectConversation(Users user1, Users user2){
        //TODO
    }


}
