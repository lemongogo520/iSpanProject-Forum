package forum.board.chat.conversation;

import org.springframework.web.bind.annotation.RestController;

import forum.board.chat.dto.ConversationResponseDTO;
import forum.board.friends.dto.SendFriendDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService cService;

    /*
     * /api/chat/conversations/direct
     * 建立/取得與某好友的私訊會話
     */
    @PostMapping("/conversations/direct")
    public ResponseEntity<?> findOrCreateConversation(
            @Valid @RequestBody SendFriendDTO friend,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        ConversationResponseDTO responseDTO = cService.findOrCreateDirectConversation(loginUserId,
                friend.targetUserId());

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /*
     * /api/chat/conversations
     * 私訊列表（含最後一則訊息、未讀數）
     * 看板聊天室 不含未讀數
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        List<ConversationResponseDTO> dto = cService.getMyConversation(loginUserId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /*
     * /api/chat/conversations/{conversationId}/read
     */
    @PatchMapping("/conversations/{conversationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long conversationId,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        cService.markAsRead(conversationId, loginUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ====================看板聊天室===============================
    /*
     * /api/chat/conversations/{conversationId}
     * 看板聊天室才能用conversationId進入
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<?> joinBoardChat(@PathVariable Long conversationId,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        ConversationResponseDTO dto = cService.getOneBoardConversation(conversationId, loginUserId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /*
     * /api/chat/boards/{boardId}
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ConversationResponseDTO> openBoardConversation(@PathVariable Integer boardId,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        ConversationResponseDTO dto = cService.getBoardConversation(boardId, loginUserId);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /*
     * /api/chat/boards/{boardId}/subscribe
     */

    @PostMapping("boards/{boardId}/subscribe")
    public ResponseEntity<ConversationResponseDTO> subscribe(@PathVariable Integer boardId, HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");

        ConversationResponseDTO dto = cService.subscribeBoard(boardId, loginUserId);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /*
     * /api/chat/boards/{boardId}/unsubscribe
     */
    @DeleteMapping("boards/{boardId}/unsubscribe")
    public ResponseEntity<ConversationResponseDTO> unsubscribe(@PathVariable Integer boardId, HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");

        ConversationResponseDTO dto = cService.unsubscribeBoard(boardId, loginUserId);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
