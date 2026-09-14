package forum.board.chat.message;

import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.chat.message.MessagesEntity.ChatType;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
    public final UsersRepository uRepo;
    public final MessagesRepository mRepo;
    public void sendMessage(UUID sender, String context){
        MessagesEntity msg = new MessagesEntity();
        msg.setType(ChatType.TEXT);
        msg.setSenderId(sender);
        msg.setContent(context);
        mRepo.save(msg);
    }

    public void getMessage(){}
    public void editMessage(){}
    public void deleteMessage(){}



    

}
