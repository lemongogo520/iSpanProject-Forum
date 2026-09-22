package forum.board.forumtemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class ForumTemplateController {
    private final ForumTemplateService forumTemplateService;

    @GetMapping("/api/forumtemplates")
    public List<ForumTemplateDto> findAllTemplates() {
        return forumTemplateService.findAllTemplates();
    }

    // 現在範本數量少可以直接在前端就呼叫所有範本如果之後多在選擇要的範本再要求後端傳送相對應的範本給前端
    // @GetMapping("/api/forumtemplates/{code}")
    // public ForumTemplateDto findTemplate(@PathVariable String code) {
    // return forumTemplateService.findTemplate(code);
    // }

    // 套用範本
    @PostMapping("/api/forumtemplates/{code}/apply")
    public ResponseEntity<?> applyTemplate(@PathVariable String code,@RequestBody ApplyTemplateRequest request, HttpSession hs) {

        UUID userId = (UUID) hs.getAttribute("loginUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        forumTemplateService.applyTemplate(code,request,userId);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "論壇範本套用成功"));
    }

}
