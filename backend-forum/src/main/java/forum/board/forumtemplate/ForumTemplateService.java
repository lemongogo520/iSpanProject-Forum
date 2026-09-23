package forum.board.forumtemplate;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.category.Category;
import forum.board.category.CategoryRepository;
import forum.board.userpermission.UserPermissionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForumTemplateService {

    private final CategoryRepository categoryRepository;

    private final BoardRepository boardRepository;

    private final UserPermissionService userPermissionService;

    // 範本回傳 目前暫定 企業 學校 一般、和自訂
    public List<ForumTemplateDto> findAllTemplates() {
        return List.of(
                createCompanyTemplate(),
                createSchoolTemplate(),
                createCommunityTemplate());
    }

    // 沒找到範本 丟例外
    public ForumTemplateDto findTemplate(String code) {
        return findAllTemplates().stream().filter(template -> template.code().equalsIgnoreCase(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到論壇範本：" + code));
    }

    // 企業範本
    private ForumTemplateDto createCompanyTemplate() {
        return new ForumTemplateDto("COMPANY", "企業論壇", "適合公司內部公告與工作交流",
                List.of(
                        new CategoryTemplateDto(
                                "企業公告", 1,
                                List.of(
                                        new BoardTemplateDto("公司公告", "發布公司重要資訊", 1),
                                        new BoardTemplateDto("部門消息", "各部門消息與通知", 2),
                                        new BoardTemplateDto("人事資訊", "發布人事相關資訊", 3))),
                        new CategoryTemplateDto(
                                "工作交流", 2,
                                List.of(
                                        new BoardTemplateDto("專案討論", "交流專案進度與問題", 1),
                                        new BoardTemplateDto("技術交流", "分享技術與工作經驗", 2))),
                        new CategoryTemplateDto(
                                "員工生活", 3,
                                List.of(
                                        new BoardTemplateDto("活動資訊", "發布公司活動資訊", 1),
                                        new BoardTemplateDto("休閒閒聊", "員工日常交流", 2)))));
    }

    // 校園範本
    private ForumTemplateDto createSchoolTemplate() {
        return new ForumTemplateDto("SCHOOL", "校園論壇", "適合學校公告與學生交流",
                List.of(
                        new CategoryTemplateDto(
                                "校務資訊", 1,
                                List.of(
                                        new BoardTemplateDto("校務公告", "發布學校重要公告", 1),
                                        new BoardTemplateDto("課程資訊", "分享選課及課程消息", 2))),
                        new CategoryTemplateDto(
                                "學習交流", 2,
                                List.of(
                                        new BoardTemplateDto("課業討論", "討論課程與作業", 1),
                                        new BoardTemplateDto("學習資源", "分享學習資源", 2))),
                        new CategoryTemplateDto(
                                "校園生活", 3,
                                List.of(
                                        new BoardTemplateDto("社團活動", "分享社團及活動資訊", 1),
                                        new BoardTemplateDto("校園閒聊", "校園生活交流", 2)))));
    }

    // 通用範本
    private ForumTemplateDto createCommunityTemplate() {
        return new ForumTemplateDto(
                "COMMUNITY", "一般社群", "適合興趣交流與一般討論",
                List.of(
                        new CategoryTemplateDto(
                                "站務中心", 1,
                                List.of(
                                        new BoardTemplateDto("站務公告", "發布論壇公告", 1),
                                        new BoardTemplateDto("意見回饋", "提供問題與建議", 2))),
                        new CategoryTemplateDto(
                                "主題交流", 2,
                                List.of(
                                        new BoardTemplateDto("綜合討論", "一般主題交流", 1),
                                        new BoardTemplateDto("新手問答", "新手問題與使用協助", 2)))));
    }

    @Transactional
    public void applyTemplate(String code, ApplyTemplateRequest request, UUID userId) {

        // 權限判斷
        if (!userPermissionService.isSystemAdmin(userId)) {
            throw new IllegalStateException("沒有系統管理員權限");
        }

        // 初始化判斷
        if (categoryRepository.count() > 0) {
            throw new IllegalStateException("論壇已有分類，不能再次套用初始化範本");
        }
        // 檢查前端有沒有選擇至少一個類別
        if (request == null || request.categories() == null || request.categories().isEmpty()) {
            throw new IllegalArgumentException("請至少選擇一個分類");
        }

        ForumTemplateDto template = findTemplate(code);

        for (SelectedCategoryDto selectedCategory : request.categories()) {

            if (selectedCategory.categoryName() == null || selectedCategory.categoryName().isBlank()) {
                throw new IllegalArgumentException("分類名稱不能為空白");
            }
            // 每個分類至少要一個討論版
            if (selectedCategory.boardNames() == null || selectedCategory.boardNames().isEmpty()) {
                throw new IllegalArgumentException("分類至少需要選擇一個討論版：" + selectedCategory.categoryName());
            }
            // 從完整範本中找出被選中的分類
            CategoryTemplateDto categoryTemplate = template.categories().stream()
                    .filter(category -> category.name().equals(
                            selectedCategory.categoryName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("範本中不存在分類：" + selectedCategory.categoryName()));

            if (categoryRepository.findByName(categoryTemplate.name()).isPresent()) {
                throw new IllegalStateException("分類已存在：" + categoryTemplate.name());
            }

            Category category = new Category();
            category.setName(categoryTemplate.name());
            category.setRank(categoryTemplate.rank());

            Category savedCategory = categoryRepository.save(category);

            // 6. 只遍歷前端選中的討論版名稱
            for (String selectedBoardName : selectedCategory.boardNames()) {

                if (selectedBoardName == null || selectedBoardName.isBlank()) {

                    throw new IllegalArgumentException("討論版名稱不能為空白");
                }

                // 從此分類的完整範本中找出討論版
                BoardTemplateDto boardTemplate = categoryTemplate.boards().stream().filter(board -> board.name().equals(
                        selectedBoardName)).findFirst().orElseThrow(() -> new IllegalArgumentException(
                                "分類「" + categoryTemplate.name() + "」不存在討論版：" + selectedBoardName));

                if (boardRepository.findByName(boardTemplate.name()).isPresent()) {
                    throw new IllegalStateException("討論版已存在：" + boardTemplate.name());
                }
                Board board = new Board();
                board.setName(boardTemplate.name());
                board.setDescription(boardTemplate.description());
                board.setRank(boardTemplate.rank());
                board.setCategory(savedCategory);

                boardRepository.save(board);
            }
        }
    }

    public boolean isInitialized() {
        return categoryRepository.count() > 0;
    }
}
