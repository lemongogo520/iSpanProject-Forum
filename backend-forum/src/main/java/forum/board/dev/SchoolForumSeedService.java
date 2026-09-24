package forum.board.dev;

import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.comments.Comments;
import forum.board.comments.CommentsRepository;
import forum.board.post.Posts;
import forum.board.post.PostsRepository;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@Profile("local")
@RequiredArgsConstructor
public class SchoolForumSeedService {
    private static final String BACKEND_URL = "http://localhost:8080";
    private static final String SCHOOL_IMAGE = BACKEND_URL
            + "/uploads/img/9779680c-b73b-45ca-bbb3-abebf10c92a2.png";
    private static final String CHAT_IMAGE = BACKEND_URL
            + "/uploads/img/2ac14d73-1329-48d5-aadd-2a7f5c09d925.png";
    private static final String SPRING_VIDEO = "https://www.youtube-nocookie.com/embed/gJrjgg1KVL4";
    private final BoardRepository boardRepository;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public SeedResult generate() {
        Users manager = getUser("manager01");
        Users user001 = getUser("user001");
        Users user002 = getUser("user002");
        List<Users> authors = List.of(
                manager,
                user001,
                user002);
        List<BoardSeed> boardSeeds = createBoardSeeds();
        int postCount = 0;
        int commentCount = 0;
        int boardIndex = 0;
        for (BoardSeed boardSeed : boardSeeds) {
            Board board = boardRepository
                    .findByName(boardSeed.boardName())
                    .orElse(null);
            if (board == null) {
                System.out.println(
                        "略過不存在的討論版："
                                + boardSeed.boardName());
                boardIndex++;
                continue;
            }
            int articleIndex = 0;
            for (ArticleSeed article : boardSeed.articles()) {
                Users author = authors.get(
                        (boardIndex + articleIndex) % authors.size());
                Posts post = new Posts();
                post.setBoard(board);
                post.setUsers(author);
                post.setPostType(article.postType());
                post.setTitle(article.title());
                post.setContent(buildContent(article));
                post.setViews(
                        80 + boardIndex * 30 + articleIndex * 25);
                post.setIsDeleted(false);
                post = postsRepository.save(post);
                List<CommentSeed> commentSeeds = createCommentSeeds(
                        article.title(),
                        manager,
                        user001,
                        user002);
                commentCount += createComments(
                        post,
                        commentSeeds);
                postCount++;
                articleIndex++;
            }
            boardIndex++;
        }
        return new SeedResult(
                postCount,
                commentCount);
    }

