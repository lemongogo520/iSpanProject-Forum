package forum.board.chat.conversation;

import org.springframework.web.bind.annotation.RestController;

import forum.board.chat.dto.ConversationResponseDTO;
import forum.board.friends.dto.SendFriendDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping ("/api/chat")
@RequiredArgsConstructor 
public class ConversationController {

    private final ConversationService cService;


    /*
    /api/chat/direct
    */
    @PostMapping("/direct")
    public ResponseEntity<?> findOrCreateConversation(@RequestBody SendFriendDTO friend,HttpSession session) {
        UUID loginUserId = (UUID)session.getAttribute("loginUserId");	
        ConversationResponseDTO responseDTO = cService.findOrCreateDirectConversation(loginUserId, friend.targetUserId());

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }


//     /*
//     /api/chat/conversations
//     */
//    @GetMapping("/conversations")
//    public String getConversations(@RequestParam String param) {
//        return new String();
//    }

//    /* 
//    /api/chat/{conversationId}
//    */
//   @GetMapping("{conversationId}")
//   public String getMethodName(@PathVariable long conversationId) {
//       return new String();
//   }
  
   

    


    
    
}
