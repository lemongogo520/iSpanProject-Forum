package forum.board.chat.message;

import org.springframework.web.bind.annotation.RestController;

import forum.board.chat.dto.MessagePageDTO;
import forum.board.chat.dto.MessageRequestDTO;
import forum.board.chat.dto.MessageResponseDTO;
import forum.board.chat.ws.ChatBroadcaster;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/conversations/{conversationId}/messages")
public class MessagesController {
    private final MessagesService mService;
    private final ChatBroadcaster broadcaster;

    /*
     * /api/chat/conversations/{conversationId}/messages
     * 取得某個對話的訊息
     */
    @GetMapping
    public ResponseEntity<?> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long before,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        MessagePageDTO messagesPage = mService.getMessages(conversationId, loginUserId, before);

        return ResponseEntity.status(HttpStatus.OK).body(messagesPage);
    }

    /*
     * /api/chat/conversations/{conversationId}/message
     * 傳送訊息
     */
    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageRequestDTO request,
            HttpSession session) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        MessageResponseDTO message = mService.sendMessage(conversationId, loginUserId, request);
        broadcaster.messageCreated(message); // 訊息已送入資料庫後
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

}
