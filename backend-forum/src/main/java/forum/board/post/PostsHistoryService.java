package forum.board.post;

import java.util.List;

import org.springframework.stereotype.Service;

import forum.board.users.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostsHistoryService {

    private final PostsHistoryRepository postsHistoryRepo;



    // 新增修改紀錄
    @Transactional
    public void saveUpdateHistory(Posts post, Users user, String newTitle, String newContent) {

        PostsHistory history = new PostsHistory();
        history.setPostId(post.getId());
        history.setUsers(user);
        history.setOldTitle(post.getTitle());
        history.setOldContent(post.getContent());
        history.setNewTitle(newTitle);
        history.setNewContent(newContent);
        history.setAction("UPDATE");
        postsHistoryRepo.save(history);

    }

    // 新增刪除紀錄
    @Transactional
    public void saveDeleteHistory(Posts post, Users user) {

        PostsHistory history = new PostsHistory();
        history.setPostId(post.getId());
        history.setUsers(user);
        history.setOldTitle(post.getTitle());
        history.setOldContent(post.getContent());
        history.setNewTitle(null); // 刪除沒有新標題
        history.setNewContent(null); // 刪除沒有新文章
        history.setAction("DELETE");
        postsHistoryRepo.save(history);
    }

    // 管理員查看某文章被修改幾次
    public List<PostsHistory> findByPostId(Integer postId){

    return postsHistoryRepo.findByPostId(postId);

}

}
