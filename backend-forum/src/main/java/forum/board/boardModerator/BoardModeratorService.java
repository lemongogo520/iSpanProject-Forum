package forum.board.boardModerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.board.Board;
import forum.board.board.BoardRepository;
import forum.board.users.Users;
import forum.board.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardModeratorService {

    private final BoardModeratorRepository boardModeratorRepo;

    private final BoardRepository boardRepo;

    private final UsersRepository usersRepo;

    private BoardModeratorDTO toDTO(BoardModerator moderator) {

        Optional<Users> creatorUser = usersRepo.findById(moderator.getCreator());
        if (creatorUser.isEmpty()) {
            throw new IllegalArgumentException("找不到建立者資料");
        }

        BoardModeratorDTO dto = new BoardModeratorDTO();
        dto.setId(moderator.getId());
        dto.setBoardId(moderator.getBoard().getId());
        dto.setBoardName(moderator.getBoard().getName());
        // 被設定為版主的人 一般使用者
        dto.setUserId(moderator.getUser().getId());
        dto.setAccount(moderator.getUser().getAccount());
        // 執行設定的人 討論版管理員
        dto.setCreateTime(moderator.getCreateTime());
        dto.setCreatorId(moderator.getCreator());
        dto.setCreatorAccount(creatorUser.get().getAccount());

        return dto;
    }

    // 取得某討論版的全部版主
    @Transactional(readOnly = true)
    public List<BoardModeratorDTO> findByBoardId(Integer boardId) {

        return boardModeratorRepo.findByBoard_IdOrderByIdAsc(boardId).stream().map(this::toDTO).toList();
    }

    // 新增版主
    @Transactional 
    public BoardModeratorDTO addModerator(Integer boardId, UUID userId, UUID creatorId) {

        if (boardId == null) {
            throw new IllegalArgumentException("討論版 ID 不可為空");
        }

        if (userId == null) {
            throw new IllegalArgumentException("使用者 ID 不可為空");
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("找不到登入者資料");
        }
        Optional<Users> creator = usersRepo.findById(creatorId);

        if (creator.isEmpty()) {
            throw new IllegalArgumentException("找不到建立者資料");
        }
        if (boardModeratorRepo.existsByBoard_IdAndUser_Id(boardId, userId)) {
            throw new IllegalArgumentException("此使用者已經是該討論版的版主");
        }
        Optional<Board> board = boardRepo.findById(boardId);
        if (board.isEmpty()) {
            throw new IllegalArgumentException("找不到討論版");
        }
        Optional<Users> user = usersRepo.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("找不到使用者");
        }

        BoardModerator moderator = new BoardModerator();
        moderator.setBoard(board.get());
        moderator.setUser(user.get());
        // 從登入 Session 傳進來的論壇管理員 UUID
        moderator.setCreator(creatorId);
        BoardModerator savedModerator = boardModeratorRepo.save(moderator);
        return toDTO(savedModerator);
    }

    // 移除版主
    @Transactional
    public void removeModerator(Integer boardId, UUID targetUserId) {

        boolean exists = boardModeratorRepo.existsByBoard_IdAndUser_Id(boardId, targetUserId);

        if (!exists) {
            throw new IllegalArgumentException("此使用者不是該討論版的版主");
        }
        boardModeratorRepo.deleteByBoard_IdAndUser_Id(boardId, targetUserId);
    }

    // 判斷指定使用者是不是該版版主
    @Transactional(readOnly = true)
    public boolean isBoardModerator(Integer boardId, UUID userId) {
        if (boardId == null || userId == null) {
            return false;
        }
        return boardModeratorRepo.existsByBoard_IdAndUser_Id(boardId, userId);
    }

}
