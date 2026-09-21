package forum.board.chat.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.chat.conversation.ConversationEntity.ConversationType;
import forum.board.chat.dto.ConversationResponseDTO;
import forum.board.chat.message.MessagesRepository;
import forum.board.friends.exception.BusinessException;
import forum.board.friends.exception.ResourceNotFoundException;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository cRepo;
    private final ConversationMemberRepository cmRepo;
    private final MessagesRepository mRepo;
    private final UsersRepository uRepo;
    private final BoardRepository bRepo;

    private ConversationResponseDTO toDTO(ConversationEntity c, UUID currentUserId, Long unreadCount) {
        String title;
        String avatar = null;
        UUID targetUserId = null;
        if (c.getType() == ConversationType.DIRECT) {
            Users other = c.getMembers().stream()
                    .map(ConversationMemberEntity::getUser)
                    .filter(u -> !u.getId().equals(currentUserId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("私訊對話缺少對方成員"));
            title = other.getUsersDetail().getName();
            avatar = other.getUsersDetail().getPhoto();
            targetUserId = other.getId();
        } else {
            title = c.getBoard().getName();
            avatar = null;
        }
        return new ConversationResponseDTO(
                c.getId(),
                c.getType(),
                title,
                avatar,
                targetUserId,
                c.getLastMessage(),
                c.getCreateTime(),
                c.getUpdateTime(),
                unreadCount);
    }

    // 找到私訊對話 找不到就新建
    @Transactional
    public ConversationResponseDTO findOrCreateDirectConversation(UUID user1Id, UUID user2Id) {
        if (user1Id.equals(user2Id)) {
            throw new BusinessException("不可以跟自己聊天");
        }

        Users user1 = uRepo.findById(user1Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        Users user2 = uRepo.findById(user2Id).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        // 先找兩人有沒有聊過天 沒有就建立 conversation+conversation member
        ConversationEntity c = cRepo.findDirectConversation(user1Id, user2Id)
                .orElseGet(() -> createDirectConversation(user1, user2));
        ConversationMemberEntity member = cmRepo.findByConversationIdAndUserId(c.getId(), user1.getId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到該對話成員"));
        Long unreadCount = mRepo.countUnreadMessages(c.getId(), member.getLastReadTime(), user1.getId());
        return toDTO(c, user1Id, unreadCount);
    }

    // 新建私訊對話
    private ConversationEntity createDirectConversation(Users user1, Users user2) {
        ConversationEntity c = new ConversationEntity();
        c.setType(ConversationType.DIRECT);
        c.setBoard(null);

        ConversationMemberEntity m1 = new ConversationMemberEntity();
        m1.setConversation(c);
        m1.setUser(user1);
        ConversationMemberEntity m2 = new ConversationMemberEntity();
        m2.setConversation(c);
        m2.setUser(user2);

        c.setMembers(new ArrayList<>(List.of(m1, m2)));
        cRepo.save(c);
        cmRepo.save(m1);
        cmRepo.save(m2);

        return c;
    }

    // 找到討論版對話 不存在就新建
    @Transactional
    public ConversationResponseDTO findOrCreateBoardConversation(UUID userId, Integer boardId) {
        Users user = uRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        // 先找看板在不在
        Board board = bRepo.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("找不到該看板"));

        // 先找看板對話存不存在
        ConversationEntity c = cRepo.findByBoard(board).orElseGet(() -> createBoardConversation(board));

        // 確認使用者是否已經是聊天室成員 不是就加入
        if (!isMember(userId, c.getId())) {
            ConversationMemberEntity m = new ConversationMemberEntity();
            m.setConversation(c);
            m.setUser(user);
            cmRepo.save(m);
        }
        ConversationMemberEntity member = cmRepo.findByConversationIdAndUserId(c.getId(), user.getId()).orElseThrow();
        Long unreadCount = mRepo.countUnreadMessages(c.getId(), member.getLastReadTime(), user.getId());

        return toDTO(c, userId, unreadCount);
    }

    // 建立討論版對話
    private ConversationEntity createBoardConversation(Board board) {
        ConversationEntity c = new ConversationEntity();
        c.setType(ConversationType.BOARD);
        c.setBoard(board);
        cRepo.save(c);
        return c;
    }

    // 檢查是不是對話成員
    private boolean isMember(UUID userId, Long conversationId) {
        return cmRepo.existsByConversationIdAndUserId(conversationId, userId);
    }

    // 抓所有的對話
    @Transactional
    public List<ConversationResponseDTO> getMyConversation(UUID userId) {
        Map<Long, Long> unreadMap = cmRepo.countUnreadByUser(userId).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
        return cRepo.findAllByUserIdWithMembers(userId).stream()
                .map(c -> toDTO(c, userId, unreadMap.getOrDefault(c.getId(), 0L)))
                .sorted(Comparator.comparing(
                        ConversationResponseDTO::updateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public void markAsRead(Long conversationId, UUID userId) {
        // 找到這個使用者
        Users user = uRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        // 找到對話
        ConversationEntity c = cRepo.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該對話"));
        // 找到對應的member
        ConversationMemberEntity member = cmRepo.findByConversationIdAndUserId(c.getId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("不是此對話的成員"));
        // 更新最後閱讀時間
        member.setLastReadTime(Instant.now());
        cmRepo.save(member);

    }

}