    private List<BoardSeed> createBoardSeeds() {
        return List.of(
                new BoardSeed(
                        "校務公告",
                        List.of(
                                new ArticleSeed(
                                        "新學期開學注意事項",
                                        "請同學於開學前確認課表、教室與學生證狀態，"
                                                + "第一週如需加退選，請依教務處公告時間辦理。",
                                        "討論",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "校園資訊系統維護通知",
                                        "校園資訊系統將於本週六凌晨進行維護，"
                                                + "維護期間選課、成績與學生資料系統將暫停服務。",
                                        "討論",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "校園安全宣導與緊急通報說明",
                                        "請同學熟悉校園緊急聯絡方式，"
                                                + "遇到可疑狀況時應立即通知校安中心。",
                                        "討論",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "期中考週教室開放公告",
                                        "期中考期間圖書館與部分自習教室將延長開放時間，"
                                                + "請共同維護安靜及整潔的讀書環境。",
                                        "討論",
                                        MediaType.BOTH,
                                        SCHOOL_IMAGE))),
                new BoardSeed(
                        "課程資訊",
                        List.of(
                                new ArticleSeed(
                                        "本學期選課與加退選時間",
                                        "初選結果已公布，加退選期間為開學第一週，"
                                                + "熱門課程額滿後將不再加開名額。",
                                        "討論",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "跨領域學程申請方式整理",
                                        "想申請跨領域學程的同學，"
                                                + "請先確認必修學分、申請資格與送件截止時間。",
                                        "心得",
                                        MediaType.IMAGE,
                                        CHAT_IMAGE),
                                new ArticleSeed(
                                        "線上課程平台操作教學",
                                        "本篇整理登入線上課程平台、查看教材、"
                                                + "繳交作業與確認截止時間的方法。",
                                        "攻略",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "如何安排一學期的選修課程",
                                        "選擇課程時除了興趣，也要考慮課程時間、"
                                                + "作業量與畢業學分需求。",
                                        "心得",
                                        MediaType.BOTH,
                                        SCHOOL_IMAGE))),
                new BoardSeed(
                        "課業討論",
                        List.of(
                                new ArticleSeed(
                                        "Java 專題資料庫關聯問題請教",
                                        "在設定文章與討論版的 ManyToOne 關聯時，"
                                                + "遇到 LazyInitializationException，"
                                                + "想請問 DTO 應該在哪一層轉換。",
                                        "問題",
                                        MediaType.IMAGE,
                                        CHAT_IMAGE),
                                new ArticleSeed(
                                        "Vue 路由守衛無限重新導向問題",
                                        "新增初始化頁判斷後發生無限重新導向，"
                                                + "想確認哪些頁面應該排除初始化檢查。",
                                        "問題",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "Spring Boot 分層架構複習",
                                        "本篇討論 Controller、Service、Repository"
                                                + " 各自應負責的工作與常見錯誤。",
                                        "攻略",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "小組專題 Git 衝突處理經驗",
                                        "多人同時修改相同檔案時容易產生衝突，"
                                                + "建議提交前先確認分支、狀態與差異。",
                                        "心得",
                                        MediaType.BOTH,
                                        CHAT_IMAGE))),
                new BoardSeed(
                        "學習資源",
                        List.of(
                                new ArticleSeed(
                                        "整理幾個適合複習 Spring Boot 的資源",
                                        "建議先熟悉 Controller、Service、Repository，"
                                                + "再練習 JPA 關聯與交易管理。",
                                        "心得",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "SQL Server 常用查詢語法整理",
                                        "整理 SELECT、JOIN、GROUP BY、子查詢"
                                                + "與常用聚合函式的基本寫法。",
                                        "攻略",
                                        MediaType.IMAGE,
                                        CHAT_IMAGE),
                                new ArticleSeed(
                                        "Spring Data JPA 入門影片",
                                        "透過影片複習 Repository、衍生查詢方法、"
                                                + "Entity 關聯與分頁查詢。",
                                        "心得",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "前後端專題除錯工具整理",
                                        "介紹瀏覽器開發者工具、Postman、"
                                                + "Spring Boot Console 與 SQL 查詢工具。",
                                        "攻略",
                                        MediaType.BOTH,
                                        SCHOOL_IMAGE))),
                new BoardSeed(
                        "社團活動",
                        List.of(
                                new ArticleSeed(
                                        "程式設計社迎新活動報名",
                                        "本週五下午將舉辦迎新活動，"
                                                + "包含小組解題、專題分享與自由交流。",
                                        "討論",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "校園攝影社外拍活動",
                                        "攝影社將於週末舉辦校園外拍，"
                                                + "沒有專業相機也可以使用手機參加。",
                                        "討論",
                                        MediaType.IMAGE,
                                        CHAT_IMAGE),
                                new ArticleSeed(
                                        "社團成果發表會預告",
                                        "本學期成果發表會將展示各社團的活動成果，"
                                                + "歡迎全校師生到場參觀。",
                                        "討論",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "學生社團聯合招募資訊",
                                        "想加入社團的同學可以先參考活動介紹，"
                                                + "再到現場與各社團幹部交流。",
                                        "討論",
                                        MediaType.BOTH,
                                        SCHOOL_IMAGE))),
                new BoardSeed(
                        "校園閒聊",
                        List.of(
                                new ArticleSeed(
                                        "大家最推薦學校附近哪一家午餐？",
                                        "想找學校附近價格合理又不會等太久的午餐，"
                                                + "希望價格大約落在 100 到 150 元。",
                                        "討論",
                                        MediaType.IMAGE,
                                        CHAT_IMAGE),
                                new ArticleSeed(
                                        "校園裡最適合讀書的地方",
                                        "除了圖書館之外，大家還推薦哪些安靜、"
                                                + "有插座而且適合長時間讀書的地方？",
                                        "討論",
                                        MediaType.IMAGE,
                                        SCHOOL_IMAGE),
                                new ArticleSeed(
                                        "期中考後放鬆影片分享",
                                        "期中考結束後想找一些輕鬆的影片，"
                                                + "歡迎大家分享最近看到的有趣內容。",
                                        "討論",
                                        MediaType.VIDEO,
                                        null),
                                new ArticleSeed(
                                        "新生校園生活經驗分享",
                                        "整理選課、交通、午餐與社團活動的經驗，"
                                                + "希望可以幫助剛入學的新生。",
                                        "心得",
                                        MediaType.BOTH,
                                        CHAT_IMAGE))));
    }

