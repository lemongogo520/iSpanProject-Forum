package forum.board.comments;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import forum.board.boardModerator.BoardModeratorService;
import forum.board.post.Posts;
import forum.board.post.PostsRepository;
import forum.board.userpermission.UserPermissionService;
import forum.board.users.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepo;

    private final PostsRepository postsRepo;

    private final CommentsHistoryService commentsHistoryService;

    private final BoardModeratorService boardModeratorService;

    private final UserPermissionService userPermissionService;

    // 新增留言
    public Comments createComments(Integer postId, Integer parentCommentId, Comments newComments, Users user) {

        // 檢查有沒有登入
        if (user == null) {
            throw new RuntimeException("權限不足：請先登入後再留言！");
        }
        // 驗證留言內容是否空白
        newComments.setContent(validateContent(newComments.getContent()));

        // 檢查是否有這篇post
        Optional<Posts> opPost = postsRepo.findByIdAndIsDeletedFalse(postId);
        if (opPost.isEmpty()) {
            throw new RuntimeException("新增留言失敗！找不到指定的文章 ID: " + postId);
        }

        // 如果有傳入 parentCommentId，代表這是一則子留言
        if (parentCommentId != null) {
            Optional<Comments> parentCommentOpt = commentsRepo.findById(parentCommentId);
            if (parentCommentOpt.isEmpty()) {
                throw new RuntimeException("新增留言失敗！找不到欲回覆的留言 ID: " + parentCommentId);
            }
            Comments parentComment = parentCommentOpt.get();

            if (Boolean.TRUE.equals(
                    parentComment.getIsDeleted())) {
                throw new RuntimeException("新增留言失敗！不能回覆已刪除的留言！");
            }
            if (!parentComment.getPost().getId().equals(postId)) {

                throw new RuntimeException("新增留言失敗！目標留言不屬於此文章！");
            }
            newComments.setParentComment(parentComment);
        }
        Posts post = opPost.get();
        newComments.setPost(post);
        newComments.setUsers(user);

        return commentsRepo.save(newComments);
    }

    // 查詢某篇post的留言有分頁
    public Page<Comments> findByPostId(Integer postId, Pageable pageable) {
        return commentsRepo.findByPostIdAndParentCommentIsNull(postId, pageable);
    }

    // 查詢某則留言底下的所有子留言
    public List<Comments> findChildComments(Integer parentId) {
        return commentsRepo.findByParentCommentIdOrderByCreateTimeAsc(parentId);
    }

    // 修改留言
    @Transactional
    public Comments update(Integer commentId, String newContent, Users user) {

        // 檢查有沒有登入
        if (user == null) {
            throw new RuntimeException("權限不足：請先登入！");
        }

        // 找出原留言
        Optional<Comments> opComment = commentsRepo.findById(commentId);
        if (opComment.isEmpty()) {

            throw new RuntimeException("修改失敗！找不到指定的留言 ID: " + commentId);
        }

        Comments comment = opComment.get();

        // 如果已經刪除，不允許修改
        if (Boolean.TRUE.equals(comment.getIsDeleted())) {
            throw new RuntimeException("留言已經刪除！");
        }
        // 權限檢查：只能修改自己的留言
        if (!comment.getUsers().getId().equals(user.getId())) {
            throw new RuntimeException("權限不足：不能修改別人的留言！");
        }

        String validatedContent = validateContent(newContent);
        // 做新增修改紀錄動作
        commentsHistoryService.updateHistory(comment, validatedContent, user);

        // 更新留言
        comment.setContent(validatedContent);
        comment.setModifyTime(Instant.now());

        return commentsRepo.save(comment);
    }

    // 刪除留言
    @Transactional
    public void delete(Integer commentId, Users user, String reason) {

        // 檢查有沒有登入
        if (user == null) {
            throw new RuntimeException("權限不足：請先登入！");
        }

        // 找出原留言
        Optional<Comments> opComment = commentsRepo.findById(commentId);
        if (opComment.isEmpty()) {
            throw new RuntimeException("刪除失敗！找不到指定的留言 ID: " + commentId);
        }

        Comments comment = opComment.get();

        // 如果已經刪除，不允許再次刪除
        if (Boolean.TRUE.equals(comment.getIsDeleted())) {
            throw new RuntimeException("留言已經刪除！");
        }
        // 是否為留言作者
        boolean isOwner = comment.getUsers().getId().equals(user.getId());
        // 留言所屬文章的討論版
        Integer boardId = comment.getPost().getBoard().getId();
        // 是否為該版版主
        boolean isBoardModerator = boardModeratorService.isBoardModerator(boardId, user.getId());
        // 是否為論壇管理員或系統管理員
        boolean isForumManager = userPermissionService.canManageForum(user.getId());
        // 三種身分都不符合才禁止刪除
        if (!isOwner && !isBoardModerator && !isForumManager) {
            throw new SecurityException("權限不足：無法刪除這則留言");
        }

        boolean isModeratorDelete = !isOwner && (isBoardModerator || isForumManager);

        if (isModeratorDelete) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new RuntimeException("管理員刪除留言時必須選擇原因");
            }
            reason = reason.trim();
        }
        // 寫入歷史紀錄
        commentsHistoryService.recordDeleteHistory(comment, user);
        // 軟刪除狀態
        comment.setDeletedByModerator(isModeratorDelete);
        comment.setDeleteReason(isModeratorDelete ? reason : null);
        comment.setIsDeleted(true);
        comment.setDeletedTime(Instant.now());
        // 刪除留言
        commentsRepo.save(comment);
    }

    // 留言空白判斷
    private String validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("留言內容不能為空！");
        }

        return content.trim();
    }

}
