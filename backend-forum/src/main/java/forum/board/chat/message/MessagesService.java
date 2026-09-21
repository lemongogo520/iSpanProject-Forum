package forum.board.chat.message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import forum.board.chat.conversation.ConversationEntity;
import forum.board.chat.conversation.ConversationMemberRepository;
import forum.board.chat.conversation.ConversationRepository;
import forum.board.chat.dto.MessagePageDTO;
import forum.board.chat.dto.MessageRequestDTO;
import forum.board.chat.dto.MessageResponseDTO;
import forum.board.chat.message.MessagesEntity.ChatType;
import forum.board.friends.dto.FriendResponseDTO;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessagesService {
    private static final int PAGE_SIZE = 30;
    private final UsersRepository uRepo;
    private final MessagesRepository mRepo;
    private final ConversationRepository cRepo;
    private final ConversationMemberRepository cmRepo;

    private Users findUser(UUID userId) {
        return uRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    }

    private ConversationEntity findConversation(Long conversationId) {
        return cRepo.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該對話"));
    }

    private void checkMember(Long conversationId, UUID userId) {
        cmRepo.findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該對話"));
    }

    private MessageResponseDTO toResponseDTO(UUID currentUserId, MessagesEntity msg) {
        Users sender = msg.getSender();
        boolean mine = sender.getId().equals(currentUserId);
        FriendResponseDTO senderDTO = new FriendResponseDTO(
                sender.getId(),
                sender.getAccount(),
                sender.getUsersDetail().getName());
        return new MessageResponseDTO(
                msg.getId(),
                msg.getConversation().getId(),
                senderDTO,
                msg.getContent(),
                msg.getType(),
                msg.getCreateTime(),
                msg.getClientMsgId(),
                mine);
    }

     /* ---------- 取得歷史訊息（cursor 分頁） ---------- */
    public MessagePageDTO getMessages(Long conversationId, UUID currentUserId, Long before) {
        // 確認使用者是對話成員
        checkMember(conversationId, currentUserId);

        // 多取一筆判斷 hasMore
        List<MessagesEntity> rows = new ArrayList<>(
                mRepo.findPage(conversationId, before, PageRequest.of(0, PAGE_SIZE + 1)));

        boolean hasMore = rows.size() > PAGE_SIZE;
        if (hasMore) {
            rows = new ArrayList<>(rows.subList(0, PAGE_SIZE));
        }

        // DB 回傳為新→舊，畫面需要舊→新
        Collections.reverse(rows);

        List<MessageResponseDTO> items = rows.stream()
                .map(m -> toResponseDTO(currentUserId, m))
                .toList();

        // reverse 後第一筆是最舊的，作為下一頁的 cursor
        Long nextCursor = items.isEmpty() ? null : items.get(0).id();

        return new MessagePageDTO(items, hasMore, nextCursor);

    }

    /* 發送訊息 */
    public MessageResponseDTO sendMessage(Long conversationId, UUID senderId, MessageRequestDTO request) {     
        // 確認使用者是對話成員
        checkMember(conversationId, senderId);

        // 冪等檢查：重送同一個 clientMsgId 直接回既有訊息
        if (request.clientMsgId() != null) {
            Optional<MessagesEntity> existing =
                    mRepo.findByConversationIdAndClientMsgId(conversationId, request.clientMsgId());
            if (existing.isPresent()) {
                return toResponseDTO(senderId, existing.get());
            }
        }

        ConversationEntity conversation = findConversation(conversationId);
        Users sender = findUser(senderId);
        String content = request.content().trim();

        MessagesEntity msg = new MessagesEntity();
        msg.setConversation(conversation);
        msg.setSender(sender);
        msg.setType(ChatType.TEXT);
        msg.setContent(content);
        msg.setClientMsgId(request.clientMsgId());   // null 時由 @PrePersist 補 UUID
        mRepo.save(msg);

        // conversation 為受管理 entity，dirty checking 會自動 flush，不需 cRepo.save()
        conversation.setLastMessage(content);
        conversation.setUpdateTime(Instant.now());

        return toResponseDTO(senderId, msg);
    }

    // TODO:
    public void editMessage() {
    }

    public void deleteMessage() {
    }

}
