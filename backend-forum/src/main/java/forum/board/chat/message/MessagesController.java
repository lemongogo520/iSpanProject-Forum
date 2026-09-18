package forum.board.chat.message;

import org.springframework.web.bind.annotation.RestController;

import forum.board.chat.conversation.ConversationService;
import forum.board.chat.dto.MessageRequestDTO;
import forum.board.chat.dto.MessageResponseDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class MessagesController {
    private final MessagesService mService;
    private final ConversationService cService;

    /*
     * /api/chat/{conversationId}/messages
     * 取得某個對話的訊息(50則)
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long conversationId, HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        List<MessageResponseDTO> messagesList = mService.getMessages(conversationId, loginUserId);

        return ResponseEntity.status(HttpStatus.OK).body(messagesList);
    }

    /*
     * /api/chat/message
     * 傳送訊息
     */
    @PostMapping("/message")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @RequestBody MessageRequestDTO request,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        MessageResponseDTO message = mService.sendMessage(loginUserId, request);

        return ResponseEntity.ok(message);
    }

    /*
     * /api/message
     */

}
