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

    private ConversationResponseDTO toDTO(ConversationEntity c, UUID currentUserId, boolean subscribed,
            Long unreadCount) {
        String title;
        UUID targetUserId = null;
        Integer boardId = null;
        if (c.getType() == ConversationType.DIRECT) {
            Users other = c.getMembers().stream()
                    .map(ConversationMemberEntity::getUser)
                    .filter(u -> !u.getId().equals(currentUserId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("私訊對話缺少對方成員"));
            title = other.getUsersDetail().getName();
            targetUserId = other.getId();

            subscribed = true;
        } else {
            title = c.getBoard().getName();
            boardId = c.getBoard().getId();
        }
        return new ConversationResponseDTO(
                c.getId(),
                c.getType(),
                title,
                targetUserId,
                boardId,
                subscribed,
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
        return toDTO(c, user1Id, true, unreadCount);
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

    // 用conversationId找討論版對話
    public ConversationResponseDTO getOneBoardConversation(Long conversationId, UUID userId) {
        ConversationEntity c = cRepo.findById(conversationId).orElseThrow(() -> new ResourceNotFoundException("找不到對話"));
        if (c.getType() == ConversationType.DIRECT) {
            throw new BusinessException("找不到該對話");
        }
        boolean subscribed = cmRepo.existsByConversationIdAndUserId(conversationId, userId);
        return toDTO(c, userId, subscribed, 0L);
    }

    // 用boardId 找到討論版對話 不存在就新建
    @Transactional
    public ConversationResponseDTO getBoardConversation(Integer boardId, UUID userId) {
        // 找看板
        Board board = bRepo.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("找不到該看板"));

        // 找對話
        ConversationEntity c = cRepo.findByBoard(board).orElseGet(() -> createBoardConversation(board));

        boolean subscribed = cmRepo.existsByConversationIdAndUserId(c.getId(), userId);
        Long unreadCount = (long) 0;

        return toDTO(c, userId, subscribed, unreadCount);
    }

    // 建立討論版對話
    private ConversationEntity createBoardConversation(Board board) {
        ConversationEntity c = new ConversationEntity();
        c.setType(ConversationType.BOARD);
        c.setBoard(board);
        cRepo.save(c);
        return c;
    }

    // 抓所有的對話
    @Transactional
    public List<ConversationResponseDTO> getMyConversation(UUID userId) {
        Map<Long, Long> unreadMap = cmRepo.countUnreadByUser(userId).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
        return cRepo.findAllByUserIdWithMembers(userId).stream()
                .map(c -> toDTO(c, userId, true, unreadMap.getOrDefault(c.getId(), 0L)))
                .sorted(Comparator.comparing(
                        ConversationResponseDTO::updateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    // 更新閱讀時間
    public void markAsRead(Long conversationId, UUID userId) {
        // 找使用者
        Users user = uRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        // 找對話
        ConversationEntity c = cRepo.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該對話"));
        // 找到對應的member
        ConversationMemberEntity member = cmRepo.findByConversationIdAndUserId(c.getId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("不是此對話的成員"));
        // 更新最後閱讀時間
        member.setLastReadTime(Instant.now());
        cmRepo.save(member);

    }

    // 訂閱看板
    @Transactional
    public ConversationResponseDTO subscribeBoard(Integer boardId, UUID userId) {
        Board b = bRepo.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("找不到看板"));
        // 找到看板聊天室 若不存在就建立
        ConversationEntity c = cRepo.findByBoard(b).orElseGet(() -> createBoardConversation(b));

        // 訂閱
        if (!cmRepo.existsByConversationIdAndUserId(c.getId(), userId)) {
            ConversationMemberEntity member = new ConversationMemberEntity();
            member.setConversation(c);
            member.setUser(uRepo.getReferenceById(userId));
            cmRepo.save(member);
        }

        return toDTO(c, userId, true, 0L);
    }

    // 取消訂閱看板
    @Transactional
    public ConversationResponseDTO unsubscribeBoard(Integer boardId, UUID userId) {  
        Board b = bRepo.findById(boardId).orElseThrow(() -> new ResourceNotFoundException("找不到看板"));
        // 找到看板聊天室 若不存在就建立
        ConversationEntity c = cRepo.findByBoard(b).orElseGet(() -> createBoardConversation(b));
        // 取消訂閱
        cmRepo.findByConversationIdAndUserId(c.getId(), userId).ifPresent(cmRepo::delete);

        return toDTO(c, userId, false, 0L);
    }

}
