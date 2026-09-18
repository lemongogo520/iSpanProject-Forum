package forum.board.chat.message;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import forum.board.chat.conversation.ConversationEntity;
import forum.board.chat.conversation.ConversationMemberRepository;
import forum.board.chat.conversation.ConversationRepository;
import forum.board.chat.dto.MessageRequestDTO;
import forum.board.chat.dto.MessageResponseDTO;
import forum.board.chat.message.MessagesEntity.ChatType;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.exception.BusinessException;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
    private final UsersRepository uRepo;
    private final MessagesRepository mRepo;
    private final ConversationRepository cRepo; 
    private final ConversationMemberRepository cmRepo; 
    

    private Users findUser(UUID userId){
		return uRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
	}

    private ConversationEntity findConversation(Long conversationId){
        return cRepo.findById(conversationId).orElseThrow(() ->
            new ResourceNotFoundException("找不到該對話")
        );
    }

    private void checkMember(Long conversationId, UUID userId){
        cmRepo.findByConversationIdAndUserId(conversationId, userId).orElseThrow(()->
            new ResourceNotFoundException("你不是該對話的成員")
        );
    }

    private MessageResponseDTO returnResponseDTO(UUID currentUserId, UUID senderId, MessagesEntity msg){
        boolean mine = msg.getSender().getId().equals(currentUserId);
        Users sender = findUser(senderId);
        FriendResponseDTO senderDTO = new FriendResponseDTO(
            sender.getId(), 
            sender.getAccount(), 
            sender.getUsersDetail().getName(),
            sender.getUsersDetail().getPhoto());
        return  new MessageResponseDTO(
            msg.getId(), 
            msg.getConversation().getId(), 
            msg.getType(), 
            senderDTO, 
            msg.getContent(), 
            msg.getCreateTime(),
            mine
        );
    }

    /* 取得對話內最後50筆訊息 */
    public List<MessageResponseDTO> getMessages(Long conversationId, UUID currentUserId) {
        // 確認對話存在
        findConversation(conversationId);
        // 確認使用者是對話成員
        checkMember(conversationId, currentUserId);

        // 取得訊息
        return mRepo.findLast50ByConversationIdOrderByCreateTimeAsc(conversationId)
                .stream().map((msg) -> {
                return returnResponseDTO(currentUserId, msg.getSender().getId(), msg);
                }).toList();
                    
    }

    /* 發送訊息 */
    public MessageResponseDTO sendMessage(UUID senderId, MessageRequestDTO request){
        // 找Conversations
        ConversationEntity conversation =  findConversation(request.conversationId());

        // 確認使用者是對話成員
        checkMember(request.conversationId(), senderId);

        Users sender = findUser(senderId);

        if(request.content() == null || request.content().trim().isEmpty()){
            throw new BusinessException("訊息不能為空");
        }

        // 建立Message
        MessagesEntity msg = new MessagesEntity();
        msg.setType(ChatType.TEXT);
        msg.setConversation(conversation);
        msg.setSender(sender);
        msg.setContent(request.content());
        mRepo.save(msg);

        conversation.setLastMessage(request.content());
        cRepo.save(conversation);

        return returnResponseDTO(senderId, senderId, msg);
    }

    // TODO:
    public void editMessage(){}
    public void deleteMessage(){}




    

}