    private String buildContent(ArticleSeed article) {
        StringBuilder content = new StringBuilder();
        content.append("<h2>")
                .append(article.title())
                .append("</h2>");
        content.append("<p>")
                .append(article.description())
                .append("</p>");
        content.append("""
                <h3>內容重點</h3>
                <ul>
                  <li>請先閱讀文章中的相關資訊</li>
                  <li>有問題可以在下方留言討論</li>
                  <li>如有最新消息將持續更新</li>
                </ul>
                """);
        if (article.mediaType() == MediaType.IMAGE
                || article.mediaType() == MediaType.BOTH) {
            content.append("<p><img src=\"")
                    .append(article.imageUrl())
                    .append("\" alt=\"")
                    .append(article.title())
                    .append("\"></p>");
        }
        if (article.mediaType() == MediaType.VIDEO
                || article.mediaType() == MediaType.BOTH) {
            content.append("""
                    <h3>相關影片</h3>
                    <iframe
                      class="ql-video"
                      src="%s"
                      frameborder="0"
                      allowfullscreen>
                    </iframe>
                    """.formatted(SPRING_VIDEO));
        }
        content.append(
                "<p>歡迎大家在留言區分享經驗或提出問題。</p>");
        return content.toString();
    }

    private List<CommentSeed> createCommentSeeds(
            String postTitle,
            Users manager,
            Users user001,
            Users user002) {
        return List.of(
                new CommentSeed(
                        user001,
                        "謝謝分享「" + postTitle
                                + "」，文章內容整理得很清楚。",
                        user002,
                        "我也覺得很實用，有新的資訊可以再一起補充。"),
                new CommentSeed(
                        user002,
                        "想請問這篇內容之後還會持續更新嗎？",
                        manager,
                        "如果有新的消息，我們會更新文章並通知大家。"),
                new CommentSeed(
                        manager,
                        "提醒大家留言時保持友善，"
                                + "如果有相關經驗也歡迎一起分享。",
                        user001,
                        "收到，我會再把這篇文章分享給其他同學。"));
    }

    private int createComments(
            Posts post,
            List<CommentSeed> commentSeeds) {
        int createdCount = 0;
        for (CommentSeed seed : commentSeeds) {
            Comments parent = new Comments();
            parent.setPost(post);
            parent.setUsers(seed.parentAuthor());
            parent.setContent(seed.parentContent());
            parent.setIsDeleted(false);
            parent.setDeletedByModerator(false);
            parent = commentsRepository.save(parent);
            Comments reply = new Comments();
            reply.setPost(post);
            reply.setUsers(seed.replyAuthor());
            reply.setContent(seed.replyContent());
            reply.setParentComment(parent);
            reply.setIsDeleted(false);
            reply.setDeletedByModerator(false);
            commentsRepository.save(reply);
            createdCount += 2;
        }
        return createdCount;
    }

    private Users getUser(String account) {
        return usersRepository
                .findByAccount(account)
                .orElseThrow(() -> new IllegalStateException(
                        "找不到假資料使用者：" + account));
    }

    private enum MediaType {
        IMAGE,
        VIDEO,
        BOTH
    }

    private record ArticleSeed(
            String title,
            String description,
            String postType,
            MediaType mediaType,
            String imageUrl) {
    }

    private record BoardSeed(
            String boardName,
            List<ArticleSeed> articles) {
    }

    private record CommentSeed(
            Users parentAuthor,
            String parentContent,
            Users replyAuthor,
            String replyContent) {
    }

    public record SeedResult(
            int postsCreated,
            int commentsCreated) {
    }
}