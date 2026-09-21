package forum.board.friends.post;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import forum.board.post.Posts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendPostService {

    private final FriendPostQueryRepository pRepo;

    private static final int EXCERPT_LENGTH = 80;
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]*>");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public List<PostSummaryDTO> getRecentPosts(UUID userId, int size) {
        int safeSize = Math.min(Math.max(size, 1), 20); // 限制範圍
        return pRepo.findRecentByAuthor(userId, PageRequest.of(0, safeSize))
                .stream().map(this::toSummary).toList();
    }

    private static final Pattern IMG_SRC = Pattern.compile("<img[^>]+src\\s*=\\s*[\"']([^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE);

    private static String firstImage(String html) {
        if (html == null)
            return null;
        Matcher m = IMG_SRC.matcher(html);
        while (m.find()) {
            String src = m.group(1);
            if (!src.startsWith("data:"))
                return src; // 跳過內嵌的 base64 圖片
        }
        return null;
    }

    private PostSummaryDTO toSummary(Posts post) {
        String content = post.getContent();
        return new PostSummaryDTO(
                post.getId(),
                post.getTitle(),
                excerpt(content, EXCERPT_LENGTH),
                firstImage(content), 
                post.getPostType(),
                post.getBoard().getId(),
                post.getBoard().getName(),
                post.getCreateTime());
    }

    private static String excerpt(String content, int max) {
        if (content == null)
            return "";
        String plain = HTML_TAG.matcher(content).replaceAll("");
        plain = WHITESPACE.matcher(plain).replaceAll(" ").strip();
        return plain.codePointCount(0, plain.length()) <= max
                ? plain
                : plain.substring(0, plain.offsetByCodePoints(0, max)) + "…";
    }
}
