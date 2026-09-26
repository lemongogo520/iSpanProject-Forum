package forum.board.forumtemplate;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AiForumTemplateController {

    private final AiForumTemplateService aiForumTemplateService;

    @PostMapping("/api/forumtemplates/ai-generate")
    public ResponseEntity<?> generate(@Valid @RequestBody AiTemplateRequest request,HttpSession session) {

        UUID userId =(UUID) session.getAttribute("loginUserId");

        if (userId == null) {
            return ResponseEntity .status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        ForumTemplateDto result = aiForumTemplateService.generate(request);

        return ResponseEntity.ok(result);
    }
}