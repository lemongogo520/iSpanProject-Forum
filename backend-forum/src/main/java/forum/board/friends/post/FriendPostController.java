package forum.board.friends.post;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FriendPostController {

    private final FriendPostService fpService;

    /*
     * GET /api/users/{userId}/posts?size=1
     * 取得某使用者最近發表的文章
     */
    @GetMapping("/{userId}/posts")
    public List<PostSummaryDTO> getRecentPosts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") int size) {
        return fpService.getRecentPosts(userId, size);
    }
}