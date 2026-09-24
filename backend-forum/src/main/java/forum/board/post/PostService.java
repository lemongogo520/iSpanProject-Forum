package forum.board.post;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.owasp.html.CssSchema;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.boardModerator.BoardModeratorService;
import forum.board.comments.CommentsRepository;
import forum.board.userpermission.UserPermissionService;
import forum.board.users.Users;
import forum.board.users.UsersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostsRepository postRepo;

	private final UsersRepository usersRepo;

	private final BoardRepository boardRepo;

	private final CommentsRepository commentsRepo;

	private final PostsHistoryService historyService;

	private final BoardModeratorService boardModeratorService;

	private final UserPermissionService userPermissionService;

	// 允許文字顏色、背景色，以及顏色函式
	private static final CssSchema POST_CSS = CssSchema.withProperties(
			List.of(
					"color",
					"background-color",
					"rgb()",
					"rgba()",
					"hsl()",
					"hsla()"));

	// 允許文章使用的 HTML 標籤與指定 CSS
	private static final PolicyFactory POST_HTML_POLICY = new HtmlPolicyBuilder()
			.allowElements(
					"p", "br", "span",
					"strong", "b",
					"em", "i",
					"u", "s",
					"h1", "h2", "h3",
					"ul", "ol", "li",
					"blockquote", "pre", "code", "a", "iframe", "img")
			.allowWithoutAttributes("span")
			.allowAttributes("class")
			.matching((elementName, attributeName, value) -> {

				if ("ql-align-center".equals(value)
						|| "ql-align-right".equals(value)
						|| "ql-align-justify".equals(value)) {

					return value;
				}

				return null;
			})
			.onElements("p")

			// Quill 1 的程式碼區塊使用此 class
			.allowAttributes("class")
			.matching(
					(elementName, attributeName, value) -> {
						if ("ql-syntax".equals(value)) {
							return value;
						}
						return null;
					})
			.onElements("pre")

			// Quill 的 YouTube iframe
			.allowAttributes("class")
			.matching((elementName, attributeName, value) -> {
				if ("ql-video".equals(value)) {
					return value;
				}
				return null;
			})
			.onElements("iframe")

			// YouTube 使用 HTTPS，本機圖片開發環境使用 HTTP
			.allowUrlProtocols("https", "http")
			.allowAttributes("href")
			.onElements("a")
			// iframe 的 src 必須通過 YouTube 或 Twitch 網址檢查
			.allowAttributes("src")
			.matching((elementName, attributeName, value) -> sanitizeVideoEmbedUrl(value))
			.onElements("iframe")

			.allowAttributes("frameborder")
			.matching((elementName, attributeName, value) -> {
				if ("0".equals(value)) {
					return value;
				}
				return null;
			})
			.onElements("iframe")

			.allowAttributes("allowfullscreen")
			.onElements("iframe")
			// 文章圖片
			.allowAttributes("src")
			.matching((elementName, attributeName, value) -> sanitizePostImageUrl(value))
			.onElements("img")

			.allowAttributes("alt")
			.onElements("img")

			.allowStyling(POST_CSS)
			.toFactory();

	// 找所有貼文
	public List<Posts> findAll() {

		return postRepo.findByIsDeletedFalse();

	}

	// 查詢全站最新文章
	@Transactional(readOnly = true)
	public List<LatestPostDTO> findLatestPosts(int limit) {
		int safeLimit = Math.max(1, Math.min(limit, 20));
		Pageable pageable = PageRequest.of(0, safeLimit);

		return postRepo
				.findByIsDeletedFalseOrderByCreateTimeDescIdDesc(pageable)
				.stream()
				.map(post -> new LatestPostDTO(
						post.getId(),
						post.getTitle(),
						post.getPostType(),
						post.getBoard().getId(),
						post.getBoard().getName(),
						post.getUsers().getAccount(),
						post.getCreateTime()))
				.toList();
	}

	// 新增貼文
	public Posts create(Posts newPost, UUID userId) {

		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入後再發文！");
		}

		Integer boardId = newPost.getBoard().getId();

		Optional<Board> board = boardRepo.findById(boardId);
		if (board.isEmpty()) {
			throw new RuntimeException("找不到指定的版區！");
		}
		Users newUser = user.get();
		Board newBoard = board.get();
		newPost.setUsers(newUser);
		newPost.setBoard(newBoard);
		newPost.setViews(0); // 假設有瀏覽數欄位

		String postType = newPost.getPostType();

		// 前端還沒傳類型時，新增文章預設為討論
		if (postType == null) {
			postType = "討論";
		}

		newPost.setPostType(validatePostType(postType));

		newPost.setContent(
				validatePostContent(newPost.getContent()));

		return postRepo.save(newPost);
	}

	// 用id找貼文
	public Posts findById(Integer id) {

		Optional<Posts> opPost = postRepo.findByIdAndIsDeletedFalse(id);

		if (opPost.isEmpty()) {
			return null;
		}
		return opPost.get();
	}

	// 取得單篇文章，並轉成 DTO
	@Transactional(readOnly = true)
	public PostDTO findPostDTOById(Integer id) {
		Posts post = findById(id);

		if (post == null) {
			return null;
		}

		return toDTO(post);
	}

	// 將文章 Entity 轉成 DTO
	private PostDTO toDTO(Posts post) {
		PostDTO postDTO = new PostDTO();

		postDTO.setId(post.getId());
		postDTO.setPostType(post.getPostType());
		postDTO.setTitle(post.getTitle());
		postDTO.setContent(sanitizePostContent(post.getContent()));
		postDTO.setViews(post.getViews());
		postDTO.setCreateTime(post.getCreateTime());
		postDTO.setModifyTime(post.getModifyTime());
		postDTO.setIsDeleted(post.getIsDeleted());
		postDTO.setUserName(post.getUsers().getAccount());
		postDTO.setBoardId(post.getBoard().getId());
		postDTO.setUserId(post.getUsers().getId());

		return postDTO;
	}

	// 用title找貼文
	public List<Posts> findPostsByTitle(String title) {
		return postRepo.findByTitleContainingAndIsDeletedFalse(title);
	}

	// 查某個board的文章
	public List<Posts> findByBoardId(Integer boardId) {

		return postRepo.findByBoardIdAndIsDeletedFalse(boardId);

	}

	// 搜尋文章：boardId 沒傳就是全站搜尋
	@Transactional(readOnly = true)
	public Page<PostDTO> searchPosts(String title, Integer boardId, String sortBy, Pageable pageable) {

		String keyword = title == null ? "" : title.trim();

		// 空白關鍵字不查詢全部文章
		if (keyword.isEmpty()) {
			return Page.empty(pageable);
		}

		// 最新文章優先，時間相同時再依 ID 排序
		Pageable sortedPageable = PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				createPostSort(sortBy));

		Page<Posts> posts;

		// 全站搜尋
		if (boardId == null) {
			posts = postRepo.findByTitleContainingAndIsDeletedFalse(keyword, sortedPageable);

			// 指定看板，依按讚數排序
		} else if ("likes".equals(sortBy)) {
			Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
			posts = postRepo.findByBoardIdAndTitleOrderByLikeCount(boardId, keyword, unsortedPageable);

			// 指定看板，使用一般排序
		} else {
			posts = postRepo.findByBoardIdAndTitleContainingAndIsDeletedFalse(boardId, keyword, sortedPageable);
		}
		return posts.map(this::toDTO);
	}

	// 檢查文章類型
	private String validatePostType(String postType) {
		if (postType == null || postType.trim().isEmpty()) {
			throw new IllegalArgumentException("請選擇文章類型");
		}

		String type = postType.trim();

		if (!type.equals("討論")
				&& !type.equals("攻略")
				&& !type.equals("心得")
				&& !type.equals("問題")) {
			throw new IllegalArgumentException("不支援的文章類型");
		}

		return type;
	}

	// 修改文章
	@Transactional
	public Posts update(Integer id, Posts newPost, UUID userId) {

		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入！");
		}

		Optional<Posts> oldPost = postRepo.findById(id);
		if (oldPost.isEmpty()) {
			throw new RuntimeException("修改失敗！找不到指定的文章 ID: " + id);

		}

		Users loginUser = user.get();
		Posts post = oldPost.get();

		// 刪除狀態的文章不能修改
		if (Boolean.TRUE.equals(post.getIsDeleted())) {
			throw new RuntimeException("已刪除的文章不能修改！");
		}

		// 檢查使用者
		if (!post.getUsers().getId().equals(loginUser.getId())) {
			throw new RuntimeException("不能修改別人的文章");
		}
		String safeContent = validatePostContent(newPost.getContent());

		String postType = newPost.getPostType();

		// 前端沒傳文章類型時，保留原本的類型
		if (postType == null) {
			postType = post.getPostType();
		}

		String validatedPostType = validatePostType(postType);

		historyService.saveUpdateHistory(post, loginUser, newPost.getTitle(), safeContent);

		// post更新文章
		post.setTitle(newPost.getTitle());
		post.setContent(safeContent);
		post.setPostType(validatedPostType);
		post.setModifyTime(Instant.now());

		return postRepo.save(post);

	}

	// 刪除文章並寫入到修改文章記錄
	@Transactional
	public void delete(Integer id, UUID userId) {

		Optional<Users> user = usersRepo.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("權限不足：請先登入！");
		}

		Optional<Posts> oldPost = postRepo.findById(id);
		if (oldPost.isEmpty()) {
			throw new RuntimeException("刪除失敗！找不到指定的文章 ID: " + id);
		}

		Users loginUser = user.get();
		Posts post = oldPost.get();

		if (Boolean.TRUE.equals(post.getIsDeleted())) {
			throw new RuntimeException("文章已經刪除！");
		}
		// 文章作者
		boolean isOwner = post.getUsers().getId().equals(loginUser.getId());
		// 找文章討論版
		Integer boardId = post.getBoard().getId();
		// 各版管理員判斷
		boolean isBoardModerator = boardModeratorService.isBoardModerator(boardId, loginUser.getId());
		// 判斷是不是論壇管理員或是系統管理員
		boolean isForumManager = userPermissionService.canManageForum(loginUser.getId());
		// 只有三種權限都不符合才會執行
		if (!isOwner && !isBoardModerator && !isForumManager) {
			throw new SecurityException("權限不足：無法刪除這篇文章");
		}

		// 建立刪除紀錄物件
		historyService.saveDeleteHistory(post, loginUser);

		post.setIsDeleted(true);
		post.setDeletedTime(Instant.now());

		postRepo.save(post);
	}

	// 增加觀看數
	@Transactional
	public int increaseViews(Integer id) {

		return postRepo.increaseViews(id);

	}

	// 分頁功能
	public Page<Posts> findByBoardId(Integer boardId, String sortBy, Pageable pageable) {

		if ("likes".equals(sortBy)) {
			Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
			return postRepo.findByBoardIdOrderByLikeCount(boardId, unsortedPageable);
		}
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				createPostSort(sortBy));

		return postRepo.findByBoardIdAndIsDeletedFalse(boardId, sortedPageable);
	}

	// 只允許 YouTube 與 Twitch Clip 的 HTTPS 嵌入網址
	private static String sanitizeVideoEmbedUrl(String value) {

		if (value == null) {
			return null;
		}

		try {
			URI uri = URI.create(value.trim());

			if (!"https".equalsIgnoreCase(uri.getScheme())
					|| uri.getUserInfo() != null
					|| uri.getPort() != -1
					|| uri.getFragment() != null) {

				return null;
			}

			String host = uri.getHost();

			if (host == null) {
				return null;
			}

			host = host.toLowerCase();
			String path = uri.getPath();

			// YouTube
			boolean isYouTube = "youtube.com".equals(host)
					|| "www.youtube.com".equals(host)
					|| "youtube-nocookie.com".equals(host)
					|| "www.youtube-nocookie.com".equals(host);

			if (isYouTube) {
				if (path == null
						|| !path.matches("/embed/[A-Za-z0-9_-]{11}/?")) {

					return null;
				}

				String videoId = path
						.replaceFirst("^/embed/", "")
						.replaceFirst("/$", "");

				return "https://www.youtube-nocookie.com/embed/" + videoId;
			}

			// Twitch Clip
			if ("clips.twitch.tv".equals(host)
					&& "/embed".equals(path)) {

				String query = uri.getRawQuery();

				if (query == null
						|| !query.matches(
								"clip=[A-Za-z0-9_-]+"
										+ "&parent=[A-Za-z0-9.-]+"
										+ "&autoplay=false")) {

					return null;
				}

				return "https://clips.twitch.tv/embed?" + query;
			}

			return null;

		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	// 只允許後端 uploads/img 資料夾中的文章圖片
	private static String sanitizePostImageUrl(String value) {

		if (value == null) {
			return null;
		}

		try {
			URI uri = URI.create(value.trim());

			String scheme = uri.getScheme();
			String host = uri.getHost();
			String path = uri.getPath();

			// 開發環境允許 http 和 https
			boolean validScheme = "http".equalsIgnoreCase(scheme)
					|| "https".equalsIgnoreCase(scheme);

			// 目前圖片由本機後端提供
			boolean validHost = "localhost".equalsIgnoreCase(host)
					|| "127.0.0.1".equals(host);

			// 後端目前使用 8080
			boolean validPort = uri.getPort() == 8080;

			// 只允許 uploads/img 中指定格式的檔案
			boolean validPath = path != null
					&& path.matches(
							"/uploads/img/[a-zA-Z0-9_-]+\\.(jpg|jpeg|png|gif|webp)");

			if (!validScheme
					|| !validHost
					|| !validPort
					|| !validPath) {

				return null;
			}

			return uri.toString();

		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	// 清理文章 HTML，只保留允許的標籤、屬性與文字顏色樣式
	private String sanitizePostContent(String content) {
		if (content == null) {
			return "";
		}

		return POST_HTML_POLICY.sanitize(content);
	}

	// 清理後再檢查是否還有文字
	private String validatePostContent(String content) {
		String safeContent = sanitizePostContent(content);

		String plainText = Jsoup.parse(safeContent)
				.text()
				.replace('\u00A0', ' ')
				.strip();

		boolean hasEmbeddedVideo = safeContent.contains(
				"src=\"https://www.youtube-nocookie.com/embed/")
				|| safeContent.contains(
						"src=\"https://clips.twitch.tv/embed?");

		if (plainText.isEmpty() && !hasEmbeddedVideo) {
			throw new IllegalArgumentException("文章內容不能為空白");
		}

		return safeContent;
	}

	// 依前端選項建立文章排序規則
	private Sort createPostSort(String sortBy) {

		if ("views".equals(sortBy)) {
			// 瀏覽數相同時，較新的文章排前面
			return Sort.by(
					Sort.Order.desc("views"),
					Sort.Order.desc("createTime"),
					Sort.Order.desc("id"));
		}
		// 預設：最新發布
		return Sort.by(
				Sort.Order.desc("createTime"),
				Sort.Order.desc("id"));
	}

	// 查全站熱門文章
	public List<HotPostDTO> findHotPosts(int limit) {

		int safeLimit = Math.max(1, Math.min(limit, 20));

		Pageable pageable = PageRequest.of(0, safeLimit);

		return postRepo
				.findByIsDeletedFalseOrderByViewsDescCreateTimeDesc(pageable)
				.stream()
				.map(post -> new HotPostDTO(
						post.getId(),
						post.getTitle(),
						post.getUsers().getId(),
						post.getUsers().getAccount(),
						post.getBoard().getId(),
						post.getBoard().getName(),
						post.getPostType(),
						extractFirstImageUrl(post.getContent()),
						post.getViews(),
						commentsRepo.countByPostIdAndIsDeletedFalse(post.getId()),
						post.getCreateTime()))
				.toList();
	}

	private String extractFirstImageUrl(String content) {
		if (content == null || content.isBlank()) {
			return null;
		}
		var firstImage = Jsoup.parseBodyFragment(content).selectFirst("img[src]");
		if (firstImage == null) {
			return null;
		}
		String imageUrl = firstImage.attr("src");
		return imageUrl.isBlank() ? null : imageUrl;
	}
}
